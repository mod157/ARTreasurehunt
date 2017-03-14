package com.nammu.artreasurehunt.module;

import android.util.DisplayMetrics;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by SunJae on 2017-03-13.
 */

public class SocketIO {
    private String serverURL;
    private Socket socket;
    {
        try {
            socket = IO.socket("http://218.209.45.76:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public SocketIO(String url){
        serverURL = url;
        initSocketConnection();
    }

    public void initSocketConnection(){
        try {
            socket = IO.socket("http://"+serverURL);
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
        if(socket != null)
            socket.connect();
    }
    public void sendMessage(String name){
        JSONObject json = new JSONObject();
        try {
            json.put(name,"");
            socket.emit(name, json);
        } catch (JSONException e) {
            Log.v("socket error : ", e.toString());
        }
    }
    public void sendMessage(String name,String[] titles, String[] messages){
        JSONObject json = new JSONObject();
        try {
            for(int i = 0; i < titles.length; i++)
                json.put(titles[i],messages[i]);
            socket.emit(name, json);
        } catch (JSONException e) {
            Log.v("socket error : ", e.toString());
        }
    }
    public void socketListener(String name,Emitter.Listener listener){
        socket.on(name,listener);
    }
    public void socketDeconnetion(){
        socket.disconnect();
    }
}
