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
import org.json.JSONObject;

import java.io.IOException;
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

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Socket mSocket;
    private GoogleSignInAccount acct;

    private TextView mDisplaydate;
    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText nameofEvent;

    private ArrayList<String> friendList = new ArrayList<>();
    private TextView peopleAtEvent;

    private Button create;

    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);


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
        getSupportActionBar().setTitle("Create_Event");

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
                new makeNewEvent(eventName, friendList).execute();
                Intent intent = new Intent(CreateEvent.this, SelectTime.class);
                startActivity(intent);

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
        String date = month + " / " + dayOfMonth + " / " + year;
        mDisplaydate.setText(date);
    }

    private class makeNewEvent extends AsyncTask<Void, Void, Void> {

        String eventName;
        ArrayList<String> email;

        makeNewEvent(String eventName, ArrayList<String> emails) {
            this.eventName = eventName;
            this.email = emails;
        }

        @Override
        protected Void doInBackground(Void... v) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/");
                Log.d(TAG, "The year: " + year + "\nThe month: " + month + "\nThe day" + day);
                JSONObject json = new JSONObject();
                json.put("name", eventName);
                json.put("year", year);
                json.put("month", month);
                json.put("day", day);
                json.put("attendees", friendList);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return null;
        }


        protected void onPostExecute() {
            //Maybe Implemented
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(CreateEvent.this, "Event Successfully Created", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
            //Maybe Implemented
        }
    }

    private class CreateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            String responseBody = "";
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            try {
                JSONObject json = new JSONObject();
                ArrayList<String> recurrence = new ArrayList<String>();
                json.put("status", "test_status");
                json.put("created", currentTime);
                json.put("updated", currentTime);
                json.put("location", "test_location");
                json.put("colorId", "test_colorId");
                json.put("creatorEmail", acct.getEmail());
                json.put("start", "test_start");
                json.put("end", "test_end");
                json.put("attendees", acct.getEmail());
                json.put("recurrence", recurrence);

                HttpPost request = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/" + "user" + "/" + acct.getEmail() + "/events");
                StringEntity params = new StringEntity(json.toString());
                request.addHeader("Authorization", "Bearer " + acct.getIdToken());
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                responseBody = EntityUtils.toString(response.getEntity());
                Log.d("CreateEvent successful",responseBody);
            } catch (Exception ex) {
                Log.e("CreateEvent failed", ex.getMessage());
                System.out.println(ex);
                // handle exception here
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return responseBody;
        }
        protected void onProgressUpdate() {
        }

        protected void onPostExecute(String response) {
            Toast.makeText(CreateEvent.this, "Finished setting up event: " + response, Toast.LENGTH_LONG).show();
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

    //    class ViewPagerAdapter extends FragmentPagerAdapter {
//
//        private ArrayList<Fragment> fragments;
//        private ArrayList<String> titles;
//
//        ViewPagerAdapter(FragmentManager fm){
//            super(fm);
//            this.fragments = new ArrayList<>();
//            this.titles = new ArrayList<>();
//        }
//
//        @NonNull
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        public void addFragment(Fragment fragment, String title){
//            fragments.add(fragment);
//            titles.add(title);
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles.get(position);
//        }
//    }
}
