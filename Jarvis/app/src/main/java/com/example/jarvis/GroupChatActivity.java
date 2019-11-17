package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jarvis.adapter.ChatBoxAdapter;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/* This is for the chatting (sending and receiving messages */
public class GroupChatActivity extends AppCompatActivity {

    private Button SendMessageButton;
    private EditText userMessage;

    private String currentEvent;
    private String currentUserID;
    private String currentUserName;
    private Toolbar toolbar;

    //new
    private Socket mSocket;
    public RecyclerView myRecylerView ;
    public List<jarvismessage> MessageList ;

    private String eventid;

    private static final String TAG = "GroupChat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getIncomingIntent();

        Log.d("GroupChat", "CurrentEvent Name: " + currentEvent);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        mSocket = ((jarvis) getApplication()).getmSocket();
        if(mSocket.connected()){
            Toast.makeText(GroupChatActivity.this, "FIRST:Socket ready for groupchat", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(GroupChatActivity.this, "FIRST: !Socket not ready for groupchat!", Toast.LENGTH_LONG).show();
        }
        toolbar = findViewById(R.id.chat_toolbar);
        initializeFields();

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
                    }
                });
            }
        });

    }

    private void initializeFields()
    {
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
            currentEvent = getIntent().getStringExtra("eventname");
        }

        if(getIntent().hasExtra("eventid")){
            this.eventid = getIntent().getStringExtra("eventid");
            Toast.makeText(GroupChatActivity.this, "event id: " + this.eventid, Toast.LENGTH_LONG).show();
        }

    }
}
