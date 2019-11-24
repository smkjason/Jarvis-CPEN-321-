package com.example.jarvis.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarvis.ChoosePreferredTime;
import com.example.jarvis.R;
import com.example.jarvis.jarvis_types.jarvisevent;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PendingEventsAdapter extends RecyclerView.Adapter{
    private static final String TAG = "PendingEventsAdapter";
    private ArrayList<jarvisevent> list_of_invited_events;
    private Context mContext;

    private String user_email, idToken, eventid;

    public PendingEventsAdapter(ArrayList<jarvisevent> list_of_invited_events, Context context, String idToken,
                                String user_email) {
        this.list_of_invited_events = list_of_invited_events;
        mContext = context;
        this.user_email = user_email;
        this.idToken = idToken;
    }

    public class PendingEVH extends RecyclerView.ViewHolder {
        public TextView event_name, admin, rank, length;
        public Button Accept_bttn, Decline_bttn;

        public PendingEVH(View view){
            super(view);

            event_name = view.findViewById(R.id.tvName_Pending);
            admin = view.findViewById(R.id.tvAdmin);
            rank = view.findViewById(R.id.tvRank);
            length= view.findViewById(R.id.tvLength_Pending);

            Accept_bttn = view.findViewById(R.id.Accept_pending_bttn);
            Decline_bttn = view.findViewById(R.id.Decline_bttn_pending);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_event_item, parent, false);
        return new PendingEVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        jarvisevent event = list_of_invited_events.get(position);
        final String name = event.getName_of_event();
        final String eventid = event.getTentative_event_id();
        ((PendingEVH) holder).event_name.setText(event.getName_of_event());
        ((PendingEVH) holder).admin.setText(event.getAdmin());
//        ((PendingEVH) holder).rank.setText(event.getName_of_event());
        ((PendingEVH) holder).length.setText(event.getLength());

        ((PendingEVH) holder).Accept_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Invitation Accepted!", Toast.LENGTH_LONG).show();
                list_of_invited_events.remove(position);
                notifyItemRemoved(position);
                Intent intent = new Intent(mContext, ChoosePreferredTime.class);
                intent.putExtra("eventid", eventid);
                intent.putExtra("eventname", name);
                intent.putExtra("email", user_email);
                intent.putExtra("idToken", idToken);
                mContext.startActivity(intent);
            }
        });

        ((PendingEVH) holder).Decline_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Invitation Declined!", Toast.LENGTH_LONG).show();
                new notifyDecline(user_email, idToken, eventid).execute();
                list_of_invited_events.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list_of_invited_events == null) ? 0 : list_of_invited_events.size();
    }

    private class notifyDecline extends AsyncTask<Void, Void, Void> {

        String email;
        String idToken;
        String eventid;

        notifyDecline(String email, String idToken, String eventid){
            this.email = email;
            this.idToken = idToken;
            this.eventid =eventid;
        }

        @Override
        protected Void doInBackground(Void... v) {
            JSONObject createresponse = new JSONObject();
            try {
                //TODO: Debug this... it doesn't print the Log in here...
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user/" + email + "/events/"
                        + eventid + "?decline=true");
                JSONObject json = new JSONObject();
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Authorization", "Bearer " + idToken);
                httpPost.setHeader("Content-Type", "application/json");
                Log.d(TAG, "Am I here...?");
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                createresponse = new JSONObject(responseBody);
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Toast.makeText(mContext, "Invitation Successfully Declined.", Toast.LENGTH_LONG).show();
            super.onPostExecute(v);
        }
    }


}
