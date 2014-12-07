package com.cibobo.wooooo.model;

import android.location.Location;
import android.util.Log;

import com.cibobo.wooooo.provider.LocationManager;

/**
 * Created by Cibobo on 11/18/2014.
 * The message data, which contains the current location information
 */
public class LocationMessageData{
    private final String tag = "Location Message Data";
    private Location location;

    public LocationMessageData(){
        location = LocationManager.getInstant().getLastLocation();
        Log.d(tag, "Constructor completed");
    }

    public String toString() {
        if(location != null) {
            String locationString = location.getLatitude() + ", " + location.getLongitude();
            Log.d(tag, locationString);
            return locationString;
        } else {
            Log.e(tag, "Location is null");
            return "Location is null";
        }
    }

    public double getLatitude(){
        return location.getLatitude();
    }

    public double getLongitude(){
        return location.getLongitude();
    }
}
