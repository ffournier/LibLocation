package com.library.android.liblocation.listener;

import android.location.Location;

/**
 * Class ILocationUpdate
 * Interface for Location Listener and Recognition
 */
public interface ILocationUpdate {

    /**
     * Update Location
     * @param loc : the new location
     */
    void updateLocation(Location loc);

    /**
     * Update Recognition
     * @param confidence : the new confidence
     * @param activityMode : the new activityMode
     */
    void updateRecognition(int confidence, int activityMode);
}