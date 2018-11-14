package com.indiewalk.mystic.weatherapp.synch;

import android.app.IntentService;
import android.content.Intent;

public class WeatherSyncIntentService extends IntentService {

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    // in  onHandleIntent,call WeatherSyncTask.syncWeather
    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}
