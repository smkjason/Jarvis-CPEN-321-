package com.example.jarvis.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.IO;
import io.socket.client.Socket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jarvis.adapter.CustomAdapter;
import com.example.jarvis.R;

import com.google.firebase.database.DatabaseReference;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class EventFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    private DatabaseReference EventRef;

    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mEvents = new ArrayList<>();

    private Socket mSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Events", "Started events");
        try {
            mSocket = IO.socket("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/");
            mSocket.connect();
        }catch(URISyntaxException e){
            Log.e("socket", "URISyntaxException eventfrag");
        }
    }

    private void initEvents() {
        Log.i("Events", "Initializing Events");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        view.setTag(TAG);
        RecyclerView recyclerView = view.findViewById(R.id.events_recyclerview);
        recyclerView.setHasFixedSize(true);

        mAdapter = new CustomAdapter(mEvents, getActivity());

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

}
