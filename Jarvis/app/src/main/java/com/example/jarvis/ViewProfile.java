package com.example.jarvis;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView UserPhoto;
        TextView Username;
        TextView Useremail;
        TextView backendMessage;

        Button View_Calendar;
        Button CreateEvent;

        GoogleSignInAccount acct;
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
                goToCalendar();
            }
        });

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateEvent();
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
        Intent intent = new Intent(ViewProfile.this, ViewCalendar.class);
        startActivity(intent);
    }

    private void goToCreateEvent(){
        Intent intent = new Intent(ViewProfile.this, CreateEvent.class);
        startActivity(intent);
    }



}
