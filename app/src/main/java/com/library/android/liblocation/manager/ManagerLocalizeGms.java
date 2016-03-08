package com.library.android.liblocation.manager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.library.android.liblocation.ActivityRecognitionIntentService;
import com.library.android.liblocation.exception.ExceptionPermissionNotGranted;

/**
 * Class ManagerLocalizeGms
 * Method GMS
 */
public class ManagerLocalizeGms extends ManagerLocalizeLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // declaration
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingIntent mPendingIntent;
    byte mTypeRequest;


    /**
     * Constructor
     * @param ctx : context
     */
    public ManagerLocalizeGms(Context ctx, Criteria criteria) throws ExceptionPermissionNotGranted {
        super(ctx, criteria, LocalizeType.GMS);

        checkPermission();

        // init GMS Client
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // getLast Location
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        updateLocation(location);
        // init
        mTypeRequest = LOCALIZE_TYPE_REQUEST_NONE;
        mLocationRequest = null;
        mPendingIntent = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // update Location
        updateLocation(location);
    }


    @Override
    public void startRequest(byte typeRequest) {
        // change type.
        mTypeRequest |= typeRequest;
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            // Connect the client.
            mGoogleApiClient.connect();
        } else if (mGoogleApiClient.isConnected()) {
            // update the right request
            switch(mTypeRequest) {
                case LOCALIZE_TYPE_REQUEST_ALL:
                    startRequestLocation();
                    startRequestRecognition();
                    break;
                case LOCALIZE_TYPE_REQUEST_LOCATION:
                    startRequestLocation();
                    break;
                case LOCALIZE_TYPE_REQUEST_RECOGNITION:
                    startRequestRecognition();
                    break;
            }
        }
    }

    @Override
    public void stopRequest(byte typeRequest) {
        // remove the right request
        switch(typeRequest) {
            case LOCALIZE_TYPE_REQUEST_ALL:
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                } catch(Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
                try {
                    ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, mPendingIntent);
                } catch(Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
                mLocationRequest = null;
                mPendingIntent = null;
                break;
            case LOCALIZE_TYPE_REQUEST_LOCATION:
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                } catch(Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
                mLocationRequest = null;
                break;
            case LOCALIZE_TYPE_REQUEST_RECOGNITION:
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                } catch(Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
                mLocationRequest = null;
                break;
        }
        // inverse byte
        mTypeRequest &= ~typeRequest & LOCALIZE_TYPE_REQUEST_CACHE;

        // disconnect the client
        if (mTypeRequest == LOCALIZE_TYPE_REQUEST_NONE) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void updateCriteria() {
        // TODO
    }

    /**
     * Start Request location
     */
    private void startRequestLocation() {
        // test if Location Request was already created
        if (mLocationRequest == null) {
            // create it
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(MIN_TIME);

            // start request
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Start Request Recognition
     */
    private void startRequestRecognition() {
        // test if Pending intent was already created
        if (mPendingIntent == null) {
            // Create an Intent pointing to the IntentService
            Intent intent = new Intent(mContext, ActivityRecognitionIntentService.class);
            /*
            * Return a PendingIntent to start the IntentService.
            * Always create a PendingIntent sent to Location Services
            * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
            * again updates the original. Otherwise, Location Services
            * can't match the PendingIntent to requests made with it.
            */
            mPendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mGoogleApiClient, MIN_TIME, mPendingIntent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // start Request
        switch(mTypeRequest) {
            case LOCALIZE_TYPE_REQUEST_ALL:
                startRequestLocation();
                startRequestRecognition();
                break;
            case LOCALIZE_TYPE_REQUEST_LOCATION:
                startRequestLocation();
                break;
            case LOCALIZE_TYPE_REQUEST_RECOGNITION:
                startRequestRecognition();
                break;
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO Auto-generated method stub
        Log.i(getClass().getCanonicalName(), "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Auto-generated method stub
        Log.i(getClass().getCanonicalName(), "GoogleApiClient connection has failed");
    }
}
