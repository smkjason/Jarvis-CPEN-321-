package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.adapter.ChatBoxAdapter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class GroupChatActivity extends AppCompatActivity {

    private ImageButton SendMessageButton;
    private EditText userMessage;
    private TextView displayTextMessages;

    private String currentEvent;
    private String currentUserID;
    private String currentUserName;

    //new
    private Socket socket;
    public RecyclerView myRecylerView ;
    public List<JSONObject> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public  EditText messagetxt ;
    public Button send ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Log.d("GroupChat", "CurrentEvent Name: " + currentEvent);

        getIncomingIntent();

        Toast.makeText(GroupChatActivity.this, currentEvent, Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        initializeFields();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageInfoToDatabase();
                userMessage.setText("");
            }
        });

        socket.on("receive msg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(GroupChatActivity.this, args[0].toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initializeFields()
    {
        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        SendMessageButton = findViewById(R.id.GroupChat_sendButton);
        userMessage = findViewById(R.id.GroupChat_MessageToSend);
        try{
            socket = IO.socket("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");
            socket.connect();
            socket.emit("join", currentUserName);
        }catch(URISyntaxException e){
            e.printStackTrace();
        }

    }


    private void sendMessageInfoToDatabase()
    {
        JSONObject msgjson = new JSONObject();
        String message = userMessage.getText().toString();
        //Message is empty
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
                socket.emit("send msg", msgjson);
            } catch (JSONException e) {
                Log.e("Error", "Failed making json object");
            }
            Toast.makeText(GroupChatActivity.this, "Message sent to server", Toast.LENGTH_LONG).show();
        }
    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        socket.on("receive msg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(GroupChatActivity.this, args[0].toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getIncomingIntent()
    {
        Log.d("GroupChat", "getIncomingIntent from EventFragment");
        if(getIntent().hasExtra("Name")){
            Log.d("GroupChat", "Has Extras");

            currentEvent = getIntent().getStringExtra("Name");
        }

    }

}
