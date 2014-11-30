package com.cibobo.wooooo.service.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import com.cibobo.wooooo.model.LocationMessageData;
import com.cibobo.wooooo.model.MessageData;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageReceiveRunnable;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageReceiveThread;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.google.android.gms.location.LocationClient;

import org.jivesoftware.smack.packet.Packet;

import java.util.Observable;
import java.util.Observer;


/**
 * Service to running a message listener thread in background
 */
public class MessageService extends Service implements Observer {
    private final String tag = "Message Service";

    private ConnectionService connectionService;

    private MessageServiceBinder messageServiceBinder = new MessageServiceBinder();

    /**
     * Reimplement the function from Observer, which will analyse the content of the Message
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        Log.d(tag, "Runnable has " + observable.countObservers() + " Observers");

        //If receive a MessageData
        if(o instanceof MessageData) {
            Log.d(tag, "Receive notification");
            MessageData message = (MessageData)o;

            //Create a location data for current location information.
            LocationMessageData locationData = new LocationMessageData();
            Log.d(tag, locationData.toString());

            //Create a answer including the current location info.
            MessageData answer = new MessageData(message.getReceiver(),"cibobo2005@gmail.com", locationData.toString());

            //Send the answer back to the sender.
            XMPPInstantMessageService.getInstance().sendMessage(answer);
        } else {
            Log.e(tag, "Get the wrong type of the notificaton");
        }
    }

    public class MessageServiceBinder extends Binder {
        //Start a message listener thread, which will be called in onServiceConnected
        public void startMessageReceiver(){
            Log.d(tag, "Start the Message Receiver");
            /*
             * Register the message service into the MessageReceiveRunnable, so that the function Update will be called if
             * the receiver received any new message.
             */
            XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().addObserver(MessageService.this);
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
        //Unregister the MessageService of the MessageReceiveRunnable.
        XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().deleteObserver(MessageService.this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.messageServiceBinder;
    }
}
