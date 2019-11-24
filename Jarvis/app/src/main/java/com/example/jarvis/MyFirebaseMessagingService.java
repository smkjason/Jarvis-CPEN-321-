package com.example.jarvis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";
    private static final String MSG_CHANNEL_ID = "New Message";

    private static int NOTIFICATION_ID = 1;
    private NotificationManagerCompat notificationManagerCompat;

    public MyFirebaseMessagingService() {
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        generateNotification(remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getTitle());
    }

    private void generateNotification(String body, String title) {
        //createNotificationChannel();
        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri setSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MSG_CHANNEL_ID)
                .setSmallIcon(R.drawable.jarvis_logo)
                .setContentText(body)
                .setContentTitle(title)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        if(NOTIFICATION_ID > 1000000){
            NOTIFICATION_ID = 0;
        }

        notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(NOTIFICATION_ID++, builder.build());
        Log.d(TAG, "Why no notification?");
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //new sendRegistrationToServer(token).execute();
    }

    private class sendRegistrationToServer extends AsyncTask<Void, Void, Integer> {

        String idToken;
        String FCMToken;

        sendRegistrationToServer(String FCMToken) {
            this.FCMToken = FCMToken;
            this.idToken = idToken;
        }

        @Override
        protected Integer doInBackground(Void... v) {
            int retval = 0;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/user");

                JSONObject json = new JSONObject();
                json.put("idToken", idToken);
                json.put("FCMToken", FCMToken);
                Log.i("Information", "idToken is: " + idToken);
                httpPost.setEntity(new StringEntity(json.toString()));
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Authorization", "Bearer " + idToken);
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
            super.onPostExecute(response);
            JSONObject authenticate_json = new JSONObject();

        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MSG_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
