package com.cibobo.wooooo.service.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageReceiveThread;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.google.android.gms.location.LocationClient;

/**
 * Service to running a message listener thread in background
 */
public class MessageService extends Service {
    private final String tag = "Message Service";

    private ConnectionService connectionService;

    private MessageServiceBinder messageServiceBinder = new MessageServiceBinder();

    public class MessageServiceBinder extends Binder {
        //Start a message listener thread, which will be called in onServiceConnected
        public void startMessageReceiver(){
            Log.d(tag, "Start the Message Receiver");
            XMPPInstantMessageService.getInstance().startReceiverThread();
        }

//        public void startLocationMessageReceiver(LocationClient inputClient){
//            XMPPInstantMessageService.getInstance().locationClient = inputClient;
//            XMPPInstantMessageService.getInstance().startReceiverThread();
//        }
    }

    public MessageService() {
        connectionService = XMPPInstantMessageService.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "Stop the Message Receiver");
        //The receiver thread should be stopped if the slave activity is destroyed.
        XMPPInstantMessageService.getInstance().stopReceiverThread();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.messageServiceBinder;
    }
}
