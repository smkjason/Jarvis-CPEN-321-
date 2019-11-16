package com.example.jarvis;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class FriendList extends AppCompatActivity {
    ListView l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        l1 = (ListView)findViewById(R.id.listView);
    }

    public void get(View v) {
        Cursor cursor = getContentResolver().query(ContactsContract.)
    }

}

