package com.cibobo.wooooo.service.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyService extends Service {
    public String testString;

    public final IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        long endTime = System.currentTimeMillis() + 5*1000;
        int i = 0;
        while(System.currentTimeMillis()<endTime){
            synchronized (this) {
                long currentTime = System.currentTimeMillis();
                try {
                    wait(endTime-currentTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                if(currentTime%1000 == 0) {
//                    Message msg = Message.obtain();
//                    msg.what = i++;
//                    Log.i("Test Service Message", "Service is running " + msg.what);
//                    this.testString = "Test String "+i++;
////                    this.handler.sendMessage(msg);
//                }
            }
        }
        this.testString = "Service is started";
    }

    public String getTestString(){
        return this.testString;
    }

    public class MyBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }
}
