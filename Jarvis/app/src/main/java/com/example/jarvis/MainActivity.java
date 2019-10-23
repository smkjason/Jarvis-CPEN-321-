package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.mortbay.util.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               // .requestIdToken(serverClientId)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
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
        new BackendTask().execute();
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String idToken = account.getIdToken();
//            //String authCode = account.getServerAuthCode();
//
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");
//
//            try {
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
//                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                HttpResponse response = httpClient.execute(httpPost);
//                int statusCode = response.getStatusLine().getStatusCode();
//                final String responseBody = EntityUtils.toString(response.getEntity());
//                Log.i("Error", "Signed in as: " + responseBody);
//            } catch (ClientProtocolException e) {
//                Log.e("Error", "Error sending ID token to backend.", e);
//            } catch (IOException e) {
//                Log.e("Error", "Error sending ID token to backend.", e);
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


    private class BackendTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");

            try {
                HttpResponse response = client.execute(request);

// Get the response
                BufferedReader rd = new BufferedReader
                        (new InputStreamReader(
                                response.getEntity().getContent()));

                String line = "";
                while ((line = rd.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (java.io.IOException e) {
                Log.w("Error", "Connection Error");
            }
            return null;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute() {

        }
    }

}
