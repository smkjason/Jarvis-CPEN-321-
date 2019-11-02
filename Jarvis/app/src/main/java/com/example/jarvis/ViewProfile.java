package com.example.jarvis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.appcompat.app.AppCompatActivity;

public class ViewProfile extends AppCompatActivity {

    private ImageView UserPhoto;
    private TextView Username;
    private TextView Useremail;
    private TextView backendMessage;

    private Button View_Calendar;
    private Button CreateEvent;

    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);


        UserPhoto = findViewById(R.id.Userphoto);
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.usergmail);
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
                        goToCreateEvent();
                        break;
                }
            }
        });

        acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();

            Username.setText(personName);
            Useremail.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(UserPhoto);
        }
    }

    private void goToCalendar(){
        Intent intent = new Intent(ViewProfile.this, ViewCalendar.class);
        startActivity(intent);
    }

    private void goToCreateEvent(){
        Intent intent = new Intent(ViewProfile.this, CreateEvent.class);
        startActivity(intent);
    }
}
