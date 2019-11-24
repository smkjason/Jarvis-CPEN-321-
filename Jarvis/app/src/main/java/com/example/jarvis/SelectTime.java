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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        timesList.add(new SelectTimeItem("2019-11-30 16:00", "2019-11-30 20:00"));
        timesList.add(new SelectTimeItem("2019-12-05 03:00", "2019-12-05 10:00"));
        timesList.add(new SelectTimeItem("2019-12-24 12:00", "2019-12-25 12:00"));

        //new getRecommendedTime().execute();

        mRecyclerView = findViewById(R.id.select_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SelectTimeAdapter(timesList, SelectTime.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SelectTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                confirmEvent(position);
            }
        });
    }

    public void confirmEvent(int position){ //send selected time to server to finialize
        timesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        Toast.makeText(SelectTime.this,"Event created",Toast.LENGTH_LONG).show();

        returntoTentativeEvent();
    }

    public void returntoTentativeEvent() {
//        Intent i = new Intent();
        //i.putExtra("Added Friends", addedList); //need to change array
//        setResult(RESULT_OK,i);
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
                String jsonObject = EntityUtils.toString(httpEntity);
                Log.d("Get Recommended Times", jsonObject);
                //jsonArray = new JSONArray(jsonObject.);
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
                Toast.makeText(SelectTime.this, "No Recommended Times by the deadline", Toast.LENGTH_LONG).show();
            }
            else{
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        timesList.add(new SelectTimeItem("2019-11-30 16:00", "2019-11-30 20:00"));
                        //Log.d("Select Time", );
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e("Find c", "TentativeEvents(): JSONException", e);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
