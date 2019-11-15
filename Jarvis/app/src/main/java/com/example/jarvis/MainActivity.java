package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.engineio.client.Transport;
//import com.github.nkzawa.socketio.client.Manager;
//import com.github.nkzawa.socketio.client.Socket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.jetty.Main;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private Socket mSocket;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
//    private DatabaseReference RootRef; //For now it will be using Firebase Database However, we may want to change this root to AWS

    private FirebaseAuth.AuthStateListener mAuthListener;

    private int RC_SIGN_IN = 0;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        mAuth.addAuthStateListener(mAuthListener);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignInButton signin;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.sign_in_button);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
//        RootRef = FirebaseDatabase.getInstance().getReference();


        mSocket = ((jarvis) this.getApplication()).getmSocket();

        if(mSocket.connected()){
            Toast.makeText(MainActivity.this, "Connected Socket!!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "Can't connect to Socket...", Toast.LENGTH_LONG).show();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    sendUsertoHomeActivity();
                }
            }
        };

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId)
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Toast.makeText(MainActivity.this, "Connected to socket!", Toast.LENGTH_LONG).show();
            Log.i("socket", "connected!");
        }
    };

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                // Google Sign In was successful, authenticate with Firebase
                Log.d("Login", "Hello");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
//              handleSignInResult(task);
            }catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error", "Google sign in failed", e);
                Toast.makeText(MainActivity.this, "Google Sign In Failed", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        Log.d("Info", "firebaseAuthWithGoogle:" + acct.getId());

        final String idToken = acct.getIdToken();
        final String authCode = acct.getServerAuthCode();
        final String name = acct.getGivenName();
<<<<<<< HEAD
=======
        email = acct.getEmail();

        if(mSocket.connected()){
            Toast.makeText(MainActivity.this, "Connected Socket!!", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MainActivity.this, "Can't connect to Socket...", Toast.LENGTH_LONG).show();
        }

        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Exception e = (Exception) args[0];
                        Toast.makeText(MainActivity.this, "caught an error...", Toast.LENGTH_LONG).show();
                        Log.e("socket", "Transport Error: " + e);
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                    }
                });
            }
        });

       // new CommunicateBackend(idToken, authCode).execute();
>>>>>>> c84cc28458985c298609807f8d348e4972773303

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //new CommunicateBackend(idToken, authCode).execute();
                            JSONObject loginjson = new JSONObject();

                            currentUser = mAuth.getCurrentUser();
                            String UserID = currentUser.getUid();
                            if(mSocket.connected()){
                                Log.d("socket", "connection is fine");
                            }else{
                                Log.d("socket", "still not connected");
                            }
                            try {
                                Log.d("socket", "Sending stuff...");

                                loginjson.put("idToken", idToken);
                                loginjson.put("code", authCode);
                                loginjson.put("name", name);
                                loginjson.put("userID", UserID);

                                Log.d("socket", "Sent");
                            }catch(JSONException e){
                                Toast.makeText(MainActivity.this, "SignIn Failed", Toast.LENGTH_LONG).show();
                                Log.e("Error", "unable to send json object", task.getException());
                            }
                            Log.d("success", "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "json emitted...", Toast.LENGTH_LONG).show();
                            mSocket.emit("login", loginjson);
                            mSocket.on("login_response", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("socket", "waiting...");
                                            Toast.makeText(MainActivity.this, "Registered on the backend!!", Toast.LENGTH_LONG).show();
                                            sendUsertoHomeActivity();
                                        }
                                    });
                                }
                            });
//                            Log.d("socket", "here");
//                            sendUsertoHomeActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "SignIn Failed", Toast.LENGTH_LONG).show();
                            Log.w("Error", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /* Goes to home activity */
    private void sendUsertoHomeActivity(){
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }
}
