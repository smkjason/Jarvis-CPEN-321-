package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.FriendItem;
import com.example.jarvis.R;
import com.example.jarvis.SelectTimeItem;

import java.util.ArrayList;

public class SelectTimeAdapter extends RecyclerView.Adapter<SelectTimeAdapter.SelectTimeViewHolder> {
    private SelectTimeAdapter.OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemCLick(int position);
    }
    public void setOnItemClickListener(SelectTimeAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public static class SelectTimeViewHolder extends RecyclerView.ViewHolder {
        public TextView mDate;
        public TextView mTime;
        public TextView mSelected;


        public SelectTimeViewHolder(View itemView, final SelectTimeAdapter.OnItemClickListener listener) {
            super(itemView);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);
            mSelected = itemView.findViewById(R.id.selected);

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

    private ArrayList<SelectTimeItem> mTimesList;

    public SelectTimeAdapter(ArrayList<SelectTimeItem> friendList) {
        mTimesList = friendList;
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
        holder.mDate.setText(currentItem.getDate());
        holder.mTime.setText(currentItem.getTime());
        holder.mSelected.setText(currentItem.getSelected());
    }

    @Override
    public int getItemCount() {
        return mTimesList.size();
    }
}
