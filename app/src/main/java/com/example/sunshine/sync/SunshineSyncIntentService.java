package com.example.sunshine.sync;

// TODO (5) Create a new class called SunshineSyncIntentService that extends IntentService

import android.app.IntentService;
import android.content.Intent;

public class SunshineSyncIntentService extends IntentService {

    //  TODO (6) Create a constructor that calls super and passes the name of this class
//  TODO (7) Override onHandleIntent, and within it, call SunshineSyncTask.syncWeather
    public SunshineSyncIntentService(){
        super("SunshineSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SunshineSyncTask.syncWeather(this);

    }
}
