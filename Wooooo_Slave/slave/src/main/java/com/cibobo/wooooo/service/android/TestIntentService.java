package com.cibobo.wooooo.service.android;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cibobo.wooooo.activities.BeginActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TestIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.cibobo.wooooo.service.android.action.FOO";
    private static final String ACTION_BAZ = "com.cibobo.wooooo.service.android.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.cibobo.wooooo.service.android.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.cibobo.wooooo.service.android.extra.PARAM2";

    private Context context;

    private Handler handler;

    public String testString;

    private final IBinder binder = new TestIntentServiceLocalBinder();

    public TestIntentService() {
        super("TestIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long endTime = System.currentTimeMillis() + 5*1000;
        while(System.currentTimeMillis()<endTime){
            synchronized (this) {
                long currentTime = System.currentTimeMillis();
                try {
                    wait(endTime-currentTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Message msg = Message.obtain();
        msg.what = 1;
        Log.i("Test Service Message", "Service is running " + msg.what);
        this.testString = "Test String "+1;
        this.handler.sendMessage(msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public class TestIntentServiceLocalBinder extends Binder{
        public TestIntentService getService(){
            return TestIntentService.this;
        }
    }
}
