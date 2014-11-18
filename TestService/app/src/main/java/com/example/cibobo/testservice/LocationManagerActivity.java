package com.example.cibobo.testservice;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LocationManagerActivity extends Activity {
    private TextView textView;
    private Button button;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);

        locationManager = LocationManager.getInstant();
        locationManager.registerActivity(LocationManagerActivity.this);
        locationManager.connection();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location currentLocation = locationManager.getLastLocation();
                String outString = currentLocation.getLatitude() + "; " + currentLocation.getLongitude();
                textView.setText(outString);
            }
        });
    }

    @Override
    protected void onDestroy() {
        locationManager.disconnection();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_manager, menu);
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
