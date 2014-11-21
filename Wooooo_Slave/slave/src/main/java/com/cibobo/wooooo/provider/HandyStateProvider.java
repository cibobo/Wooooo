package com.cibobo.wooooo.provider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.cibobo.wooooo.slave.R;

/**
 * Created by Cibobo on 11/20/2014.
 * Provider to provide the current state of the handy.
 */
public class HandyStateProvider {
    private final String tag = "Handy State Provider";

    private static HandyStateProvider providerInstant = null;

    private static Context context;

    public static HandyStateProvider getInstant(Context inputContext){
        if(providerInstant == null){
            providerInstant = new HandyStateProvider();
        }
        context = inputContext;
        return providerInstant;
    }

    public boolean isLocationServiceActivated(){
        android.location.LocationManager manager =
                (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSActivated = manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetWorkActivated = manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        Log.d(tag, "Location Provider state: " + isGPSActivated + ", " + isNetWorkActivated);

        return (isGPSActivated || isNetWorkActivated);
    }

    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
