package com.example.cibobo.testservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ThreadService extends Service {
    private final String tag="Test Multi Thread Service";

    private ThreadBinder threadBinder;

    private Timer timer;
    private TimerTask timerTask;

    private String currentTime;

    private Runnable runnable = new Runnable(){

        @Override
        public void run() {
            while(true){
                currentTime = String.valueOf(System.currentTimeMillis());
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public class ThreadBinder extends Binder{
        public ThreadService getService(){
            return ThreadService.this;
        }
    }

    public ThreadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.threadBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        Toast.makeText(MyActivity.this, "Service is started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

//        Log.i(tag, "Service starts");
//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                currentTime = String.valueOf(System.currentTimeMillis());
//                Log.i(tag, "get the current timestamp");
//            }
//        };
//        timer.schedule(timerTask,100);

        new Thread(runnable).start();
    }

    public String getCurrentTime(){
        return this.currentTime;
    }
}
