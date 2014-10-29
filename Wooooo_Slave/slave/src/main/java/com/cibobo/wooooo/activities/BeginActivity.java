package com.cibobo.wooooo.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.service.android.MessageService;
import com.cibobo.wooooo.service.android.MyService;
import com.cibobo.wooooo.service.android.TestIntentService;
import com.cibobo.wooooo.slave.R;

import java.io.Serializable;


public class BeginActivity extends ActionBarActivity {
    //Log title
    private String BEGIN_ACTIVITY_LOG_TITLE = "BeginActivity";

    private Button beginSlaveButton;
    private Button beginMasterButton;

    private Context context;
    //private Intent serviceIntent;

    public MessageServiceHandler messageServiceHandler;

    private TestIntentService testService = null;
    //private MyService testService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            testService = ((TestIntentService.TestIntentServiceLocalBinder)iBinder).getService();
//            MyService.MyBinder myBinder = (MyService.MyBinder)iBinder;
//            testService = myBinder.getService();
            if(testService==null){
                Toast.makeText(context,"Service is null", Toast.LENGTH_SHORT);
            }
            testService.setHandler(messageServiceHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            testService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        context = this;

        messageServiceHandler = new MessageServiceHandler(Looper.getMainLooper());

        Intent serviceIntent = new Intent(context, TestIntentService.class);
        //Intent serviceIntent = new Intent(BeginActivity.this, MyService.class);
        bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
       // this.startService(serviceIntent);

        beginSlaveButton = (Button) this.findViewById(R.id.buttonSlave);
        beginMasterButton = (Button) this.findViewById(R.id.buttonMaster);

        /*
         * Test code for Thread+service
         */
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                if(testService==null){
//                    Toast.makeText(context,"Service is null", Toast.LENGTH_SHORT);
//                    Log.e("Test Service", "Test service is null");
//                }
//                beginSlaveButton.setText(testService.testString);
//            }
//        };
//        this.messageServiceHandler.postDelayed(r, 400L);

        beginSlaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testService==null){
                    Toast.makeText(getApplicationContext(),"Service is null", Toast.LENGTH_SHORT).show();
                    Log.e("Test Service", "Test service is null");
                }
                Toast.makeText(context,testService.testString, Toast.LENGTH_SHORT).show();
//                Intent slaveIntent = new Intent(context, SlaveActivity.class);
//                context.startActivity(slaveIntent);
             //XMPPInstantMessageService.getInstance().sendMessage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.begin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler to handle the change request of the UI from the MessageService
     */
    private final class MessageServiceHandler extends Handler{
        public MessageServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("Begin Activity Message", "Service is running "+msg.what);
            Toast.makeText(context, "Handle the message from Message Service "+msg.what, Toast.LENGTH_SHORT).show();
        }
    }
}
