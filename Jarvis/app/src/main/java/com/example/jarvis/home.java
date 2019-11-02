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
import com.example.jarvis.Fragments.HomeFragment;
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
    private ViewPager viewPager;

    FirebaseAuth mAuth;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();

//        mAuth.addAuthStateListener(mAuthListener); moving...
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

//        mAuth = FirebaseAuth.getInstance(); moving...

        toolbar = findViewById(R.id.home_toolbar);
        tabLayout = findViewById(R.id.tablayout_id);
        viewPager = findViewById(R.id.viewpager_id);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jarvis");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Add the fragments
        EventFragment eventFragment = new EventFragment();
        HomeFragment homeFragment = new HomeFragment();
        adapter.addFragment(homeFragment, "Home");
        adapter.addFragment(eventFragment, "Events");
        //adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
}
