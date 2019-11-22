package com.example.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jarvis.adapter.ChatBoxAdapter;
import com.example.jarvis.jarvis_types.jarvismessage;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/* This is for the chatting (sending and receiving messages */
public class GroupChatActivity extends AppCompatActivity {
    private static final String TAG = "GroupChat";
    private static final String MSG_CHANNEL_ID = "New Message";

    private NotificationManagerCompat notificationManagerCompat;

    private int message_id = 0;
    //XML
    private Button SendMessageButton;
    private EditText userMessage;
    public RecyclerView myRecylerView ;
    private Toolbar toolbar;
    
    private String currentEvent;
    private String currentUserID;
    private String currentUserName;

    //new
    private Socket mSocket;
    public List<jarvismessage> MessageList ;
    private String eventid;
    private String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getIncomingIntent();

        notificationManagerCompat = NotificationManagerCompat.from(this);

        Log.d("GroupChat", "CurrentEvent Name: " + currentEvent);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        mSocket = ((jarvis) getApplication()).getmSocket();

        initializeFields();
        new load_old_messages(eventid, idToken);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "SECOND: Why aren't I getting chat socket here?!", Toast.LENGTH_LONG).show();
                sendMessageInfoToDatabase();
                userMessage.setText("");
            }
        });

        mSocket.on(this.eventid + ".message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotmessage = "";
                        String sender = "";
                        JSONObject jsonObject = (JSONObject) args[0];
                        try{
                            gotmessage = jsonObject.getString("message");
                            sender = jsonObject.getString("sender");
                        }catch (JSONException e){
                            Log.e("json", "debugging json");
                        }
                        jarvismessage newmsg = new jarvismessage(sender, gotmessage);
                        MessageList.add(newmsg);
                        ChatBoxAdapter chatBoxAdapter = new ChatBoxAdapter(MessageList);
                        chatBoxAdapter.notifyDataSetChanged();
                        myRecylerView.setAdapter(chatBoxAdapter);
                        myRecylerView.scrollToPosition(MessageList.size()-1);
                        int size = MessageList.size();
                        Toast.makeText(GroupChatActivity.this, "Getting messages: " + gotmessage, Toast.LENGTH_LONG).show();
                        sendMessageNotification(++message_id, gotmessage);
                    }
                });
            }
        });

    }

    private void initializeFields()
    {
        toolbar = findViewById(R.id.chat_toolbar);
        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.reyclerview_message_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        ChatBoxAdapter chatBoxAdapter = new ChatBoxAdapter(MessageList);
        chatBoxAdapter.notifyDataSetChanged();
        myRecylerView.setAdapter(chatBoxAdapter);

        SendMessageButton = findViewById(R.id.button_chatbox_send);
        userMessage = findViewById(R.id.edittext_chatbox);

        Log.d(TAG, "eventname: " + currentEvent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentEvent);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void sendMessageInfoToDatabase()
    {
        JSONObject msgjson = new JSONObject();
        String message = userMessage.getText().toString();
        //Message is empty
        if(mSocket.connected()){
            Toast.makeText(GroupChatActivity.this, "SECOND: Socket ready for groupchat", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(GroupChatActivity.this, "SECOND: !Socket not ready for groupchat!", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Oops!", Toast.LENGTH_LONG).show();
        }else {
            SimpleDateFormat currentDateformat = new SimpleDateFormat("MMM dd, yyyy");
            SimpleDateFormat currentTimeformat = new SimpleDateFormat("hh:mm a");
            String currentDate = currentDateformat.format(Calendar.getInstance().getTime());
            String currentTime = currentTimeformat.format(Calendar.getInstance().getTime());
            try {
                msgjson.put("name", currentUserName);
                msgjson.put("message", message);
                msgjson.put("date", currentDate);
                msgjson.put("time", currentTime);
                mSocket.emit(this.eventid + ".send", msgjson);
            } catch (JSONException e) {
                Log.e("Error", "Failed making json object");
            }
            Toast.makeText(GroupChatActivity.this, "Message sent to server" + message, Toast.LENGTH_LONG).show();
        }
    }

    private void getIncomingIntent()
    {
        Log.d("GroupChat", "getIncomingIntent from EventFragment");
        if(getIntent().hasExtra("Name")){
            Log.d("GroupChat", "Has Extras");
        }

        if(getIntent().hasExtra("eventid")){
        this.eventid = getIntent().getStringExtra("eventid");
        }

        if(getIntent().hasExtra("idToken")){
            this.idToken = getIntent().getStringExtra("idToken");
        }
    }

    private class load_old_messages extends AsyncTask<Void, Void, JSONArray>{

        String eventid;
        String idToken;

        public load_old_messages(String eventid, String idToken) {
            this.eventid = eventid;
            this.idToken = idToken;
        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            JSONArray jsonArray = new JSONArray();
            HttpGet httpGet = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/events/" + eventid + "/messages/");
            try{
                httpGet.addHeader("Authorization", "Bearer " + idToken);
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String json_string = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(json_string);
            } catch (IOException e){
                Log.e(TAG, "IOException caught", e);
            } catch (JSONException e){
                Log.e(TAG, "JSONException caught", e);
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            JSONObject cur;
            if(jsonArray == null || jsonArray.length() == 0){
                Toast.makeText(GroupChatActivity.this, "something wrong with jsonArray", Toast.LENGTH_LONG).show();
            }
            else{
                for(int index = 0; index < jsonArray.length(); index++)
                {
                    try{
                        cur = jsonArray.getJSONObject(index);
                        jarvismessage message = new jarvismessage(cur.getString("sender"), cur.getString("message"));
                        MessageList.add(message);
                    }catch (JSONException e){
                        Log.e(TAG, "JSONException caught", e);
                    }
                }
                ChatBoxAdapter chatBoxAdapter = new ChatBoxAdapter(MessageList);
                chatBoxAdapter.notifyDataSetChanged();
                myRecylerView.setAdapter(chatBoxAdapter);
                myRecylerView.scrollToPosition(MessageList.size() - 1);
            }
        }
    }

    public void sendMessageNotification(int msg_id, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GroupChatActivity.this, MSG_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Message")
                .setContentText(message)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        notificationManagerCompat.notify(msg_id, builder.build());
    }
}
