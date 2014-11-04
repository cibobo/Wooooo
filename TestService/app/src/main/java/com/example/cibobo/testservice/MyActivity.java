package com.example.cibobo.testservice;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MyActivity extends Activity {
    private Button btnStart, btnInvoke, btnStop;
    MyService mService=null;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取服务上的IBinder对象，调用IBinder对象中定义的自定义方法，获取Service对象
            MyService.LocalBinder binder=(MyService.LocalBinder)service;
            mService=binder.getService();
         }
    };

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        btnStart = (Button) findViewById(R.id.btnStartSer);
        btnInvoke = (Button) findViewById(R.id.btnInvokeMethod);
        btnStop = (Button) findViewById(R.id.btnStopSer);

        btnStart.setOnClickListener(onclick);
        btnInvoke.setOnClickListener(onclick);
        btnStop.setOnClickListener(onclick);
    }

        View.OnClickListener onclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStartSer:
                   Toast.makeText(getApplicationContext(), "绑定服务成功", Toast.LENGTH_SHORT).show();
                   bindService(new Intent(MyActivity.this,MyService.class),mConnection, Service.BIND_AUTO_CREATE);
                   break;
               case R.id.btnInvokeMethod:
                   if(mService==null){
                        Toast.makeText(getApplicationContext(), "请先绑定服务", Toast.LENGTH_SHORT).show();
                        return;
                   }
                   // 调用绑定服务上的方法，进行数据交互
                   int iResult=mService.getMultipleNum(10);
                   Toast.makeText(getApplicationContext(), "服务计算结果为："+iResult, Toast.LENGTH_SHORT).show();
                   break;
               case R.id.btnStopSer:
                   Toast.makeText(getApplicationContext(), "服务解除绑定", Toast.LENGTH_SHORT).show();
                   unbindService(mConnection);
                   mService=null;
                   break;
                default:
                   break;
            }
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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