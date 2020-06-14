package com.example.sunshine.sync;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.sunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

// TODO (9) Create a class called SunshineSyncUtils
public class SunshineSyncUtils {

    //  TODO (10) Add constant values to sync Sunshine every 3 - 4 hours
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_HOURS / 3;
    private static boolean sInitialized;

//  TODO (11) Add a sync tag to identify our sync job

    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    //  TODO (12) Create a method to schedule our periodic weather sync
    static void scheduleFirebaseJobDispatcherSync(Context context) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_HOURS,
                        SYNC_INTERVAL_HOURS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
    }

    //  TODO (1) Declare a private static boolean field called sInitialized
    //  TODO (2) Create a synchronized public static void method called initialize
    synchronized public static void initialize(@NonNull final Context context) {

        //  TODO (3) Only execute this method body if sInitialized is false
        if (sInitialized) return;
        //  TODO (4) If the method body is executed, set sInitialized to true
        sInitialized = true;

        //  TODO (5) Check to see if our weather ContentProvider is empty
//      TODO (13) Call the method you created to schedule a periodic weather sync
        scheduleFirebaseJobDispatcherSync(context);

        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                /* URI for every row of weather data in our weather table*/
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                /*
                 * Since this query is going to be used only as a check to see if we have any
                 * data (rather than to display data), we just need to PROJECT the ID of each
                 * row. In our queries where we display data, we need to PROJECT more columns
                 * to determine what weather details need to be displayed.
                 */
                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();

                /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                /*
                 * A Cursor object can be null for various different reasons. A few are
                 * listed below.
                 *
                 *   1) Invalid URI
                 *   2) A certain ContentProvider's query method returns null
                 *   3) A RemoteException was thrown.
                 *
                 * Bottom line, it is generally a good idea to check if a Cursor returned
                 * from a ContentResolver is null.
                 *
                 * If the Cursor was null OR if it was empty, we need to sync immediately to
                 * be able to display data to the user.
                 */

                //  TODO (6) If it is empty or we have a null Cursor, sync the weather now!
                if (null == cursor || (cursor).getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Make sure to close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });
        checkForEmpty.start();
    }

            //  TODO (10) Create a public static void method called startImmediateSync
            //  TODO (11) Within that method, start the SunshineSyncIntentService
            public static void startImmediateSync(final Context context) {
                Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
                context.startService(intentToSyncImmediately);
            }

        }
