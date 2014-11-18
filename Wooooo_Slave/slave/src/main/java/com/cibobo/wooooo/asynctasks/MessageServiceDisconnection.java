package com.cibobo.wooooo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;

import java.sql.Connection;

/**
 * Created by Cibobo on 11/16/2014.
 * Disconnect the current connecting message services
 * Using the interface to implement
 */
public class MessageServiceDisconnection extends AsyncTask<ConnectionService, Void, Void> {
    private final String tag = "Message Service Disconnection Asynchronous Task";

    @Override
    protected Void doInBackground(ConnectionService[] messageService) {
        Log.d(tag, "do in back ground is called");
        if(messageService[0] != null){
            messageService[0].disconnection();
            Log.d(tag, "disconnection");
        } else {
            Log.e(tag, "Connection is null");
        }
        return null;
    }
}
