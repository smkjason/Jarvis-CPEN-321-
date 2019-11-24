package com.example.jarvis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.R;
import com.example.jarvis.jarvis_types.SelectTimeItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SelectTimeAdapter extends RecyclerView.Adapter<SelectTimeAdapter.SelectTimeViewHolder> {
    private SelectTimeAdapter.OnItemClickListener mListener;
    private Context mContext;
    private String event_id;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(SelectTimeAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public static class SelectTimeViewHolder extends RecyclerView.ViewHolder {
        public TextView mStart;
        public TextView mEnd;


        public SelectTimeViewHolder(View itemView, final SelectTimeAdapter.OnItemClickListener listener) {
            super(itemView);
            mStart = itemView.findViewById(R.id.start);
            mEnd = itemView.findViewById(R.id.end);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private ArrayList<SelectTimeItem> mTimesList;

    public SelectTimeAdapter(ArrayList<SelectTimeItem> timesList, Context mContext) {
        this.mContext = mContext;
        mTimesList = timesList;
    }

    @Override
    public SelectTimeAdapter.SelectTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_time_item, parent, false);
        SelectTimeAdapter.SelectTimeViewHolder selectTimeViewHolder = new SelectTimeAdapter.SelectTimeViewHolder(v, mListener);
        return selectTimeViewHolder;
    }

    @Override
    public void onBindViewHolder(SelectTimeAdapter.SelectTimeViewHolder holder, int position) {
        SelectTimeItem currentItem = mTimesList.get(position);
        holder.mStart.setText(currentItem.getStart());
        holder.mEnd.setText(currentItem.getEnd());
    }

    @Override
    public int getItemCount() {
        return mTimesList.size();
    }
}
