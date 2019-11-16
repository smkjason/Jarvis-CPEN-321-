package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.content.Intent;
import android.os.AsyncTask;
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


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GoogleSignInClient mGoogleSignInClient;

    private Socket mSocket;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
//    private DatabaseReference RootRef; //For now it will be using Firebase Database However, we may want to change this root to AWS

    private FirebaseAuth.AuthStateListener mAuthListener;

    private int RC_SIGN_IN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignInButton signin;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.sign_in_button);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

//        RootRef = FirebaseDatabase.getInstance().getReference();

//        jarvis app = (jarvis) getApplication();
//        mSocket = app.getmSocket();
//        if(mSocket.connected()){
//            Toast.makeText(MainActivity.this, "Connected Socket!!", Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(MainActivity.this, "Can't connect to Socket...", Toast.LENGTH_LONG).show();
//        }

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


//
//        if(mSocket.connected()){
//            Toast.makeText(MainActivity.this, "Connected Socket!!", Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(MainActivity.this, "Can't connect to Socket...", Toast.LENGTH_LONG).show();
//        }
//
//        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Transport transport = (Transport) args[0];
//                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
//                    @Override
//                    public void call(Object... args) {
//                        Exception e = (Exception) args[0];
//                        Toast.makeText(MainActivity.this, "caught an error...", Toast.LENGTH_LONG).show();
//                        Log.e("socket", "Transport Error: " + e);
//                        e.printStackTrace();
//                        e.getCause().printStackTrace();
//                    }
//                });
//            }
//        });

        new CommunicateBackend(idToken, authCode).execute();
//        if(mSocket.connected()){
//            Toast.makeText(MainActivity.this, "Connected Socket!!", Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(MainActivity.this, "Can't connect to Socket...", Toast.LENGTH_LONG).show();
//        }

//        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Transport transport = (Transport) args[0];
//                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
//                    @Override
//                    public void call(Object... args) {
//                        Exception e = (Exception) args[0];
//                        Toast.makeText(MainActivity.this, "caught an error...", Toast.LENGTH_LONG).show();
//                        Log.e("socket", "Transport Error: " + e);
//                        e.printStackTrace();
//                        e.getCause().printStackTrace();
//                    }
//                });
//            }
//        });

       // new CommunicateBackend(idToken, authCode).execute();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            Log.d("success", "signInWithCredential:success");
                            sendUsertoHomeActivity();
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

    private class CommunicateBackend extends AsyncTask<Void, Void, Integer> {

        String idToken;
        String authCode;

        CommunicateBackend(String idToken, String authCode) {
            this.idToken = idToken;
            this.authCode = authCode;
        }

        @Override
        protected Integer doInBackground(Void... v) {
            int retval = 0;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user");

                JSONObject json = new JSONObject();
                json.put("idToken", idToken);
                json.put("code", authCode);
                Log.i("Information", "idToken is: " + idToken);
                Log.i("Information", "authCode is: " + authCode);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Content-Type", "application/json");

                HttpResponse response = httpClient.execute(httpPost);
                retval = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (Exception e) {
                Log.e("Error", "I caught some exception.", e);
            }
            return retval;
        }

        @Override
        protected void onPostExecute(Integer response) {
            Toast.makeText(MainActivity.this, "Sent stuff to backend on the background", Toast.LENGTH_LONG).show();
            super.onPostExecute(response);
            if(response == 200) {
                jarvis app = (jarvis) getApplication();
                mSocket = app.getmSocket();
                if(mSocket.connected()){
                    Toast.makeText(MainActivity.this, "Socket Connected after approval", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "got 200 back but can't connect", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(MainActivity.this, "response is not 200", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }

}
