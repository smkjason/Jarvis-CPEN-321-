package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.jarvis.adapter.SelectTimeAdapter;
import com.example.jarvis.jarvis_types.SelectTimeItem;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//List of events that this user created (Admin)
public class SelectTime extends AppCompatActivity {
    private static final String TAG = "SelectTime";

    //XML
    private RecyclerView mRecyclerView;
    private SelectTimeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Information
    ArrayList<SelectTimeItem> timesList = new ArrayList<>();
    private String event_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time);

        //TODO: Collect all the Inivitations sent to this user.
        //TODO: SET eventid;
        timesList.add(new SelectTimeItem("2019-11-30 16:00", "2019-11-30 20:00"));
        timesList.add(new SelectTimeItem("2019-12-05 03:00", "2019-12-05 10:00"));
        timesList.add(new SelectTimeItem("2019-12-24 12:00", "2019-12-25 12:00"));

        mRecyclerView = findViewById(R.id.select_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SelectTimeAdapter(timesList, SelectTime.this, event_id);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SelectTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                confirmEvent(position);
            }
        });
    }

    public void confirmEvent(int position){ //send selected time to server to finialize
        timesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        Toast.makeText(SelectTime.this,"Event created",Toast.LENGTH_LONG);

       // gotoHome();
    }

    public void gotoHome() {
        Intent intent = new Intent(SelectTime.this, Home.class);
        startActivity(intent);
    }
}
