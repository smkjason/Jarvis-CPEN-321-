package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jarvis.adapter.PreferredTimeAdapter;
import com.example.jarvis.jarvis_types.jarvis_pt;
import com.google.android.material.appbar.AppBarLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private String user_email;
    private String idToken;

    //To send Backend
    private ArrayList<jarvis_pt> list_of_pt = new ArrayList<jarvis_pt>(); //Array of preferred times.

    private void getIncomingIntent(){
        if(getIntent().hasExtra("eventid") && getIntent().hasExtra("eventname")){
            eventid = getIntent().getStringExtra("eventid");
            eventname = getIntent().getStringExtra("eventname");
            user_email = getIntent().getStringExtra("email");
            this.idToken = getIntent().getStringExtra("idToken");
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
        getIncomingIntent();
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

        DateFormat dateTimeFormatter = new SimpleDateFormat("MM-dd, yyyy h:mm");
        DateFormat parseDate = new SimpleDateFormat("MM-dd, yyyy");
        DateFormat parseTime = new SimpleDateFormat("hh:mm");
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
        if(item.getItemId() == R.id.finish_pt_bttn){
            new sendPT(list_of_pt, user_email, eventid, idToken).execute();
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

    private class sendPT extends AsyncTask<Void, Void, JSONObject> {

        ArrayList<jarvis_pt> list_of_pt_final;
        String email;
        String eventid;
        DateFormat dateTimeFormatter = new SimpleDateFormat("YYYY-MM-DD hh:mm");
        String idToken;


        sendPT(ArrayList<jarvis_pt> list_of_pt_final, String email, String eventid, String idToken) {
            this.list_of_pt_final = list_of_pt_final;
            this.email = email;
            this.eventid = eventid;
            this.idToken = idToken;
        }

        @Override
        protected JSONObject doInBackground(Void... v) {
            JSONObject createresponse = new JSONObject();
            jarvis_pt jarvis_pt;
            String startT, endT;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/"
                        + eventid + "?decline=false");
                JSONArray timeslots = new JSONArray();
                for(int i = 0; i < list_of_pt_final.size(); i++){
                    JSONObject jsonObject = new JSONObject();
                    jarvis_pt = list_of_pt_final.get(i);
                    startT = dateTimeFormatter.format(jarvis_pt.getStarttime());
                    endT = dateTimeFormatter.format(jarvis_pt.getEndtime());
                    Log.d(TAG, "StartTime is: " + startT);
                    Log.d(TAG, "endTime is: " + endT);
                    jsonObject.put("startTime", startT);
                    jsonObject.put("endTime", endT);
                    timeslots.put(jsonObject);
                }
                JSONObject send = new JSONObject();
                send.put("timeslots", timeslots);
                httpPost.setHeader("Authorization", "Bearer " + idToken);
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                createresponse = new JSONObject(responseBody);
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return createresponse;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
//            Toast.makeText(CreateEvent.this, "Event Successfully Created", Toast.LENGTH_LONG).show();
            super.onPostExecute(jsonObject);
            try {
                if (!jsonObject.getString("status").equals("success")) {
                    Toast.makeText(ChoosePreferredTime.this, "Something went wrong with invitation", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(ChoosePreferredTime.this, "Invitations sent!", Toast.LENGTH_LONG).show();
                    eventid = jsonObject.getString("id");
                    finish();
                }
            }catch (JSONException e){
                Log.e(TAG, "JSONException caught", e);
            }
        }
    }
}
