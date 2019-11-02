package com.example.jarvis.Fragments;

import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.example.jarvis.Adapter.CustomAdapter;
import com.example.jarvis.Model.Events;
import com.example.jarvis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EventFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";


    private DatabaseReference EventRef;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;


    private ArrayList<Events> mEvents;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Events", "Started events");
        /* For Reading from FirebaseDatabase */
        EventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
//        initEvents();
    }

    private void initEvents() {
        Log.i("Events", "Initializing Events");

        /* Get all the events from the Database */
        EventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
//                for(DataSnapshot postdataSnapshot : dataSnapshot.child("Events").getChildren()){
//
//                }
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.child("Events").getChildren().iterator();
                Events tempEvent = new Events();
                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                    tempEvent.setEvent_name(((DataSnapshot) iterator.next()).getKey());
                    mEvents.add(tempEvent);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Events", "loadEvents: Cancelled");
            }
        });

    }

    public EventFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        view.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        recyclerView = view.findViewById(R.id.events_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        mAdapter = new CustomAdapter(mEvents, getActivity());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initEvents();

//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
//        layoutManager = new LinearLayoutManager(view.getContext());

//        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

//        if (savedInstanceState != null) {
//            // Restore saved layout manager type.
//            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
//                    .getSerializable(KEY_LAYOUT_MANAGER);
//        }

//        //initizliaing recyclerview

        CustomAdapter customAdapter = new CustomAdapter(mEvents, getActivity());
        // Set CustomAdapter as the adapter for RecyclerView.
        recyclerView.setAdapter(customAdapter);
        // END_INCLUDE(initializeRecyclerView)
        // Inflate the layout for this fragment
        return view;
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

//        switch (layoutManagerType) {
//            case GRID_LAYOUT_MANAGER:
//                recyclerView.setLayoutManager(new GridLayoutManager());
//                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
//                break;
//            case LINEAR_LAYOUT_MANAGER:
//                recyclerView.setLayoutManager(new LinearLayoutManager());
//                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//                break;
//            default:
//                recyclerView.setLayoutManager(new LinearLayoutManager());
//                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//        }
//
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

}
