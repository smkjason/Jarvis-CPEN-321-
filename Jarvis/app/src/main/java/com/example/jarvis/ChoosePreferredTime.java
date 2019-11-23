package com.example.jarvis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jarvis.adapter.PreferredTimeAdapter;
import com.example.jarvis.jarvis_types.jarvis_pt;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


//TODO: Finish this
public class ChoosePreferredTime extends AppCompatActivity {
    private static final String TAG = "ChoosePreferredTime";

    //XML
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AppBarLayout appBarLayout;

    //Current Event Info
    private String eventid;
    private String eventname;

    //To send Backend
    private ArrayList<jarvis_pt> dates = new ArrayList<jarvis_pt>(); //Array of preferred times.

    private void getIncomingIntent(){
        if(getIntent().hasExtra("eventid") && getIntent().hasExtra("eventname")){
            eventid = getIntent().getStringExtra("eventid");
            eventname = getIntent().getStringExtra("eventname");

        } else{
            Toast.makeText(ChoosePreferredTime.this,"Couldn't load event info...", Toast.LENGTH_LONG)
                    .show();
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosepreferredtime);
        //getIncomingIntent();
        initialize();
    }

    private void initialize() {
        toolbar = findViewById(R.id.choosept_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose Preferred TImes");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.preferredtimes_recyclerv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        PreferredTimeAdapter preferredTimeAdapter = new PreferredTimeAdapter(dates);
        preferredTimeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(preferredTimeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_preferredtime_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.add_pt_bttn){
            //TODO: prompt user to input start and endtime
            Intent intent = new Intent(getApplicationContext(), Popup.class);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Activity.RESULT_OK){
            //TODO: add a new adapter
        }else if(requestCode == Activity.RESULT_CANCELED){
            //TODO:
        }
    }
}
