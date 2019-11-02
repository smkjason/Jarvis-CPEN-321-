package com.example.jarvis;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jarvis.Fragments.EventFragment;
import com.example.jarvis.Model.Events;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class home extends AppCompatActivity {
    private static final String TAG = "home";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    Button view_profile, create_event, chatrooms,
            Signout, Map;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    GoogleSignInClient mGoogleSignInClient;

    private ArrayList<Events> mEvents;

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

        toolbar = findViewById(R.id.home_toolbar);
        tabLayout = findViewById(R.id.tablayout_id);
        appBarLayout = findViewById(R.id.appbarid);
        viewPager = findViewById(R.id.viewpager_id);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hello");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Add the fragments
        EventFragment eventFragment = new EventFragment();
        adapter.addFragment(eventFragment, "Events");
        //adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        initEvents();

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

        if (isServicesOK()) { //check if map can operate with this phone
            init();
        }

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
                        revokeAccess();
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

//    private void initEvents(){
//        /* Retrieve from the Database */
//        initRecyclerView();
//    }
//
//    private void initRecyclerView()
//    {
//        Log.d("Events", "Initializing recyclerview of events");
//        RecyclerView recyclerView = findViewById(R.id.events_recyclerview);
//        CustomAdapter customAdapter = new CustomAdapter(mEvents, home.this);
//        recyclerView.setAdapter(customAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(home.this));
//    }

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
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(home.this, "Revoked", Toast.LENGTH_LONG).show();
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
            revokeAccess();
        }

        return true;
    }


        class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public boolean isServicesOK() {
        Log.d(TAG,"isServicesOK: checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(home.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but we can fix (e.g. version issue
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(home.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void init() {
        Map = findViewById(R.id.Map_bttn);
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}
