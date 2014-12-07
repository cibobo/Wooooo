package com.cibobo.wooooo.service.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by cibobo on 12/3/14.
 */
public class MasterMessageService extends Service
       implements Observer{
    private final String tag = "Master Message Service";

    private MasterMessageServiceBinder serviceBinder = new MasterMessageServiceBinder();

    public class MasterMessageServiceBinder extends Binder {
        public void startMasterMessageReceiver(){
            Log.d(tag, "Start the Message Receiver");
            /*
             * Register the message service into the MessageReceiveRunnable, so that the function Update will be called if
             * the receiver received any new message.
             */
            XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().addObserver(MasterMessageService.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.serviceBinder;
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    @Override
    public void onDestroy() {
        Log.d(tag, "Stop the Master Message Receiver");
        //Unregister the MessageService of the MessageReceiveRunnable.
        XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().deleteObserver(MasterMessageService.this);
        super.onDestroy();
    }
}
