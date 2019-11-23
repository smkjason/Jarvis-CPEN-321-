package com.example.jarvis;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jarvis.adapter.PreferredTimeAdapter;
import com.example.jarvis.jarvis_types.jarvis_pt;
import com.google.android.material.appbar.AppBarLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


//TODO: Finish this
public class ChoosePreferredTime extends AppCompatActivity implements PTDialog.PTDialogListener {
    private static final String TAG = "ChoosePreferredTime";

    //XML
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AppBarLayout appBarLayout;

    //Current Event Info
    private String eventid;
    private String eventname;

    //To send Backend
    private ArrayList<jarvis_pt> list_of_pt = new ArrayList<jarvis_pt>(); //Array of preferred times.

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

        PreferredTimeAdapter preferredTimeAdapter = new PreferredTimeAdapter(list_of_pt);
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
            PTDialog ptDialog = new PTDialog();
            ptDialog.show(getSupportFragmentManager(), "Preferred Time D");
//            Intent intent = new Intent(getApplicationContext(), Popup.class);
//            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == Activity.RESULT_OK){
//            //TODO: add a new adapter
//        }else if(requestCode == Activity.RESULT_CANCELED){
//            //TODO:
//        }
//    }

    @Override
    public void applyTexts(Date startdate, Date starttime, Date enddate, Date endtime) {

        DateFormat dateTimeFormatter = new SimpleDateFormat("MM-dd, yyyy h:mm");
        DateFormat parseDate = new SimpleDateFormat("MM-dd, yyyy");
        DateFormat parseTime = new SimpleDateFormat("hh:mm");


        // Need to combine Date and Time

        //For start PT
        String parsed_date = parseDate.format(startdate);
        String parsed_time = parseTime.format(starttime);

        Log.d(TAG, "parseddate: " + parsed_date);
        Log.d(TAG, "parsed_time: " + parsed_time);

        try {
            startdate = dateTimeFormatter.parse(parsed_date + parsed_time);
        }catch(ParseException e){
            Log.e(TAG, "Couldn't parse dates", e);
        }

        Log.d(TAG, "Full Parsing: Start Date: " + startdate);


        //For END PT
        parsed_date = parseDate.format(enddate);
        parsed_time = parseTime.format(endtime);

        Log.d(TAG, "parseddate: " + parsed_date);
        Log.d(TAG, "parsed_time: " + parsed_time);

        try {
            enddate = dateTimeFormatter.parse(parsed_date + parsed_time);
        }catch(ParseException e){
            Log.e(TAG, "Couldn't parse dates", e);
        }

        Log.d(TAG, "Full Parsing: End Date: " + enddate);

        //Add the adapter
        jarvis_pt pt = new jarvis_pt(startdate, enddate);

        list_of_pt.add(pt);

        PreferredTimeAdapter preferredTimeAdapter = new PreferredTimeAdapter(list_of_pt);
        preferredTimeAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(preferredTimeAdapter);
    }
}
