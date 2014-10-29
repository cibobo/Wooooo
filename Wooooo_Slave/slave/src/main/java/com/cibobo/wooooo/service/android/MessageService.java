package com.cibobo.wooooo.service.android;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;

import java.util.Timer;
import java.util.TimerTask;

//TODO: rewrite with a handler, which can manage the different process
//See http://davanum.wordpress.com/2007/12/31/android-just-use-smack-api-for-xmpp/
//& http://developer.android.com/guide/components/services.html
//& http://www.vogella.com/tutorials/AndroidServices/article.html
public class MessageService extends Service {
    private Timer timer;
    private TimerTask timerTask;

    private Context context;

    private Looper messageServiceLooper;
    private ServiceHandler messageServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5*1000;
            Toast.makeText(context, "Wait until: " + endTime, Toast.LENGTH_SHORT);
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job

            stopSelf(msg.arg1);
        }
    }

    //Message: there must be a constructor without any parameter,
    //otherwise you will get a compiler error: java.lang.RuntimeException: Unable to instantiate service
    public MessageService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        messageServiceLooper = thread.getLooper();
        messageServiceHandler = new ServiceHandler(messageServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = messageServiceHandler.obtainMessage();
        msg.arg1 = startId;
        messageServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
