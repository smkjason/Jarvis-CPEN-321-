package com.example.jarvis;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import io.socket.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "CreateEvent";

    //Server Stuff
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Socket mSocket;

    //XML
    private EditText nameofEvent;
    private TextView peopleAtEvent;
    private Button invite;
    private Toolbar mtoolbar;
    private TextView mDisplaydate;
    private Calendar calendar;
    private int year, month, day, hour, minute;
    private TextView lengthshow;

    //Event Details
    private Date deadline;
    private SimpleDateFormat deadlineformat;
    private SimpleDateFormat lengthformat;
    private ArrayList<String> friendList = new ArrayList<>();
    private String timeselected;
    private String date_selected;

    //User
    private String user_email;
    private String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        if(getIntent().hasExtra("idToken")){
            this.idToken = getIntent().getStringExtra("idToken");
        }

        //deadline format
        deadlineformat = new SimpleDateFormat("yyyy-MM-dd");
        lengthformat = new SimpleDateFormat("hh:mm");

        //TextEdits
        nameofEvent = findViewById(R.id.name_of_event);
        mDisplaydate = findViewById(R.id.tvDate);
        peopleAtEvent = findViewById(R.id.add_people_to_event);
        lengthshow = findViewById(R.id.tvShowLength);

        //Button(s)
        invite = findViewById(R.id.make_event);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        user_email = currentUser.getEmail();

        //initial eventid

        mSocket = ((jarvis) this.getApplication()).getmSocket();
        if(!mSocket.connected()) {
            Toast.makeText(CreateEvent.this, "Can't connect...", Toast.LENGTH_LONG).show();
        }


        mtoolbar = findViewById(R.id.create_event_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create an Event");



        mDisplaydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        lengthshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePiccckerDialog();
            }
        });

        peopleAtEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchFriends.class);
                intent.putExtra("Added Friends", friendList);
                startActivityForResult(intent,101);
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eventName = nameofEvent.getText().toString();
                if(eventName.isEmpty()){
                    Toast.makeText(CreateEvent.this, "What is the name of this event?", Toast.LENGTH_LONG).show();
                }
                else if(date_selected == null){
                    Toast.makeText(CreateEvent.this, "When does this needs to happen by?", Toast.LENGTH_LONG).show();
                }
                else if(timeselected == null){
                    Toast.makeText(CreateEvent.this, "The length of the event is invalid.", Toast.LENGTH_LONG).show();
                }
                else {
                    new makeNewEvent(eventName, date_selected, timeselected, friendList, user_email, idToken).execute();
                }
            }
        });
    }

    private void showTimePiccckerDialog() {
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateEvent.this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeselected = hourOfDay + ":" + minute;
                lengthshow.setText(timeselected);
                try {
                    Date length = lengthformat.parse(timeselected);
                }catch (ParseException e){
                    Log.e(TAG, "parse exception caught", e);
                }
            }
        },  hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
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
        date_selected = year + "-" + ++month + "-" + dayOfMonth;
        mDisplaydate.setText(date_selected);
        try {
            deadline = deadlineformat.parse(date_selected);
        }catch(ParseException e){
            Log.d(TAG, "Deadline: " + deadline);
            Log.e(TAG, "dateformat exception", e);
        }
    }

    private class makeNewEvent extends AsyncTask<Void, Void, JSONObject> {

        String eventName;
        ArrayList<String> emails;
        String deadline;
        String length;
        String admin_email;
        String idToken;


        makeNewEvent(String eventName, String date, String length, ArrayList<String> emails, String email, String idToken) {
            this.eventName = eventName;
            this.emails = emails;
            this.deadline = date;
            this.length = length;
            admin_email = email;
            this.idToken = idToken;
        }

        @Override
        protected JSONObject doInBackground(Void... v) {
            JSONObject createresponse = new JSONObject();
            try {
                HttpClient httpClient = new DefaultHttpClient();

                Log.d(TAG, "email: " + admin_email);Log.d(TAG, "idToken: " + idToken);Log.d(TAG, "deadline: " + deadline);Log.d(TAG, "length: " + length);

                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + admin_email + "/events/");
                Log.d(TAG, "\nThe year: " + year + "\nThe month: " + month + "\nThe day" + day);
                JSONObject json = new JSONObject();
                JSONArray jsonArray = new JSONArray(friendList);
                json.put("name", eventName);
                json.put("deadline", deadline);
                json.put("length", length);
                json.put("invitees", jsonArray);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Authorization", "Bearer " + idToken);
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                createresponse = new JSONObject(responseBody);
                Log.i(TAG, "Created... " + responseBody);
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
            super.onPostExecute(jsonObject);
            if (!jsonObject.has("id")) {
                Toast.makeText(CreateEvent.this, "Something went wrong with invitation", Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(CreateEvent.this, "Invitations sent!", Toast.LENGTH_LONG).show();
                finish();
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