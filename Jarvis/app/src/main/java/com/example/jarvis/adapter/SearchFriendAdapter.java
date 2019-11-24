package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarvis.R;
import com.example.jarvis.jarvis_types.FriendItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.SearchFriendViewHolder> {
    private static ClickListener mListener;

    public static class SearchFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mUsername;


        public SearchFriendViewHolder(View itemView, final ClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mUsername = itemView.findViewById(R.id.username);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SearchFriendAdapter.mListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
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
