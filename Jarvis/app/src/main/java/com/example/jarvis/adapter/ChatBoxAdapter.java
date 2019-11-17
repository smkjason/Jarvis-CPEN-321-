package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jarvis.R;
import com.example.jarvis.jarvismessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatBoxAdapter  extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {
    private List<jarvismessage> MessageList;

    public ChatBoxAdapter(List<jarvismessage> MessagesList) {
        this.MessageList = MessagesList;
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;
        public TextView time;

        public MyViewHolder(View view) {
            super(view);

            nickname = view.findViewById(R.id.text_message_name);
            message = view.findViewById(R.id.text_message_body);
            time = view.findViewById(R.id.text_message_time);
        }
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatbox, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        String name, message, time;
        name = MessageList.get(position).getSender();
        message = MessageList.get(position).getMessage();
        time = MessageList.get(position).getTime();

        holder.nickname.setText(name);
        holder.message.setText(message);
        holder.time.setText(time);
    }

}