package com.example.jarvis.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jarvis.R;
import com.example.jarvis.jarvis_types.jarvis_pt;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PreferredTimeAdapter extends RecyclerView.Adapter {
    private static final String TAG = "PreferredTimeAdapter";
    private ArrayList<jarvis_pt> List_of_preferredTimes;

    public PreferredTimeAdapter(ArrayList<jarvis_pt> list_of_preferredTimes) {
        List_of_preferredTimes = list_of_preferredTimes;
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder{
        public TextView startTime, endTime;

        public TimeViewHolder(View view){
            super(view);

            startTime = view.findViewById(R.id.tvstartPreferredDate);
            endTime = view.findViewById(R.id.tvendPreferredDate);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preferredtime_item, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String startTime_, endTime_;
        jarvis_pt preferredTime;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd, yyyy h:mm");

        preferredTime = List_of_preferredTimes.get(position);
        startTime_ = preferredTime.getStartDatenTime();
        endTime_ = preferredTime.getEndDatenTime();

        String first = startTime_;
        String second = endTime_;

        Log.d(TAG, "starttime: " + first);
        Log.d(TAG, "endtime: " + second);

        ((TimeViewHolder) holder).startTime.setText(first);
        ((TimeViewHolder) holder).endTime.setText(second);
    }

    @Override
    public int getItemCount() {
        return List_of_preferredTimes.size();
    }
}
