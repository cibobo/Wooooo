package com.cibobo.wooooo.service.android;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

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
    }
}
