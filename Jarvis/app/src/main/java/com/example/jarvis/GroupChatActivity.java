package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton SendMessageButton;
    private EditText userMessage;
    private ScrollView mScrollview;
    private TextView displayTextMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        initializeFields();
    }

    private void initializeFields()
    {
        toolbar = findViewById(R.id.GroupChat_ToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GroupName");
        SendMessageButton = findViewById(R.id.GroupChat_sendButton);
        userMessage = findViewById(R.id.GroupChat_MessageToSend);
        mScrollview = findViewById(R.id.scrollview_groupchat);
        displayTextMessages = findViewById(R.id.GroupChat_TextDisplay);
    }


}
