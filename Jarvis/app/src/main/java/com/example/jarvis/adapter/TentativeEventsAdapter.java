package com.example.jarvis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;
import com.example.jarvis.TentativeEvents;
import com.example.jarvis.jarvis_types.TentativeEventItem;

import java.util.ArrayList;

public class TentativeEventsAdapter extends RecyclerView.Adapter<TentativeEventsAdapter.TentativeEventsViewHolder> {
    private TentativeEventsAdapter.OnItemClickListener mListener;
    private Context mContext;
    private String event_id;

    public interface OnItemClickListener {
        void onItemCLick(int position);
    }
    public void setOnItemClickListener(TentativeEventsAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public static class TentativeEventsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mDeadline;


        public TentativeEventsViewHolder(View itemView, final TentativeEventsAdapter.OnItemClickListener listener) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tentative_title);
            mDeadline = itemView.findViewById(R.id.deadline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemCLick(position);
                        }
                    }
                }
            });
        }
    }

    private ArrayList<TentativeEventItem> mEventsList;

    public TentativeEventsAdapter(ArrayList<TentativeEventItem> eventList, Context mContext) {
        this.mContext = mContext;
        mEventsList = eventList;
    }

    @Override
    public TentativeEventsAdapter.TentativeEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tentative_event_item, parent, false);
        TentativeEventsAdapter.TentativeEventsViewHolder tentativeEventsViewHolder = new TentativeEventsAdapter.TentativeEventsViewHolder(v, mListener);
        return tentativeEventsViewHolder;
    }

    @Override
    public void onBindViewHolder(TentativeEventsAdapter.TentativeEventsViewHolder holder, int position) {
        TentativeEventItem currentItem = mEventsList.get(position);
        holder.mTitle.setText(currentItem.getTitle());
        holder.mDeadline.setText(currentItem.getDeadline());
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }
}
