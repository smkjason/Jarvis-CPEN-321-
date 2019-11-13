package com.example.jarvis;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketAWS extends Application {

    private Socket mSocket;
    private static final String URL = "http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/";

    @Override
    public void onCreate() {
        super.onCreate();

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
