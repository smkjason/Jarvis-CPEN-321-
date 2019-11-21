package com.example.jarvis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import io.socket.client.Socket;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "CreateEvent";

    //Server Stuff
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Socket mSocket;
    private GoogleSignInAccount acct;

    //XML
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private EditText nameofEvent;
    private TextView peopleAtEvent;
    private Button create;
    private Toolbar mtoolbar;
    private TextView mDisplaydate;
    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //Event Details
    private Date deadline;
    private SimpleDateFormat deadlineformat;
    private Time length;
    private String eventid;
    private ArrayList<String> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        //deadline format
        deadlineformat = new SimpleDateFormat("yyyy-MM-dd");

        //TextEdits
        nameofEvent = findViewById(R.id.name_of_event);
        mDisplaydate = findViewById(R.id.tvDate);
        peopleAtEvent = findViewById(R.id.add_people_to_event);

        //Button(s)
        create = findViewById(R.id.make_event);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mSocket = ((jarvis) this.getApplication()).getmSocket();
        if(!mSocket.connected()) {
            Toast.makeText(CreateEvent.this, "Can't connect...", Toast.LENGTH_LONG).show();
        }


        mtoolbar = findViewById(R.id.create_event_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create an Event");

        acct = GoogleSignIn.getLastSignedInAccount(this);

        peopleAtEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFriends.class);
                intent.putExtra("Added Friends", friendList);
                startActivityForResult(intent,101);
            }
        });

        mDisplaydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eventName = nameofEvent.getText().toString();
                if(eventName.isEmpty()){
                    Toast.makeText(CreateEvent.this, "What is the name of this event?", Toast.LENGTH_LONG).show();
                }
                else if(deadline == null){
                    Toast.makeText(CreateEvent.this, "When does this needs to happen by?", Toast.LENGTH_LONG).show();
                }
                else if(length == null || length.equals(new Time(0))){
                    Toast.makeText(CreateEvent.this, "The length of the event is invalid.", Toast.LENGTH_LONG).show();
                }
                else {
                    new makeNewEvent(eventName, deadline, length, friendList).execute();
                    Intent intent = new Intent(CreateEvent.this, SelectTime.class);
                    startActivity(intent);
                }

            }
        });

    }

    private void showDatePickerDialog() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(CreateEvent.this, "Clicked!", Toast.LENGTH_LONG).show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEvent.this,
                R.style.Theme_AppCompat_DayNight_Dialog,
                CreateEvent.this,
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + month + "-" + dayOfMonth;
        mDisplaydate.setText(date);
        try {
            deadline = deadlineformat.parse(date);
        }catch(ParseException e){
            Log.e(TAG, "dateformat exception", e);
        }
    }

    private class makeNewEvent extends AsyncTask<Void, Void, JSONObject> {

        String eventName;
        ArrayList<String> email;
        Date deadline;
        Time length;


        makeNewEvent(String eventName, Date date, Time length, ArrayList<String> emails) {
            this.eventName = eventName;
            this.email = emails;
            this.deadline = date;
            this.length = length;
        }

        @Override
        protected JSONObject doInBackground(Void... v) {
            JSONObject createresponse = new JSONObject();
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/");
                Log.d(TAG, "The year: " + year + "\nThe month: " + month + "\nThe day" + day);
                JSONObject json = new JSONObject();
                json.put("name", eventName);
                json.put("deadline", deadline);
                json.put("length", length);
                json.put("invitees", friendList);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                createresponse = new JSONObject(responseBody);
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return createresponse;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
//            Toast.makeText(CreateEvent.this, "Event Successfully Created", Toast.LENGTH_LONG).show();
            super.onPostExecute(jsonObject);
            try {
                if (!jsonObject.getString("status").equals("success")) {
                    Toast.makeText(CreateEvent.this, "Something went wrong with invitation", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(CreateEvent.this, "Invitation sent!", Toast.LENGTH_LONG).show();
                    eventid = jsonObject.getString("id");
                }
            }catch (JSONException e){
                Log.e(TAG, "JSONException caught", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            ArrayList<String> newList = data.getStringArrayListExtra("Added Friends");
            for (int i = 0; i < newList.size(); i++) {
                if (!friendList.contains(newList.get(i))) {
                    friendList.add(newList.get(i));
                }
            }
            String People = "";
            for (int i = 0; i < friendList.size(); i++) {
                if (i != 0) {
                    People += "\n";
                }
                People += friendList.get(i);
            }
            peopleAtEvent.setText(People);
            Log.d("Back", "Successful");
            Log.d("Back", String.valueOf(requestCode));
            Log.d("Back", String.valueOf(resultCode));
        }
    }
}
