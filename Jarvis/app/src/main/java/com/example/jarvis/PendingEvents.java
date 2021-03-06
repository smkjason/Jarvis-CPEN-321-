package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jarvis.adapter.PendingEventsAdapter;
import com.example.jarvis.jarvis_types.jarvisevent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//List of invites this user got.
public class PendingEvents extends AppCompatActivity {
    private static final String TAG = "PendingEventsAdapter";

    //XML
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    //Information
    private ArrayList<jarvisevent> list_of_invited_events = new ArrayList<>();
    private String idToken, user_email;

    public PendingEvents() {
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("idToken")){
            this.idToken = getIntent().getStringExtra("idToken");
            this.user_email = getIntent().getStringExtra("email");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invites);
        getIncomingIntent();
        initializeFields();
    }

    private void initializeFields() {
        toolbar = findViewById(R.id.invites_ToolBar);

        recyclerView = findViewById(R.id.rvInvites);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        PendingEventsAdapter pendingEventsAdapter = new PendingEventsAdapter(
                list_of_invited_events,
                PendingEvents.this,
                idToken, user_email);
        pendingEventsAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(pendingEventsAdapter);
        new fetchInvites(user_email, idToken).execute();
    }

    private class fetchInvites extends AsyncTask<Void, Void, JSONObject> {

        private String user_email;
        private String idToken;

        fetchInvites(String user_email, String idToken){
            this.user_email = user_email;
            this.idToken = idToken;
        }

        @Override
        protected JSONObject doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONObject jsonObject = new JSONObject();
            Log.d(TAG, "AM I HERE THOOOOO");
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + user_email + "/invites");
            try {
                httpGet.addHeader("Authorization", "Bearer " + idToken);
                httpGet.addHeader("Content-Type", "application/json");
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                jsonObject = new JSONObject(EntityUtils.toString(httpEntity));
                Log.d(TAG, "json_string: " + jsonObject);
            } catch (Exception e) {
                Log.e(TAG, "I caught some exception.", e);
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject.has("events")){
                JSONObject cur;
                String id, name, creator, length;
                Log.d(TAG, "AM I HERE THOOOOO22222222222222222222");
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("events");
                    if(jsonArray == null){
                        Toast.makeText(PendingEvents.this, "Something went wrong with retrieving info...", Toast.LENGTH_LONG).show();
                    }else if(jsonArray.length() == 0){
                        Toast.makeText(PendingEvents.this, "You have no invitations", Toast.LENGTH_LONG).show();
                    }else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cur = jsonArray.getJSONObject(i);
                            id = cur.getString("id");
                            name = cur.getString("name");
                            creator = cur.getString("creatorEmail");
                            length = cur.getString("length");
                            jarvisevent jarvisevent = new jarvisevent(name, id, creator, length);
                            list_of_invited_events.add(jarvisevent);
                        }
                        Toast.makeText(PendingEvents.this, "You got some invitations!", Toast.LENGTH_LONG).show();
                        PendingEventsAdapter pendingEventsAdapter = new PendingEventsAdapter(list_of_invited_events,
                                PendingEvents.this,
                                idToken, user_email);
                        pendingEventsAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(pendingEventsAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(PendingEvents.this, "Something went wrong with retrieving events...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
