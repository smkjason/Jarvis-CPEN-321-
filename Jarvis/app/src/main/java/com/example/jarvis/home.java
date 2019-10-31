package com.example.jarvis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.Fragments.ChatFragment;
import com.example.jarvis.Fragments.UserFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mortbay.jetty.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class home extends AppCompatActivity {

    Button view_profile, create_event, chatrooms,
            Signout;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private Toolbar toolbar;

    TextView backendMessage;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hello");

        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        view_profile = findViewById(R.id.view_profile_bttn);
        create_event = findViewById(R.id.create_event_bttn);
        chatrooms = findViewById(R.id.go_to_chatroom_bttn);
        Signout = findViewById(R.id.sign_out_button);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(home.this, MainActivity.class));
                }
            }
        };

        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // ...
                    case R.id.sign_out_button:
                        mAuth.signOut();
                        finish();
                        break;
                }
            }
        });


        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, com.example.jarvis.view_profile.class);
                startActivity(intent);
            }
        });

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, Create_Event.class);
                startActivity(intent);
            }
        });

        new BackendTask().execute();
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
        }
    }


    /* Probably not needed anymore because we are now using Firebase login logout */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(home.this, "Logged Out", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    /* This is for drop down menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.Settings_menu){
            /* Do something */
        }
        if(item.getItemId() == R.id.Log_out_menu){
            /* Do something */
            mAuth.signOut();
        }

        return true;
    }
}
