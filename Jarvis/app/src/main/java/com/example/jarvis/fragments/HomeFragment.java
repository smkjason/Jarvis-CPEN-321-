package com.example.jarvis.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jarvis.CreateEvent;
import com.example.jarvis.MainActivity;
import com.example.jarvis.MapActivity;
import com.example.jarvis.PendingEvents;
import com.example.jarvis.R;
import com.example.jarvis.SelectTime;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.socket.client.Socket;

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

    private String idToken;
    private String user_email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        idToken = acct.getIdToken();
        user_email = acct.getEmail();
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

        Button My_events = getView().findViewById(R.id.my_events_bttn);
        Button create_event = getView().findViewById(R.id.create_event_bttn);
        Button Invitations = getView().findViewById(R.id.invitations_bttn);
        Button Mapp = getView().findViewById(R.id.Map_bttn);

        /* Testing Chat */

        acct = GoogleSignIn.getLastSignedInAccount(getActivity());



        My_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The events that this user is admin
                Intent intent = new Intent(getActivity(), SelectTime.class);
                startActivity(intent);
                //TODO: See all the responses? OR something
            }
        });

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEvent.class);
                //TODO: I need to get tentative eventid from this
                intent.putExtra("idToken", idToken);
                startActivity(intent);
            }
        });

        Invitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PendingEvents.class);
                intent.putExtra("idToken", idToken);
                intent.putExtra("email", user_email);
                startActivity(intent);
            }
        });

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
