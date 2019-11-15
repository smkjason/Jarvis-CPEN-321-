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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.io.IOException;
import java.net.URISyntaxException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar mtoolbar;
        Button create;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        final EditText nameofEvent;
        final EditText dateofEvent;
        final EditText peopleatEvent;

        String email;

        //TextEdits
        nameofEvent = findViewById(R.id.name_of_event);
        dateofEvent = findViewById(R.id.date_of_event);
        peopleatEvent = findViewById(R.id.add_people_to_event);

        //Button(s)
        create = findViewById(R.id.make_event);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();

        mSocket = ((jarvis) this.getApplication()).getmSocket();
        if(mSocket.connected()){
            Toast.makeText(CreateEvent.this, "Connected!!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(CreateEvent.this, "Can't connect...", Toast.LENGTH_LONG).show();
        }

        mtoolbar = findViewById(R.id.create_event_toolbar);
//        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
//        appBarLayout = (AppBarLayout) findViewById(R.id.appbarid);
//        viewPager = (ViewPager) findViewById(R.id.viewpager_id);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create_Event");

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

    private void makeNewEvent(final String eventName, String eventDate, String email) {
        /* Check if the User exists on Server */
        new CommunicateBackend(eventName, eventDate, email).execute();
    }

    private class CommunicateBackend extends AsyncTask<Void, Void, Void> {

        String eventName;
        String eventDate;
        String email;

        CommunicateBackend(String eventName, String eventDate, String email) {
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... v) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/");

                JSONObject json = new JSONObject();
                json.put("name", eventName);
                json.put("date", eventDate);
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
            Toast.makeText(CreateEvent.this, "Event sent to backend", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
            //Maybe Implemented
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
