package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import io.socket.client.Socket;

public class CreateEvent extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Socket mSocket;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar mtoolbar;
        Button create;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        final EditText nameofEvent;
        final EditText dateofEvent;
        final EditText peopleatEvent;


        //TextEdits
        nameofEvent = findViewById(R.id.name_of_event);
        dateofEvent = findViewById(R.id.date_of_event);
        peopleatEvent = findViewById(R.id.add_people_to_event);

        //Button(s)
        create = findViewById(R.id.make_event);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mSocket = ((jarvis) this.getApplication()).getmSocket();
        if(mSocket.connected()){
            Toast.makeText(CreateEvent.this, "Connected!!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(CreateEvent.this, "Can't connect...", Toast.LENGTH_LONG).show();
        }

        mtoolbar = (Toolbar) findViewById(R.id.create_event_toolbar);
//        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
//        viewPager = (ViewPager) findViewById(R.id.viewpager_id);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create_Event");
        acct = GoogleSignIn.getLastSignedInAccount(this);
        
        new CreateTask().execute();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eventName = nameofEvent.getText().toString();
                final String eventDate = dateofEvent.getText().toString();
                final String eventMembers = peopleatEvent.getText().toString();

                makeNewEvent(eventName, eventDate, eventMembers);

            }
        });

//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        //Add the fragments
//        adapter.addFragment(new EventFragment(), "Chat");
//        adapter.addFragment(new UserFragment(), "Users");
//        //adapter setup
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void makeNewEvent(final String eventName, String eventDate, String eventMembers) {
        /* Check if the User exists on Server */
        JSONObject event = new JSONObject();
        try {
            Log.d("socket", "Sending event jsonobject...");
            event.put("eventName", eventName);
            event.put("eventDate", eventDate);
            event.put("eventMembers", eventMembers);
        }catch (JSONException e){
            Log.e("socket", "JSONException caught");
        }
        mSocket.emit("new event", event);
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
