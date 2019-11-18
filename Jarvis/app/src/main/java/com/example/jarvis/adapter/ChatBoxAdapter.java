package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jarvis.R;
import com.example.jarvis.jarvis_types.jarvismessage;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatBoxAdapter  extends RecyclerView.Adapter {
    private List<jarvismessage> MessageList;
    private static final int SENT = 1;
    private static final int RECEIVED = 0;

    public ChatBoxAdapter(List<jarvismessage> MessagesList) {
        this.MessageList = MessagesList;
    }

    public class receivedViewHolder extends RecyclerView.ViewHolder{
        public TextView nickname;
        public TextView message;
        public TextView time;

        public receivedViewHolder(View view){
            super(view);

            nickname = view.findViewById(R.id.text_message_name);
            message = view.findViewById(R.id.text_message_body);
            time = view.findViewById(R.id.text_message_time);
        }
    }
    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView time;

        public MyViewHolder(View view) {
            super(view);

            message = view.findViewById(R.id.text_message_body);
            time = view.findViewById(R.id.text_message_time);
        }
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SENT){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_chatbox, parent, false);
            return new MyViewHolder(itemView);
        }else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatbox, parent, false);
            return new receivedViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        String name, message, time;

        jarvismessage jarvismessage = MessageList.get(position);
        name = MessageList.get(position).getSender();
        message = MessageList.get(position).getMessage();
        time = MessageList.get(position).getTime();

        if(jarvismessage.get_is_mine()){
            ((MyViewHolder) holder).message.setText(message);
            ((MyViewHolder) holder).time.setText(time);
        }else{
            ((receivedViewHolder) holder).nickname.setText(name);
            ((receivedViewHolder) holder).message.setText(message);
            ((receivedViewHolder) holder).time.setText(time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(MessageList.get(position).get_is_mine()){
            return SENT;
        }else{
            return RECEIVED;
        }
    }
}