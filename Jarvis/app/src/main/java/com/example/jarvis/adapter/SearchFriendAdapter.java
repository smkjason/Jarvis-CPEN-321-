package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.FriendItem;
import com.example.jarvis.R;

import java.util.ArrayList;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.SearchFriendViewHolder> {
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemCLick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class SearchFriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mUsername;

        public SearchFriendViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mUsername = itemView.findViewById(R.id.username);

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

    private ArrayList<FriendItem> mfriendList;

    public SearchFriendAdapter(ArrayList<FriendItem> friendList) {
        mfriendList = friendList;
    }

    @Override
    public SearchFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        SearchFriendViewHolder searchFriendViewHolder = new SearchFriendViewHolder(v, mListener);
        return searchFriendViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchFriendViewHolder holder, int position) {
        FriendItem currentItem = mfriendList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mUsername.setText(currentItem.getFriend());
    }

    @Override
    public int getItemCount() {
        return mfriendList.size();
    }
}
