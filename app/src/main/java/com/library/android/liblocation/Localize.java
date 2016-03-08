package com.library.android.liblocation;
/**<ul>
 * <li>ForecastRestYahooSax</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo</li>
 * <li>28 mai 2014</li>
 *
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br>
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 *
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br>
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */



import android.content.Context;
import android.location.Criteria;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.library.android.liblocation.listener.ILocationUpdate;
import com.library.android.liblocation.manager.ManagerLocalizeGms;
import com.library.android.liblocation.manager.ManagerLocalizeLegacy;
import com.library.android.liblocation.manager.ManagerLocalizeLocation;


/**
 * Created by Mathias Seguy - Android2EE on 27/02/2015.
 * Class Localize
 * Get Location and ModeDrive
 *
 * To Use it : getInstance()
 * add Listener if you want
 * Next isntance.getmManagerLocalizeLocation()
 * if you want start Update call function startRequest(type)
 * Be careful call stopRequest at the end on all Request you start
 */
public class Localize {
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Singleton pattern
     */
    private static Localize instance = null;

    public static Localize getInstance(Context context) {
        return getInstance(context, null);
    }

    public static Localize getInstance(Context context, Criteria criteria) {
        if (instance == null) {
            instance = new Localize(context, criteria);
        } else {
            instance.setCriteria(criteria);
        }
        return instance;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////


    // declaration
    private Context mContext;
    // the method of Location GMS or LEGACY
    private ManagerLocalizeLocation mManagerLocalizeLocation;

    /**
     * Constructor
     * @param context
     */
    private Localize(Context context, Criteria criteria) {
        mContext = context;
        // test if Google Play services was available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (resultCode == ConnectionResult.SUCCESS){
            // Method GMS
            Log.i(getClass().getCanonicalName(), "GMS managerLocalizeLocation");
            mManagerLocalizeLocation = new ManagerLocalizeGms(mContext, criteria);
        }else{
            // Method legacy
            Log.i(getClass().getCanonicalName(), "legacy managerLocalizeLocation");
            mManagerLocalizeLocation = new ManagerLocalizeLegacy(mContext, criteria);
        }
    }

    /**
     * Get ManagerLocalizeLocation
     * @return managerLocalizeLocation
     */
    public ManagerLocalizeLocation getManagerLocalizeLocation() { return mManagerLocalizeLocation;}

    /**
     * Add LocationUpdate interface to list listener
     * @param listener
     */
    public void addLocationUpdate(ILocationUpdate listener) {
        mManagerLocalizeLocation.addLocationUpdate(listener);
    }

    /**
     * Remove LocationUpdate interface to list listener
     * @param listener
     */
    public void removeLocationUpdate(ILocationUpdate listener) {
        mManagerLocalizeLocation.removeLocationUpdate(listener);
    }

    public void setCriteria(Criteria criteria) {
        if (mManagerLocalizeLocation != null) {
            mManagerLocalizeLocation.setCriteria(criteria);
        }
    }
}