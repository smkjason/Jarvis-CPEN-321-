package com.example.jarvis;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class view_profile extends AppCompatActivity {

    ImageView UserPhoto;
    TextView Username, Useremail, backendMessage;
    Button Signout;

    Button Send;
    Button View_Calendar;
    Button CreateEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);


        UserPhoto = findViewById(R.id.Userphoto);
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.usergmail);
        Send = findViewById(R.id.urlconnection);
        View_Calendar = findViewById(R.id.button);
        backendMessage = findViewById(R.id.backendMessage);
        CreateEvent = findViewById(R.id.CreateEvent);

        View_Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.button:
                        goToCalendar();
                        break;
                }
            }
        });

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.CreateEvent:
                        GoToCreateEvent();
                        break;
                }
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Username.setText(personName);
            Useremail.setText(personEmail);
            //backendMessage.setText("TEST");
            //UserID.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(UserPhoto);
        }
    }

    private void goToCalendar(){
        Intent intent = new Intent(view_profile.this, View_Calendar.class);
        startActivity(intent);
    }

    private void GoToCreateEvent(){
        Intent intent = new Intent(view_profile.this, Create_Event.class);
        startActivity(intent);
    }


    private class BackendTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");
            String message = "";
            try {
                HttpResponse response = client.execute(request);

// Get the response
                BufferedReader rd = new BufferedReader
                        (new InputStreamReader(
                                response.getEntity().getContent()));

                String line = "";
                while ((line = rd.readLine()) != null) {
                    message += line;
                    System.out.println(message);
                }
            } catch (java.io.IOException e) {
                Log.w("Error", "Connection Error");
            }
            return message;
        }


        protected void onProgressUpdate() {
        }

        protected void onPostExecute(String message) {
            //System.out.println(message);
            backendMessage.setText(message);
        }
    }
}
