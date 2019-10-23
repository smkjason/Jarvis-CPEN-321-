package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.mortbay.util.IO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    SignInButton signin;
    int RC_SIGN_IN = 0;
//    String CHANNEL_ID = "See Channel";
//
//    /* Create the Notification Channel */
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            /* Set Channel Name */
//            CharSequence name = getString(R.string.channel_name);
//            /* Set Channel Description */
//            String description = getString(R.string.channel_description);
//            /* Must set importance for Android 8.0 or above */
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//            /* Create the Channel */
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(getString(R.string.Title))
//                .setContentText(getString(R.string.Content))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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

        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });
    }

    /* Add this if we would like */
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
//    }

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
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            String authCode = account.getServerAuthCode();

//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            new communicateBackend(idToken, authCode).execute();


//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/test_user");
//
//            try {
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                UrlEncodedFormEntity urlencodedformentity = new UrlEncodedFormEntity(nameValuePairs, "utf-8");
//                urlencodedformentity.setContentType("application/json");
//                nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
//                nameValuePairs.add(new BasicNameValuePair("AuthCode", authCode));
//                httpPost.setEntity(urlencodedformentity);
//                httpPost.setHeader("Content-Type", "application/json");
//
//                HttpResponse response = httpClient.execute(httpPost);
//                int statusCode = response.getStatusLine().getStatusCode();
//                final String responseBody = EntityUtils.toString(response.getEntity());
//                Log.i("Error", "Signed in as: " + responseBody);
//            } catch (ClientProtocolException e) {
//                Log.e("Error", "Error sending ID token to backend.", e);
//            } catch (IOException e) {
//                Log.e("Error", "Error sending ID token to backend.", e);
//            }catch (Exception e){
//                Log.e("Error", "I caught some exception.", e);
//            }
             //returns a one-time server auth code to send to your web server which can be exchanged for access token and sometimes refresh token if requestServerAuthCode(String) is configured; null otherwise. for details.
            // Signed in successfully, show authenticated UI.
            Intent intent = new Intent(MainActivity.this, LoggedIn.class);
            startActivity(intent);

//            /* Send the authentication code to backend server */
//            try{
//                URL url = new URL("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");
//                HttpURLConnection URLconnection = (HttpURLConnection) url.openConnection();
//                URLconnection.setRequestMethod("POST");
//                URLconnection.setDoOutput(true);
//                URLconnection.setChunkedStreamingMode(0);
//
//                OutputStream output = URLconnection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
//                writer.write(authCode);
//
//                URLconnection.disconnect();
//            }catch (IOException e){
//                Log.w("Error", "Connection Error");
//            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            //Maybe Add another UI
            //updateUI(null);
        }
    }

    private class communicateBackend extends AsyncTask<Void, Void, Void>{

        String idToken;
        String authCode;
        communicateBackend(String idToken, String authCode){
            this.idToken = idToken;
            this.authCode = authCode;
        }

        @Override
        protected Void doInBackground(Void... v) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/test_user");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                UrlEncodedFormEntity urlencodedformentity = new UrlEncodedFormEntity(nameValuePairs, "utf-8");
                urlencodedformentity.setContentType("application/json");
                nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
                Log.i("Information", "idToken is: " + idToken);
                Log.i("Information", "urlencodedformentity is: " + urlencodedformentity);
                nameValuePairs.add(new BasicNameValuePair("AuthCode", authCode));
                Log.i("Information", "authCode is: " + authCode);
                httpPost.setEntity(urlencodedformentity);
                httpPost.setHeader("Content-Type", "application/json");

                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i("Information", "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e("Error", "Error sending ID token to backend.", e);
            }catch (Exception e){
                Log.e("Error", "I caught some exception.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Sent stuff to backend on the background", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);

        }
    }


}
