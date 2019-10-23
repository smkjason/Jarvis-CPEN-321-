package com.example.jarvis;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoggedIn extends AppCompatActivity {

    ImageView UserPhoto;
    TextView Username, Useremail/*, UserID*/;
    Button Signout;

    Button Send;
    Button View_Calendar;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loggedin);

        String serverClientID = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
//                .requestServerAuthCode(serverClientID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        UserPhoto = findViewById(R.id.Userphoto);
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.usergmail);
        Signout = findViewById(R.id.sign_out_button);
        Send = findViewById(R.id.urlconnection);
        View_Calendar = findViewById(R.id.button);

        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // ...
                    case R.id.sign_out_button:
                        signOut();
                        break;

//                    case R.id.button:
//                        goToCalendar();
//                        break;// ...
                }
            }
        });

        View_Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.button:
                        goToCalendar();
                        break;
                }
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.urlconnection:
                        sendMessage();
                        break;
                }
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Username.setText(personName);
            Useremail.setText(personEmail);
            //UserID.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(UserPhoto);
        }
    }

    private void sendMessage(){

//        try {
//            URL url = new URL("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");
//            HttpURLConnection urlconnect = (HttpURLConnection) url.openConnection();
//
//            urlconnect.setRequestMethod("POST");
//            urlconnect.setDoOutput(true);
//            urlconnect.setChunkedStreamingMode(0);
//
//            OutputStream output = urlconnect.getOutputStream();
//            BufferedWriter writer_2 = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
//            writer_2.write("Hello Can I write??");
//
//        }catch (IOException e){
//            Log.w("ERROR", "Cant Connect to the URL");
//        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("Message", "Hello"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i("Error", "Signed in as: " + responseBody);
        } catch (ClientProtocolException e) {
            Log.e("Error", "Error sending ID token to backend.", e);
        } catch (IOException e) {
            Log.e("Error", "Error sending ID token to backend.", e);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoggedIn.this, "Logged Out", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void goToCalendar(){
        Intent intent = new Intent(LoggedIn.this, View_Calendar.class);
        startActivity(intent);
    }
}
