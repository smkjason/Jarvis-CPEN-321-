package com.example.jarvis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jarvis.adapter.SearchFriendAdapter;
import com.example.jarvis.jarvis_types.FriendItem;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFriends extends AppCompatActivity {
    private static final String TAG = "SearchFriends";

    private RecyclerView mRecyclerView;
    private SearchFriendAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    ArrayList<FriendItem> friendList = new ArrayList<>();
    ArrayList<String> addedList;
    GoogleSignInAccount acct;
    String myemail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friends);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        myemail = acct.getEmail();

        Intent intent = getIntent();
        addedList = intent.getStringArrayListExtra("Added Friends");

        toolbar = findViewById(R.id.searchfriends_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invite Friends");

        new getUsernames().execute();

        mRecyclerView = findViewById(R.id.search_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SearchFriendAdapter(friendList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SearchFriendAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                addedFriend(position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        Log.d("Back","going back");
        i.putExtra("Added Friends", addedList); //need to change array
        setResult(RESULT_OK,i);
        finish();
    }

    public void newSearch(){
        friendList.removeAll(friendList);
        mAdapter.notifyDataSetChanged();
    }

    public void addedFriend(int position){
        if (!addedList.contains(friendList.get(position).getFriend())) {
            addedList.add(friendList.get(position).getFriend());
        }
        Toast.makeText(SearchFriends.this , "Friend " + position + "added!", Toast.LENGTH_LONG).show();
        friendList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }


    private class getUsernames extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... v) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user?q=");
            try {
//                httpGet.addHeader("Authorization", "Bearer " + idToken);
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String usernames = EntityUtils.toString(httpEntity);
                Log.d("Get Username from server", usernames);
                jsonArray = new JSONArray(usernames);
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
        protected void onPostExecute(JSONArray jsonArray){
            super.onPostExecute(jsonArray);
            JSONObject cur;
            if(jsonArray == null || jsonArray.length() == 0){
                Toast.makeText(SearchFriends.this, "No users with those characters", Toast.LENGTH_LONG).show();
            }
            else{
                for(int index = 0; index < jsonArray.length(); index++){
                    try{
                        cur = jsonArray.getJSONObject(index);
                        if (!myemail.equals(cur.getString("email"))) {
                            friendList.add(new FriendItem(R.drawable.ic_android, cur.getString("email")));
                        }
                        Log.d("Email",cur.getString("email"));
                    }catch(JSONException e){
                        e.printStackTrace();
                        Log.e("Find Users", "SearchFriends(): JSONException", e);
                    }
                }
                Log.d("My Email", myemail);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
