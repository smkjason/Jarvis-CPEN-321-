package com.example.jarvis;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.adapter.FriendAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FriendList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        toolbar = findViewById(R.id.friend_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FriendList");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        //new BackendTask(acct.getEmail()).execute();

        ListView resultsListView = (ListView) findViewById(R.id.results);

        HashMap<String,String> nameEmail = new HashMap<>();
        nameEmail.put("a","a");
        nameEmail.put("b","b");
        nameEmail.put("c","c");
        nameEmail.put("d","d");
        nameEmail.put("e","e");
        nameEmail.put("f","f");
        nameEmail.put("g","g");
        nameEmail.put("h","h");
        nameEmail.put("i","i");

        List<HashMap<String,String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this,listItems,R.layout.friend_list_text_view, new String[]{"First Line", "Second Line"}, new int[]{R.id.name,R.id.email});

        Iterator it = nameEmail.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String,String> resultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultMap.put("First Line", pair.getKey().toString());
            resultMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultMap);
        }

        resultsListView.setAdapter(adapter);

//        recyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        recyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        list = Arrays.asList(getResources().getStringArray(R.array.test)); //change to friend list in data base
//        // specify an adapter (see also next example)
//        mAdapter = new FriendAdapter(list);
//        recyclerView.setAdapter(mAdapter);
    }

    private class BackendTask extends AsyncTask<Void, Void, String> {
        String email;
        BackendTask(String email) {
            this.email = email;
        }
        @Override
        protected String doInBackground(Void... v) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email);
            String message = "";
            try {
                HttpResponse response = client.execute(request);
                JSONObject friendlist = new JSONObject(response.toString());


            } catch (java.io.IOException e) {
                Log.e("Error", "Connection Error");
            } catch (org.json.JSONException je) {
                Log.e("Error", "Invalid JSONObject");
            }
            return message;
        }


        protected void onProgressUpdate() {
        }

        protected void onPostExecute(String message) {

        }
    }

}

