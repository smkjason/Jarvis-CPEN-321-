package com.example.jarvis.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jarvis.Model.Events;
import com.example.jarvis.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

//    private ArrayList<String> eventNames = new ArrayList<>();
    private ArrayList<Events> events;
    private Context mContext;

    public CustomAdapter(ArrayList<Events> events, Context context){
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

        Events this_event = events.get(position);
       holder.eventName.setText(this_event.getEvent_name());
        if(this_event.getImageUrl() == null){
            holder.groupChat_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(this_event.getImageUrl()).into(holder.groupChat_image);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("eventItem", "Clicked on " + events.get(position).getEvent_name());

                Toast.makeText(mContext, "Going to Event Info...", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null!=events? events.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView eventName;
        public CircleImageView groupChat_image;
        RelativeLayout layout;

        public ViewHolder(View itemView){
            super(itemView);

            eventName = itemView.findViewById(R.id.event_name);
            groupChat_image = itemView.findViewById(R.id.event_name);
            layout = itemView.findViewById(R.id.event_item_layout);
        }

    }
}
