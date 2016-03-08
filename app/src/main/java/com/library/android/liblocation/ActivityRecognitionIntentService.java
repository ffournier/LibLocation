package com.library.android.liblocation;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 */
public class ActivityRecognitionIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ActivityRecognitionIntentService() {
        super("ActivityRecognitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            // Get the confidence percentage for the most probable activity
            int confidence = mostProbableActivity.getConfidence();
            // Get the type of activity
            int activityType = mostProbableActivity.getType();
            // set result in LocalizeGms
            Localize localize = Localize.getInstance(getApplicationContext());
            if (localize != null) {
                localize.getManagerLocalizeLocation().updateRecognition(confidence, activityType);
            }
        }
    }

}
