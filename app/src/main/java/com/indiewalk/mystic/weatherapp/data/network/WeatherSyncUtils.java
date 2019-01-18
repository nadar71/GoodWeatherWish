package com.indiewalk.mystic.weatherapp.data.network;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.indiewalk.mystic.weatherapp.data.provider.WeatherContract;

import java.util.concurrent.TimeUnit;

public class WeatherSyncUtils {


    private static boolean sInitialized;

    // Interval at which to sync with the weather.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    // Sync tag to identify sync job
    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";


    /**
     * Schedules repeating sync
     * @param context
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the periodic synch Job
        Job syncSunshineJob = dispatcher.newJobBuilder()
                // service used to sync data
                .setService(WeatherFirebaseJobService.class)
                // set the UNIQUE tag used to identify this Job
                .setTag(SUNSHINE_SYNC_TAG)

                 // Network constraints on which this Job should run.
                 // Run on any network
                 // TODO : include a preference for this: wifi, while charging etc.
                 //
                .setConstraints(Constraint.ON_ANY_NETWORK)
                // setLifetime. Options : forever" or  die the next time the device boots up.
                .setLifetime(Lifetime.FOREVER)
                // the Job to recur.
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                // If a Job with the tag  already exists, replace
                .setReplaceCurrent(true)
                .build();

        // Schedule  Job
        dispatcher.schedule(syncSunshineJob);
    }




    /**
     * Periodic sync tasks.
     * Checks if an immediate sync is required and in case do it.
     * @param context
     */
    synchronized public static void initialize(@NonNull final Context context) {
        //Only perform initialization once per app lifetime.
        if (sInitialized) return;

        sInitialized = true;

        // Create task to synchronize weather data periodically.
        scheduleFirebaseJobDispatcherSync(context);

        // Check to see if our weather ContentProvider is empty, in background
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground( Void... voids ) {

                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                // Need only to cjeck if there are data, id count suffice
                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();

                // Query to check if there are data or not
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);

                // if cursors is null, sync
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();
                return null;
            }
        }.execute();
    }


    /** Immediate sync using an IntentService for asynchronous execution.
     *  @param context
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
