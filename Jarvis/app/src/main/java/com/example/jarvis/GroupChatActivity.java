package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.Notifications.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.entity.StringEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import androidx.appcompat.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton SendMessageButton;
    private EditText userMessage;
    private ScrollView mScrollview;
    private TextView  Title;
    private TextView displayTextMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, EventRef, GroupMessageKey ;

    private String currentEvent, currentUserID, currentUserName,
            currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Log.d("GroupChat", "CurrentEvent Name: " + currentEvent);


        getIncomingIntent();

        Toast.makeText(GroupChatActivity.this, currentEvent, Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        EventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(currentEvent);

        initializeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageInfoToDatabase();
                userMessage.setText("");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        EventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeFields()
    {
        toolbar = findViewById(R.id.GroupChat_ToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentEvent);
        SendMessageButton = findViewById(R.id.GroupChat_sendButton);
        userMessage = findViewById(R.id.GroupChat_MessageToSend);
        mScrollview = findViewById(R.id.scrollview_groupchat);
        displayTextMessages = findViewById(R.id.GroupChat_TextDisplay);
        Title = findViewById(R.id.Toolbar_title);
        Title.setText(currentEvent);
    }

    private void GetUserInfo() {

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d("GroupChat", "Something went wrong with getting User Info from Firebase...");
            }
        });
    }


    private void SendMessageInfoToDatabase()
    {
        String message = userMessage.getText().toString();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupChatActivity.this, "Oops!", Toast.LENGTH_LONG).show();
        }
        else {
            String messageKey = EventRef.push().getKey();
            Calendar date = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(date.getTime());

            Calendar time = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(time.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            EventRef.updateChildren(groupMessageKey);

            GroupMessageKey = EventRef.child(messageKey);

            HashMap<String, Object> messageMAP = new HashMap<>();
            messageMAP.put("Name", currentUserName);
            messageMAP.put("Message", message);
            messageMAP.put("Date", currentDate);
            messageMAP.put("Time", currentTime);
            GroupMessageKey.updateChildren(messageMAP);

            Toast.makeText(GroupChatActivity.this, "Message sent to server", Toast.LENGTH_LONG).show();
        }
    }
    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime =(String) ((DataSnapshot) iterator.next()).getValue();

            displayTextMessages.append(chatName + ":\n" + chatMessage + "\n" + chatTime +  "     " + chatDate + "\n\n");
            Toast.makeText(GroupChatActivity.this, "Got Messages: " + chatMessage + chatDate + chatName + chatTime, Toast.LENGTH_LONG).show();
        }
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
