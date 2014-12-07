package com.cibobo.wooooo.service.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cibobo.wooooo.model.LocationMessageData;
import com.cibobo.wooooo.model.MessageData;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.cibobo.wooooo.slave.R;

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
     * @param obj
     */
    @Override
    public void update(Observable observable, Object obj) {
        Log.d(tag, "Runnable has " + observable.countObservers() + " Observers");

        //If receive a MessageData
        if(obj instanceof MessageData) {
            Log.d(tag, "Receive notification");
            MessageData message = (MessageData)obj;

            if(message.getContent().toString().equals("cibobo")){
                //Create a location data for current location information.
                LocationMessageData locationData = new LocationMessageData();
                Log.d(tag, locationData.toString());

                //Get the partner User name from SharedPreference
                //TODO: temporary solution for the partner input
                String savedPartnerName = this.getSharedPreferences(getString(R.string.login_data_preference), Context.MODE_PRIVATE)
                        .getString(this.getString(R.string.login_partnerName), "");

                //TODO: Check whether the message is coming from target partner
                //Create a answer including the current location info.
                MessageData answer = new MessageData(message.getReceiver().getUserName(), savedPartnerName, locationData.toString());

                //Send the answer back to the sender.
                XMPPInstantMessageService.getInstance().sendMessage(answer);

            } else {
                Log.e(tag, "Message is not equal to cibobo");
            }
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
