package com.library.android.liblocation.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.library.android.liblocation.exception.ExceptionPermissionNotGranted;

/**
 * Class ManagerLocalizeLegacy
 * Method Legacy
 */
public class ManagerLocalizeLegacy extends ManagerLocalizeLocation implements android.location.LocationListener {

    // Provider
    String mProvider;
    // LocationManager
    LocationManager mLocationManager;

    /**
     * Constructor
     * @param ctx : context
     */
    public ManagerLocalizeLegacy(Context ctx, Criteria criteria) throws ExceptionPermissionNotGranted {
        super(ctx, criteria, LocalizeType.LEGACY);

        // init Location Manager
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        //Criteria criteria = new Criteria();
        // not ACCUCARY _HIGH don't know why but that cause problem for 4.1.2 :/ so that the api is 9 ...
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mProvider = mLocationManager.getBestProvider(mCriteria, false);
        checkPermission();
        Location location = mLocationManager.getLastKnownLocation(mProvider);

        updateLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        //update location
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        Log.i(getClass().getCanonicalName(), "LocationListener onStatusChanged " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Log.i(getClass().getCanonicalName(), "LocationListener onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Log.i(getClass().getCanonicalName(), "LocationListener onProviderDisabled");
    }

    @Override
    public void startRequest(byte typeRequest) {
        if (typeRequest == LOCALIZE_TYPE_REQUEST_ALL || typeRequest == LOCALIZE_TYPE_REQUEST_LOCATION) {
            // start request update
            // minTime, minDistance
            checkPermission();
            mLocationManager.requestLocationUpdates(mProvider, MIN_TIME, MIN_DISTANCE, this);
        }
    }

    @Override
    public void stopRequest(byte typeRequest) {
        if (typeRequest == LOCALIZE_TYPE_REQUEST_ALL || typeRequest == LOCALIZE_TYPE_REQUEST_LOCATION) {
            // remove request update
            checkPermission();
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void updateCriteria() {
        // TODO
    }
}