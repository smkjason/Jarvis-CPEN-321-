package com.example.jarvis.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.GroupChatActivity;
import com.example.jarvis.R;
import com.example.jarvis.jarvisevent;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<jarvisevent> events;
    private Context mContext;


    public CustomAdapter(ArrayList<jarvisevent> events, Context context){
        this.events = events;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("eventItem", "onBindViewHolder: called.");

        jarvisevent this_event = events.get(position);
        holder.eventName.setText(this_event.getName_of_event());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("eventItem", "Clicked on " + events.get(position));
                Toast.makeText(mContext, "Going to Event Info...", Toast.LENGTH_LONG).show();
                /* Go to GroupChat on Click */
                Intent intent = new Intent (mContext, GroupChatActivity.class);
                intent.putExtra("eventid", events.get(position).getEventid());
                intent.putExtra("Name", events.get(position).getName_of_event());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != events ? events.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView eventName;
        private RelativeLayout layout;

        ViewHolder(View itemView){
            super(itemView);

            eventName = itemView.findViewById(R.id.event_name);
            layout = itemView.findViewById(R.id.event_item_layout);
        }

    }
}
