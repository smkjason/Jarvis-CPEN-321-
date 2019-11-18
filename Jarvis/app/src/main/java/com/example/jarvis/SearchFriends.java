package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.adapter.SearchFriendAdapter;

import java.util.ArrayList;

public class SearchFriends extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SearchFriendAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<FriendItem> friendList = new ArrayList<>();
    ArrayList<String> addedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friends);

        Intent intent = getIntent();
        addedList = intent.getStringArrayListExtra("Added Friends");

        friendList.add(new FriendItem(R.drawable.ic_android,"Username1"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username2"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username3"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username4"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username5"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username6"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username7"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username8"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username9"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username10"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username11"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username12"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username13"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username14"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username15"));
        friendList.add(new FriendItem(R.drawable.ic_android,"Username16"));


        mRecyclerView = findViewById(R.id.search_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SearchFriendAdapter(friendList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SearchFriendAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                addedFriend(position);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        Log.d("Back","going back");
        i.putExtra("Added Friends", addedList); //need to change array
        setResult(RESULT_OK,i);
        super.onBackPressed();
        finish();
    }

    public void newSearch(){
        friendList.removeAll(friendList);
        mAdapter.notifyDataSetChanged();
    }

    public void addedFriend(int position){
        if (!addedList.contains(friendList.get(position).getFriend())) {
            addedList.add(friendList.get(position).getFriend());
        }
        friendList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

}
