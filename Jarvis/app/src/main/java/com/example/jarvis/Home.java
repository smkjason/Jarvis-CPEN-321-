package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jarvis.fragments.EventFragment;
import com.example.jarvis.fragments.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.socket.client.Socket;


public class Home extends AppCompatActivity {

    private static final String TAG = "Home";

    private Socket mSocket;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        initializeFrags();
    }

    private void initializeFrags(){
        toolbar = findViewById(R.id.home_toolbar);
        tabLayout = findViewById(R.id.tablayout_id);
        viewPager = findViewById(R.id.viewpager_id);

        mSocket = ((jarvis) this.getApplication()).getmSocket();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(Home.this);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jarvis");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                        Toast.makeText(Home.this, "Logged Out", Toast.LENGTH_LONG).show();
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

        if(item.getItemId() == R.id.Settings_profile){
            Intent intent = new Intent(Home.this, com.example.jarvis.ViewProfile.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.refresh_events){
            initializeFrags();
        }

        if(item.getItemId() == R.id.Log_out_menu){
            signOut();
        }

        return super.onOptionsItemSelected(item);
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
