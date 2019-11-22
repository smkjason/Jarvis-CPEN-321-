package com.example.jarvis;

import android.os.Bundle;

import com.example.jarvis.adapter.PendingEventsAdapter;
import com.example.jarvis.jarvis_types.jarvisevent;

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

    public PendingEvents() {
    }

    private void getIncomingIntent(){
        //Maybe you need to know the user email
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invites);
        initializeFields();
    }

    private void initializeFields() {

        //TODO: Fetch all the invitations
        toolbar = findViewById(R.id.invites_ToolBar);

        recyclerView = findViewById(R.id.rvInvites);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Test Pending Event
        list_of_invited_events.add(new jarvisevent("Test Invitation", "Fake eventid", "Jason", "2:00"));

        PendingEventsAdapter pendingEventsAdapter = new PendingEventsAdapter(list_of_invited_events, PendingEvents.this);
        pendingEventsAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(pendingEventsAdapter);

    }


}
