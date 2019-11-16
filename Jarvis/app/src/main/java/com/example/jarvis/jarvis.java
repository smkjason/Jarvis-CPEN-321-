package com.example.jarvis;

import android.app.Application;

//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import org.mortbay.jetty.Main;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class jarvis extends Application {

    private Socket mSocket;
    private static final String URL = "http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/";
    IO.Options opts = new IO.Options();
    private String idToken;

    public void setidToken(String idToken){
        this.idToken = idToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
