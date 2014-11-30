package com.cibobo.wooooo.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cibobo.wooooo.asynctasks.MessageServiceDisconnection;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageReceiveRunnable;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.slave.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class BeginActivity extends ActionBarActivity {
    //Log title
    private final String tag = "BeginActivity";

    private final int MAX_THREAD_COUNT = 1;

    private Button beginSlaveButton;
    private Button beginMasterButton;

    private Context context;

    //Thread pool containing all necessary runnable, which is keep on active during the whole life of the APP
    private ExecutorService executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        context = this;

        beginSlaveButton = (Button) this.findViewById(R.id.buttonSlave);
        beginMasterButton = (Button) this.findViewById(R.id.buttonMaster);


        beginSlaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent slaveIntent = new Intent(context, SlaveActivity.class);
                context.startActivity(slaveIntent);
            }
        });

        beginMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent masterIntent = new Intent(context, MasterActivity.class);
                context.startActivity(masterIntent);
            }
        });

        //Create thread pool to running the necessary task in other thread
        executor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Start the MessageReceiveRunnable.
         * This runnable will always running in the background during the whole life of the APP
         */
        executor.execute(XMPPInstantMessageService.getInstance().getMessageReceiveRunnable());
    }

    @Override
    protected void onDestroy() {
        //Shut down all thread in thread pool
        executor.shutdown();
        try {
            executor.awaitTermination((long)100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(tag, e.toString());
        }

        MessageServiceDisconnection messageServiceDisconnection = new MessageServiceDisconnection();
        messageServiceDisconnection.execute(XMPPInstantMessageService.getInstance());
        super.onDestroy();
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



}
