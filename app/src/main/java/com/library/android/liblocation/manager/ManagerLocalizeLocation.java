package com.library.android.liblocation.manager;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;
import com.library.android.liblocation.exception.ExceptionPermissionNotGranted;
import com.library.android.liblocation.listener.ILocationUpdate;
import com.library.android.liblocation.util.PermissionGranted;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mother Type Location
 */
public abstract class ManagerLocalizeLocation {

    /**
     * Constant to Request Type
     */
    public static final byte LOCALIZE_TYPE_REQUEST_NONE = 0; // NONE
    public static final byte LOCALIZE_TYPE_REQUEST_LOCATION = 1; // LOCATION
    public static final byte LOCALIZE_TYPE_REQUEST_RECOGNITION = 2; // RECOGNITION
    public static final byte LOCALIZE_TYPE_REQUEST_ALL = 3; // ALL

    protected static final byte LOCALIZE_TYPE_REQUEST_CACHE = LOCALIZE_TYPE_REQUEST_ALL;

    /**
     * Enum on Type Method Location
     */
    public enum LocalizeType {
        GMS, // GMS
        LEGACY // LEGACY
    }
    // Interval Time between request
    protected final int MIN_TIME = 10000;
    // min distance
    protected final int MIN_DISTANCE = 5;

    // Type Method Location
    private LocalizeType mType;
    // Context
    protected Context mContext;
    // Current Location
    private Location mLocation;
    // Current ActivityMode

    /*public static final int IN_VEHICLE

    The device is in a vehicle, such as a car.
    Constant Value: 0 (0x00000000)
    public static final int ON_BICYCLE

    The device is on a bicycle.
    Constant Value: 1 (0x00000001)
    public static final int ON_FOOT

    The device is on a user who is walking or running.
    Constant Value: 2 (0x00000002)
    public static final int RUNNING

    The device is on a user who is running. This is a sub-activity of ON_FOOT.
    Constant Value: 8 (0x00000008)
    public static final int STILL

    The device is still (not moving).
    Constant Value: 3 (0x00000003)
    public static final int TILTING

    The device angle relative to gravity changed significantly. This often occurs when a device is picked up from a desk or a user who is sitting stands up.
    Constant Value: 5 (0x00000005)
    public static final int UNKNOWN

    Unable to detect the current activity.
    Constant Value: 4 (0x00000004)
    public static final int WALKING

    The device is on a user who is walking. This is a sub-activity of ON_FOOT.
    Constant Value: 7 (0x00000007)*/

    private int mActivityMode;

    // Cuurent Confidence This value will be <= 100. It means that larger values
    // such as a confidence of >= 75 indicate that it's very likely that the detected activity is correct,
    // while a value of <= 50 indicates that there may be another activity that is just as or more likely.
    private int mConfidence;
    // List interface LocationUpdate
    private CopyOnWriteArrayList<ILocationUpdate> mListeners;

    protected Criteria mCriteria;


    /**
     * Constructor
     * @param ctx
     * @param type
     */
    public ManagerLocalizeLocation(Context ctx, Criteria criteria, LocalizeType type) throws ExceptionPermissionNotGranted {
        this.mType = type;
        this.mContext = ctx;
        this.mListeners = new CopyOnWriteArrayList<>();
        this.mLocation = null;
        this.mActivityMode = DetectedActivity.UNKNOWN;
        this.mConfidence = 0;
        this.mCriteria = criteria;

        checkPermission();
    }

    protected void checkPermission() throws ExceptionPermissionNotGranted {
        if (!PermissionGranted.checkPermissionGranted(mContext)) {
            throw new ExceptionPermissionNotGranted();
        }
    }

    /**
     * get LocalizeType
     * @return mType
     */
    public LocalizeType getType() {return mType; }

    /**
     * Add a listener to the list
     * @param listener : the listener
     */
    public void addLocationUpdate(ILocationUpdate listener) {
        mListeners.add(listener);
    }

    /**
     * Remove a listener to the list
     * @param listener : the listener
     */
    public void removeLocationUpdate(ILocationUpdate listener) {
        mListeners.remove(listener);
    }

    /**
     * Get Current Location
     * @return Current Location
     */
    public Location getLocation() {return mLocation; }

    /**
     * Get Activity Mode
     * @return Activity Mode
     */
    public int getActivityMode() {return mActivityMode; }

    /**
     * Get Confidence
     * @return Confidence
     */
    public int getConfidence() {return mConfidence;}

    /**
     * Set Criteria
     * @param criteria
     */
    public void setCriteria(Criteria criteria) {
        this.mCriteria = criteria;
        // TODO here update provider ?
        updateCriteria();
    }


    /**
     * Update Location
     * @param loc : the new loc
     */
    protected void updateLocation(Location loc) {
        if (loc == null)
            Log.i(getClass().getCanonicalName(), "updateLocation null");
        else
            Log.i(getClass().getCanonicalName(), "updateLocation " + loc.getLatitude() + "," + loc.getLongitude());

        mLocation = loc;
        if (mListeners != null && mListeners.size() > 0) {
            for (ILocationUpdate listener : mListeners) {
                listener.updateLocation(loc);
            }
        }
    }

    /**
     * Update Recognition
     * @param confidence : confidence
     * @param activityMode : activity Mode
     */
    public void updateRecognition(int confidence, int activityMode) {
        Log.i(getClass().getCanonicalName(), "confidence " + confidence + " activityMode " + activityMode);
        this.mActivityMode = activityMode;
        this.mConfidence = confidence;
        if (mListeners != null && mListeners.size() > 0) {
            for (ILocationUpdate listener : mListeners) {
                listener.updateRecognition(confidence, activityMode);
            }
        }
    }

    /**
     * Start Request Location
     * @param typeRequest : type of request
     * LOCALIZE_TYPE_REQUEST_NONE;
     * LOCALIZE_TYPE_REQUEST_LOCATION;
     * LOCALIZE_TYPE_REQUEST_RECOGNITION;
     * LOCALIZE_TYPE_REQUEST_ALL;
     */
    public abstract void startRequest(byte typeRequest);

    /**
     * Stop Request Location
     * @param typeRequest : type of request
     * LOCALIZE_TYPE_REQUEST_NONE;
     * LOCALIZE_TYPE_REQUEST_LOCATION;
     * LOCALIZE_TYPE_REQUEST_RECOGNITION;
     * LOCALIZE_TYPE_REQUEST_ALL;
     */
    public abstract void stopRequest(byte typeRequest);

    /**
     * Update Criteria
     */
    public abstract void updateCriteria();

}