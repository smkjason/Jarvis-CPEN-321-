package com.example.jarvis.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.socket.client.Socket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jarvis.CreateEvent;
import com.example.jarvis.GroupChatActivity;
import com.example.jarvis.MainActivity;
import com.example.jarvis.MapActivity;
import com.example.jarvis.R;
//import com.example.jarvis.jarvis;
//import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class HomeFragment extends Fragment {

    private static final String TAG = "home";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    //Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Google Stuff
    private GoogleSignInClient mGoogleSignInClient;

    private Socket mSocket;

    private GoogleSignInAccount acct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        };
//
//        mSocket = ((jarvis) getApplication()).getmSocket();

        Button My_events = getView().findViewById(R.id.view_profile_bttn);
        Button create_event = getView().findViewById(R.id.create_event_bttn);
        Button chatrooms = getView().findViewById(R.id.go_to_chatroom_bttn);
        Button Mapp = getView().findViewById(R.id.Map_bttn);

        /* Testing Chat */

        acct = GoogleSignIn.getLastSignedInAccount(getActivity());



        My_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.jarvis.ViewProfile.class);
                startActivity(intent);
            }
        });

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEvent.class);
                startActivity(intent);
            }
        });

//        chatrooms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), GroupChatActivity.class);
//                startActivity(intent);
//            }
//        });

        if (isServicesOK()) { //check if map can operate with this phone
            Mapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG,"isServicesOK: checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but we can fix (e.g. version issue
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
