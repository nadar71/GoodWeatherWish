
package eu.indiewalk.mystic.weatherapp.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import eu.indiewalk.mystic.weatherapp.utilities.InjectorUtils;

/**
 * -------------------------------------------------------------------------------------------------
 * An {@link IntentService} subclass for immediately scheduling a sync with the server in bg
 * off of the main thread.
 * Otherwise there is only {@link com.firebase.jobdispatcher.FirebaseJobDispatcher}
 * but it will not trigger a job immediately if needed.
 * This should only be called when the application is on the screen.
 * -------------------------------------------------------------------------------------------------
 */
public class WeatherSyncIntentService extends IntentService {
    private static final String LOG_TAG = WeatherSyncIntentService.class.getSimpleName();

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
        Log.d(LOG_TAG, "WeatherSyncIntentService : fetch remote data service started");
        // inject the data source property to recover data from owm through fetch
        WeatherNetworkDataSource networkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        Log.d(LOG_TAG, "WeatherSyncIntentService : property networkDataSource injected, with which calling : fetchWeather()");
        networkDataSource.fetchWeather();
    }
}