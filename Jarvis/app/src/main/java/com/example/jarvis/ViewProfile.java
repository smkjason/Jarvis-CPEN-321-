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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView UserPhoto;
        TextView Username;
        TextView Useremail;
        TextView backendMessage;

        Button View_Calendar;
        Button CreateEvent;
        Button FriendList;

        GoogleSignInAccount acct;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);


        UserPhoto = findViewById(R.id.Userphoto);
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.usergmail);
        View_Calendar = findViewById(R.id.button);
        backendMessage = findViewById(R.id.backendMessage);
        CreateEvent = findViewById(R.id.CreateEvent);
        FriendList = findViewById(R.id.FriendList);

        View_Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCalendar();
            }
        });

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateEvent();
            }
        });

        FriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFriendList();
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


//        Toolbar myToolbar = (Toolbar) findViewById(R.id.friends_bar);
//        setSupportActionBar(myToolbar);
    }

    private void goToCalendar(){
        Intent intent = new Intent(ViewProfile.this, PendingEvents.class);
        startActivity(intent);
    }

    private void goToCreateEvent(){
        Intent intent = new Intent(ViewProfile.this, CreateEvent.class);
        startActivity(intent);
    }

    private void goToFriendList() {
        Intent intent = new Intent(ViewProfile.this, FriendList.class);
        startActivity(intent);
    }



}
