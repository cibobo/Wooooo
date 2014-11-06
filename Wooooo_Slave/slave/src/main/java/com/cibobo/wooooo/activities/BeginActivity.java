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

}
