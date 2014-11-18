package com.cibobo.wooooo.provider;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.util.concurrent.ExecutionException;

/**
 * Created by Cibobo on 11/17/2014.
 */
public class LocationManager implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
    private final String tag = "Location Manager";

    private Activity activity;

    private LocationClient locationClient;

    private static LocationManager locationManagerInstant = null;

    private class UpdateLocationAsyncTask extends AsyncTask<LocationClient, Void, Location>{

        @Override
        protected Location doInBackground(LocationClient... locationClients) {
            Location location = null;
            LocationClient lClient = locationClients[0];
            if(lClient != null){
                location = lClient.getLastLocation();
            }
            return location;
        }
    }

    private LocationManager() {
    }

    public static LocationManager getInstant(){
        if(locationManagerInstant == null){
            locationManagerInstant = new LocationManager();
        }
        return locationManagerInstant;
    }

    public void registerActivity(Activity activity){
        this.activity = activity;
        locationClient = new LocationClient(activity,this, this);
    }

    public void connection(){
        if(servicesConnected()) {
            locationClient.connect();
        }
    }

    public void disconnection(){
        locationClient.disconnect();
    }

    //Add Async Task for the update of the last location, to avoid timeout in the main thread.
    public Location getLastLocation(){
        /*UpdateLocationAsyncTask updateLocationAsyncTask = new UpdateLocationAsyncTask();
        updateLocationAsyncTask.execute(locationClient);
        try {
            return updateLocationAsyncTask.get();
        } catch (InterruptedException e) {
            Log.e(tag, e.toString());
        } catch (ExecutionException e) {
            Log.e(tag, e.toString());
        }
        Log.e(tag, "Failed to update the location");
        return null;*/
        return locationClient.getLastLocation();
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                activity,
                9000);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            errorDialog.show();
        }
    }

    private boolean servicesConnected() {
        Log.d(tag, "Check Service Connected");
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(activity.getApplicationContext());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    activity,
                    9000);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                errorDialog.show();
            }
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(activity, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        Log.e(tag, "On connection failed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        activity,
                        9000);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }

    }
}
