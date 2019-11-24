package com.example.jarvis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfile extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView UserPhoto;
        TextView Username;
        TextView Useremail;
        TextView backendMessage;

        Button View_Calendar;
        Button CreateEvent;
        Button Revoke;

        GoogleSignInAccount acct;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        UserPhoto = findViewById(R.id.Userphoto);
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.usergmail);
        Revoke = findViewById(R.id.Revoke);

        Revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
                finish();
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

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ViewProfile.this, "Revoked", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
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
