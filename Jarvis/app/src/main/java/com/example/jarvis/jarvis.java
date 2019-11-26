package com.example.jarvis;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
//
//import io.socket.client.IO;
//import io.socket.client.Socket;

public class jarvis extends Application {
    private static final String URL = "http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/";
    private static final String MSG_CHANNEL_ID = "New Message";

    private Socket mSocket;
    IO.Options opts = new IO.Options();
    private String idToken;

    public void setidToken(String idToken){
        this.idToken = idToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    MSG_CHANNEL_ID,
                    "New Message",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setDescription("Testing Notification");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        opts.query = "idToken=" + idToken;

        try{
            mSocket = IO.socket(URL);
            mSocket.connect();
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public Socket getmSocket() {
        return mSocket;
    }
}
