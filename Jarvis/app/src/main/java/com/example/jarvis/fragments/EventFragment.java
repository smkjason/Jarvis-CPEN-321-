package com.example.jarvis.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jarvis.GroupChatActivity;
import com.example.jarvis.adapter.CustomAdapter;
import com.example.jarvis.R;

import com.example.jarvis.jarvis_types.jarvisevent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;

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

public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    private DatabaseReference EventRef;

    private ArrayList<jarvisevent> mEvents = new ArrayList<>();
    private RecyclerView recyclerView;

    private Socket mSocket;

    private GoogleSignInAccount acct;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Events", "Started events");

        acct = GoogleSignIn.getLastSignedInAccount(getActivity());


//        HttpClient httpClient = new DefaultHttpClient();
//        HttpResponse httpResponse;
//        HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/");
//        try {
//            httpGet.addHeader("Authorization", "Bearer " + idToken);
//            httpResponse = httpClient.execute(httpGet);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            json_string = EntityUtils.toString(httpEntity);
//            Log.d("http", "json_string: " + json_string);
//            response_json = new JSONArray(json_string);
//        } catch (ClientProtocolException e) {
//            Log.e("Error", "Error sending ID token to backend.", e);
//        } catch (IOException e) {
//            Log.e("Error", "Error sending ID token to backend.", e);
//        } catch (Exception e) {
//            Log.e("Error", "I caught some exception.", e);
//        }
    }

    private void initEvents() {
        Log.i("Events", "Initializing Events");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        view.setTag(TAG);
        final String email = acct.getEmail();
        final String idToken = acct.getIdToken();

        recyclerView = view.findViewById(R.id.events_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new GetEventIDs(idToken, email).execute();

//
//        mAdapter = new CustomAdapter(mEvents, getActivity());
//
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private class GetEventIDs extends AsyncTask<Void, Void, JSONArray> {

        String idToken;
        String email;

        GetEventIDs(String idToken, String email) {
            this.idToken = idToken;
            this.email = email;
        }

        @Override
        protected JSONArray doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            String id = "";
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/");
            try {
                httpGet.addHeader("Authorization", "Bearer " + idToken);
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String json_string = EntityUtils.toString(httpEntity);
                Log.d("http", "json_string: " + json_string);
                jsonArray = new JSONArray(json_string);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            JSONObject cur;
            if(jsonArray == null || jsonArray.length() == 0){
                Toast.makeText(getActivity(), "!!couldn't get jsonarray!!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), "Trying to get event names...........", Toast.LENGTH_LONG).show();
                Log.d(TAG, "jsonArray: " + jsonArray);
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        jarvisevent event = new jarvisevent(cur.getString("summary"), cur.getString("id"));
                        mEvents.add(event);
                        Log.d(TAG, "jsonobj: " + cur.getString("summary"));
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e(TAG, "initEvents(): JSONException", e);
                    }
                }
            }
            CustomAdapter mAdapter = new CustomAdapter(mEvents, getActivity());
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);
            recyclerView.scrollToPosition(mEvents.size() - 1);

            Intent intent = new Intent(getActivity(), GroupChatActivity.class);
            intent.putExtra("eventname", "Nothing for now");
            startActivity(intent);
        }
    }

}
