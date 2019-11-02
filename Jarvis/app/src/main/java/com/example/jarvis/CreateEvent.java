package com.example.jarvis;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

public class CreateEvent extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private Toolbar mtoolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        final EditText nameofEvent, dateofEvent, peopleatEvent;


        //TextEdits
        nameofEvent = (EditText) findViewById(R.id.name_of_event);
        dateofEvent = (EditText) findViewById(R.id.date_of_event);
        peopleatEvent = (EditText) findViewById(R.id.add_people_to_event);

        //Button(s)
        create = findViewById(R.id.make_event);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mtoolbar = (Toolbar) findViewById(R.id.create_event_toolbar);
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

    private void makeNewEvent(final String eventName, String eventDate, String eventMembers) {
        /* Check if the User exists on Server */
        currentUser = mAuth.getCurrentUser();
        RootRef.child("Events").child(eventName).setValue("");
        RootRef.child("Events").child(eventName).child(eventDate).setValue("");
        RootRef.child("Events").child(eventName).child(eventMembers).setValue("")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CreateEvent.this, eventName + "is created Successfully...", Toast.LENGTH_LONG).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(CreateEvent.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        /* Add the Events under this User */
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
