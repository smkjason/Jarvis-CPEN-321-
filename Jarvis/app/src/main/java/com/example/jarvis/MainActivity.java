package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef; //For now it will be using Firebase Database However, we may want to change this root to AWS

    private FirebaseAuth.AuthStateListener mAuthListener;

    private int RC_SIGN_IN = 0;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignInButton signin;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.sign_in_button);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

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
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
//                handleSignInResult(task);
            }catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error", "Google sign in failed", e);
                // ...
            }

        }
    }

//    /* Probably won't be needing this since moving login to Firebase */
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            idToken = account.getIdToken();
//            authCode = account.getServerAuthCode();
//
//            new CommunicateBackend(idToken, authCode).execute();
//
//             //returns a one-time server auth code to send to your web server which can be exchanged for access token and sometimes refresh token if requestServerAuthCode(String) is configured; null otherwise. for details.
//            // Signed in successfully, show authenticated UI.
//            sendUsertoHomeActivity();
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
//            //Maybe Add another UI
//            //updateUI(null);
//        }
//    }

    private class CommunicateBackend extends AsyncTask<Void, Void, Void> {

        private String idToken;
        private String authCode;

        CommunicateBackend(String idToken, String authCode) {
            this.idToken = idToken;
            this.authCode = authCode;
        }

        @Override
        protected Void doInBackground(Void... v) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user");

                JSONObject json = new JSONObject();
                json.put("idToken", idToken);
                json.put("code", authCode);
                Log.i("Information", "idToken is: " + idToken);
//                Log.i("Information", "urlencodedformentity is: " + urlencodedformentity);
                Log.i("Information", "authCode is: " + authCode);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Content-Type", "application/json");

                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
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

        protected void onPostExecute() {
            //Maybe Implemented
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Sent stuff to backend on the background", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
            //Maybe Implemented
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String idToken;
        String authCode;

        Log.d("Info", "firebaseAuthWithGoogle:" + acct.getId());

        idToken = acct.getIdToken();
        authCode = acct.getServerAuthCode();
        final String name = acct.getGivenName();

        new CommunicateBackend(idToken, authCode).execute();
        
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /* Add the currentUser to the FirebaseDatabase */
                            currentUser = mAuth.getCurrentUser();
                            String UserID = currentUser.getUid();
                            RootRef.child("Users").child(UserID).child("Name").setValue(name);
                            RootRef.child("Users").child(UserID).child("UID").setValue(UserID);
                            RootRef.child("Users").child(UserID).child("Latitude").setValue("NA");
                            RootRef.child("Users").child(UserID).child("Longitude").setValue("NA");
                            // Sign in success, update UI with the signed-in user's information
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
}
