package com.example.jarvis;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.adapter.FriendAdapter;

import java.util.Arrays;
import java.util.List;

public class FriendList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        recyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list = Arrays.asList(getResources().getStringArray(R.array.test)); //change to friend list in data base
        // specify an adapter (see also next example)
        mAdapter = new FriendAdapter(list);
        recyclerView.setAdapter(mAdapter);
    }

}

