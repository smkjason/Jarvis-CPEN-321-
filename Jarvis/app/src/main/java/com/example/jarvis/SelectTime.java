package com.example.jarvis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.adapter.SelectTimeAdapter;
import com.example.jarvis.jarvis_types.SelectTimeItem;
import com.example.jarvis.jarvis_types.TentativeEventItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

//List of events that this user created (Admin)
public class SelectTime extends AppCompatActivity {
    private static final String TAG = "SelectTime";

    //XML
    private RecyclerView mRecyclerView;
    private SelectTimeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView selectTimeTitle;

    //Information
    ArrayList<SelectTimeItem> timesList = new ArrayList<>();
    private String eventId;

    private GoogleSignInAccount acct;
    private String myemail;
    private String idToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        myemail = acct.getEmail();
        idToken = acct.getIdToken();

        selectTimeTitle = findViewById(R.id.select_time_title);
        Intent intent = getIntent();

        selectTimeTitle.setText(intent.getStringExtra("Event Title"));
        eventId = intent.getStringExtra("Event Id");

//        timesList.add(new SelectTimeItem("2019-11-30 16:00", "2019-11-30 20:00"));
//        timesList.add(new SelectTimeItem("2019-12-05 03:00", "2019-12-05 10:00"));
//        timesList.add(new SelectTimeItem("2019-12-24 12:00", "2019-12-25 12:00"));

        new getRecommendedTime().execute();

        mRecyclerView = findViewById(R.id.select_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SelectTimeAdapter(timesList, SelectTime.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SelectTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                confirmEvent(position);
            }
        });
    }

    public void confirmEvent(int position){ //send selected time to server to finialize
//        timesList.remove(position);
//        mAdapter.notifyItemRemoved(position);
        new finalizeEvent(timesList.get(position).getStart(),timesList.get(position).getEnd()).execute();
        returntoTentativeEvent();
    }

    public void returntoTentativeEvent() {
        Intent i = new Intent();
       // i.putExtra("Added Friends", addedList); //need to change array
        setResult(RESULT_OK,i);
        finish();
    }

    private class getRecommendedTime extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/events/" + eventId + "/preferred");
            try {
                httpGet.addHeader("Authorization", "Bearer " + idToken);
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String jsonString = EntityUtils.toString(httpEntity);
                Log.d("Get Recommended Times", jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                jsonArray = jsonObject.getJSONArray("timeslots");
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray){
            super.onPostExecute(jsonArray);
            JSONObject cur;
            if(jsonArray == null || jsonArray.length() == 0) {
                //Toast.makeText(SelectTime.this, "No Recommended Times by the deadline", Toast.LENGTH_LONG).show();
            }
            else{
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        timesList.add(new SelectTimeItem(cur.getString("startTime"), cur.getString("endTime")));
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e("SelectTime", "Reading info from JSONObject: JSONException", e);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class finalizeEvent extends AsyncTask<Void, Void, JSONObject> {
        private String startTime;
        private String endTime;

        finalizeEvent(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;

        }

        @Override
        protected JSONObject doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONObject jsonReturn = new JSONObject();
            HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/events/" + eventId + "/activate");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("startTime", startTime);
                jsonObject.put("endTime", endTime);
                httpPost.setEntity(new StringEntity(jsonObject.toString()));
                httpPost.addHeader("Authorization", "Bearer " + idToken);

                httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String jsonString = EntityUtils.toString(httpEntity);
                Log.d("finalizeEvent", jsonString);
                jsonReturn = new JSONObject(jsonString);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return jsonReturn;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject){
            super.onPostExecute(jsonObject);
            JSONObject cur = jsonObject;
            if(jsonObject == null || jsonObject.length() == 0) {
                Toast.makeText(SelectTime.this, "No Recommended Times by the deadline", Toast.LENGTH_LONG).show();
            }
            else{
                try {
                    if (cur.getString("creatorEmail").equals(myemail)) {
                        Toast.makeText(SelectTime.this,selectTimeTitle.getText() + " Event created",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SelectTime.this, "unable to finalize event", Toast.LENGTH_LONG).show();
                    }
                }catch(JSONException e){
                    Toast.makeText(SelectTime.this, "unable to finalize event", Toast.LENGTH_LONG).show();
                    Log.d("SelectTime", e.getLocalizedMessage());
                }
            }
        }
    }
}
