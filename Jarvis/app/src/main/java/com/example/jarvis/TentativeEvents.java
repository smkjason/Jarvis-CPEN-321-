package com.example.jarvis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.adapter.TentativeEventsAdapter;
import com.example.jarvis.jarvis_types.FriendItem;
import com.example.jarvis.jarvis_types.TentativeEventItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class TentativeEvents extends AppCompatActivity {
    private static final String TAG = "TentativeEvents";

    //XML
    private RecyclerView mRecyclerView;
    private TentativeEventsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Information
    ArrayList<TentativeEventItem> mEventsList = new ArrayList<>();
    private GoogleSignInAccount acct;
    private String myemail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tentative_events);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        myemail = acct.getEmail();

//        mEventsList.add(new TentativeEventItem("Event title", "Deadline time", "eventId"));


        mRecyclerView = findViewById(R.id.tentative_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TentativeEventsAdapter(mEventsList, TentativeEvents.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    mAdapter.setOnItemClickListener(new TentativeEventsAdapter.OnItemClickListener() {
        @Override
        public void onItemCLick(int position) {
            gotoSelectTime(position);
        }
    });
    }

    public void gotoSelectTime(int position){
        Intent intent = new Intent(getApplicationContext(), SelectTime.class);
        intent.putExtra("Event Title", mEventsList.get(position).getTitle());
        intent.putExtra("Event Id", mEventsList.get(position).getEventId());
        startActivityForResult(intent,77);
//            mEventsList.remove(position);
//            mAdapter.notifyItemChanged(position);

        Toast.makeText(TentativeEvents.this,"Select time to confirm event",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 77 && resultCode == RESULT_OK) {
//            ArrayList<String> newList = data.getStringArrayListExtra("Added Friends");
            //TODO: maybe delete event? or update list from backend?
            mEventsList.clear();
            mAdapter.notifyDataSetChanged();
            new getTentativeEvents().execute();
        }

    }

    private class getTentativeEvents extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + myemail + "/admin");
            try {
                httpGet.addHeader("Authorization", "Bearer " + acct.getIdToken());
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String jsonString = EntityUtils.toString(httpEntity);
                Log.d("Get Tentative Events", jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                jsonArray = jsonObject.getJSONArray("events");
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray){
            super.onPostExecute(jsonArray);
            JSONObject cur;
            if(jsonArray == null || jsonArray.length() == 0){
                Log.d("TentativeEvents", "No events found");
                //Toast.makeText(TentativeEvents.this, "No Events to finalize", Toast.LENGTH_LONG).show();
            }
            else{
                mEventsList.clear();
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        mEventsList.add(new TentativeEventItem(cur.getString("name"), cur.getString("deadline"), cur.getString("id")));
                        Log.d("TentativeEvents",cur.getString("name"));
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e("TentativeEvents", "Reading info from JSONObject: JSONException", e);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getTentativeEvents().execute();
    }
}