package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.adapter.SearchFriendAdapter;
import com.example.jarvis.adapter.SelectTimeAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

public class SelectTime extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SelectTimeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<SelectTimeItem> timesList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time);

        timesList.add(new SelectTimeItem("December 1 2019", "8:00 pm", ""));

        mRecyclerView = findViewById(R.id.select_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SelectTimeAdapter(timesList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SelectTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                confirmEvent(position);
            }
        });


    }

    public void confirmEvent(int position){
        timesList.get(position).selected();
        mAdapter.notifyItemChanged(position);

        Toast.makeText(SelectTime.this,"Event created",Toast.LENGTH_LONG);

        gotoHome();
    }

    public void gotoHome() {
        Intent intent = new Intent(SelectTime.this, Home.class);
        startActivity(intent);
    }


}
