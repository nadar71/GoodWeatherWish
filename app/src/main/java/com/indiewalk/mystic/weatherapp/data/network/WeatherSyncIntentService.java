package com.indiewalk.mystic.weatherapp.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.indiewalk.mystic.weatherapp.utilities.InjectorUtils;


/**
 * An {@link IntentService} subclass for immediately scheduling a sync with the server off of the
 * main thread. This is necessary because {@link com.firebase.jobdispatcher.FirebaseJobDispatcher}
 * will not trigger a job immediately. This should only be called when the application is on the
 * screen.
 */
public class WeatherSyncIntentService extends IntentService {

    private static final String TAG = WeatherSyncIntentService.class.getSimpleName();

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    /*
    // in  onHandleIntent,call WeatherSyncTask.syncWeather
    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
    */

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Intent service started");
        // inject the
        com.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchWeather();
    }
}



