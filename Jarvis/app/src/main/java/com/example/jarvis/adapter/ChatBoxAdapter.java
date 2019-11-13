package com.example.jarvis.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jarvis.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatBoxAdapter  extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {
    private List<JSONObject> MessageList;

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;


        public MyViewHolder(View view) {
            super(view);

            nickname = (TextView) view.findViewById(R.id.nickname);
            message = (TextView) view.findViewById(R.id.message);

        }
    }
// in this adaper constructor we add the list of messages as a parameter so that
// we will passe  it when making an instance of the adapter object in our activity



    public ChatBoxAdapter(List<JSONObject>MessagesList) {

        this.MessageList = MessagesList;


    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }
    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatbox, parent, false);

        return new ChatBoxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        String name, message;
        //binding the data from our ArrayList of object to the item.xml using the viewholder
        try{
            name = MessageList.get(position).getString("name");
            message = MessageList.get(position).getString("message");
            holder.nickname.setText(name);
            holder.message.setText(message);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

}