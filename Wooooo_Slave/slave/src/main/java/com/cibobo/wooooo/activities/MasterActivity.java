package com.cibobo.wooooo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cibobo.wooooo.model.LocationMessageData;
import com.cibobo.wooooo.model.MessageData;
import com.cibobo.wooooo.provider.LocationManager;
import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;
import com.cibobo.wooooo.slave.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;


/**
 * Master Activity to show the slave's position in GoogleMap.
 * In order to use the Google Map API, you need a development key.
 * Go to the Google API console, and create such key with your SHA-1 fingerprint, which can be found
 * in the debug.keystore.
 * Type: keytool -list -v -alias androiddebugkey -keystore debug.keystore -storepass android -keypass android
 * under your android folder.
 */
public class MasterActivity extends Activity
        implements Observer{
    private final String tag = "Master Activity";

    private Context context;

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng AHAUSEN = new LatLng(47.7240644, 9.3279032);

    private GoogleMap map;

    private SharedPreferences sharedPreferences;

    private LatLng masterLatLng;
    private LatLng slaveLatLng = new LatLng(53.558, 9.927);;

    private Marker slaveMarker;

    private Button callButton;

    /*
     * Create handler to update the slave position in map.
     * @message: AsyncTask doesn't work in this situation. Reason is unknown.
     */
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        context = this;
        sharedPreferences = this.getSharedPreferences(getString(R.string.login_data_preference), Context.MODE_PRIVATE);

        //Register the current activity to the LocationManager
        LocationManager.getInstant().registerActivity(MasterActivity.this);
        LocationManager.getInstant().connection();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.master_map))
                .getMap();

        slaveMarker = map.addMarker(new MarkerOptions().position(slaveLatLng)
                .title(getString(R.string.master_slave_position_title)));


        //Call master location to get the current location of master
        //TODO: following code to get the current location should be corrected. Met an exception now. Using Ahausen as a test version
//        LocationMessageData masterLocation = new LocationMessageData();
//        masterLatLng = new LatLng(masterLocation.getLatitude(), masterLocation.getLongitude());
        Marker masterMarker = map.addMarker(new MarkerOptions()
                .position(AHAUSEN)
                .title(getString(R.string.master_master_position_title))
                .snippet("")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher_alternative)));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(slaveLatLng, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        callButton = (Button) findViewById(R.id.master_map_call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String requestMessage = "cibobo";
                //Set sender of Master mode as the login user.
                String sender = sharedPreferences.getString(context.getString(R.string.login_username), "");
//                String receiver = "ninzainar@gmail.com";
                //TODO: temporary solution for the partner input
                String receiver = sharedPreferences.getString(context.getString(R.string.login_partnerName), "");
                MessageData request = new MessageData(sender, receiver, requestMessage);
                XMPPInstantMessageService.getInstance().sendMessage(request);
            }
        });

        //Register current observer to XMPPInstantmessageReceiveRunnable
        XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstant().disconnection();
        //Remove the current activity from the observer list
        XMPPInstantMessageService.getInstance().getMessageReceiveRunnable().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master_map, menu);
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
     * MasterActivity is registered in XMPPInstantMessageReceiver. If there is any message received update function will be called.
     * Redraw the slave position on the map
     * @param observable
     * @param obj
     */
    @Override
    public void update(Observable observable, Object obj) {
        Log.d(tag, "Update called");
        if(obj instanceof MessageData){
            MessageData message = (MessageData)obj;
            String locationString = message.getContent().toString();
            String []component = locationString.split(", ");
            if(component.length == 2){
                Log.d(tag, Double.parseDouble(component[0]) + ", " + Double.parseDouble(component[1]));

                //Create the LatLng with the current position of slave
                final LatLng currentSlaveLatLng = new LatLng(Double.parseDouble(component[0]),
                        Double.parseDouble(component[1]));

                //Multithreading during post function of Handler.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        slaveMarker.setPosition(currentSlaveLatLng);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentSlaveLatLng, 15));
                    }
                });

            } else {
                Log.e(tag, "Get wrong location message");
            }
        } else {
            Log.e(tag, "Get wrong type of the message");
        }
    }
}
