
package eu.indiewalk.mystic.weatherapp.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import eu.indiewalk.mystic.weatherapp.AppExecutors;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * -------------------------------------------------------------------------------------------------
 * Provides an API for doing all operations with the server data
 * -------------------------------------------------------------------------------------------------
 */
public class WeatherNetworkDataSource {
    // The number of days we want our API to return, set to 14 days or two weeks
    public static final int NUM_DAYS = 5;
    private static final String TAG = WeatherNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String WEATHERAPP_SYNC_TAG = "weatherapp-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static WeatherNetworkDataSource sInstance;
    private final Context mContext;

    private final AppExecutors mExecutors;

    // LiveData storing the latest downloaded weather forecast
    private final MutableLiveData<WeatherEntry[]> mDownloadedWeatherForecasts;


    private WeatherNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;

        mDownloadedWeatherForecasts = new MutableLiveData<WeatherEntry[]>();
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Get the singleton for this class
     * ---------------------------------------------------------------------------------------------
     */
    public static WeatherNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WeatherNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(TAG, "Made new network data source");
            }
        }
        return sInstance;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for mDownloadedWeatherForecasts
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public LiveData<WeatherEntry[]> getCurrentWeatherForecast(){
        return mDownloadedWeatherForecasts;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Immediate sync using an IntentService for asynchronous execution.
     * Used by repository, of course.
     * ---------------------------------------------------------------------------------------------
     */
    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(mContext, WeatherSyncIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(TAG, "startFetchWeatherService : Fetch weather data Service WeatherSyncIntentService created.");
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Schedules a repeating job service which fetches the weather.
     * ---------------------------------------------------------------------------------------------
     */
    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync WeatheraApp
        Job syncWeatherAppJob = dispatcher.newJobBuilder()

                // service used to sync data
                .setService(WeatherFirebaseJobService.class)

                // set the UNIQUE tag used to identify this Job
                .setTag(WEATHERAPP_SYNC_TAG)

                // Network constraints on which this Job should run.
                // Run on any network
                // TODO : include a preference for this: wifi, while charging etc.
                .setConstraints(Constraint.ON_ANY_NETWORK)

                // setLifetime. Options : forever" or  die the next time the device boots up.
                .setLifetime(Lifetime.FOREVER)

                // Tell the Job that must recur.
                .setRecurring(true)

                /*
                 * Weather data must be synced every 3 to 4 hours. The first argument for
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

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncWeatherAppJob);
        Log.d(TAG, "Job scheduled");
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Gets the newest weather
     * ---------------------------------------------------------------------------------------------
     */
    public void fetchWeather() {
        Log.d(TAG, "Fetch weather method started");
        mExecutors.networkIO().execute(() -> {
            try {

                // The getUrl method will return the URL that we need to get the forecast JSON for the
                // weather. It will decide whether to create a URL based off of the latitude and
                // longitude or off of a simple location as a String.

                URL weatherRequestUrl = NetworkUtils.getUrl(mContext);
                Log.d(TAG, "weatherRequestUrl : " + weatherRequestUrl);

                // Use the URL to retrieve the JSON
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                Log.d(TAG, "JSON answer received , response : " + jsonWeatherResponse);

                // Parse the JSON into a list of weather forecasts
                WeatherResponse response = new OpenWeatherJsonParser().parse(jsonWeatherResponse);
                Log.d(TAG, "JSON Parsing finished, response : " + response.toString());


                // As long as there are weather forecasts, update the LiveData storing the most recent
                // weather forecasts. This will trigger observers of that LiveData, such as the
                // WeatherAppRepository.
                if (response != null && response.getWeatherForecast().length != 0) {
                    Log.d(TAG, "JSON not null and has " + response.getWeatherForecast().length
                            + " values : livedata mDownloadedWeatherForecasts will post value WeatherEntry array to main thread, using repository");
                    Log.d(TAG, String.format("First value is %1.0f and %1.0f",
                            response.getWeatherForecast()[0].getMin(),
                            response.getWeatherForecast()[0].getMax()));


                    // Posted to repository observer, networkData.observeForever
                    // which is observe MutableLiveData<WeatherEntry[]> mDownloadedWeatherForecasts
                    // getting it with LiveData<WeatherEntry[]> networkData = mWeatherNetworkDataSource.getCurrentWeatherForecast();
                    // when modified, it deletes previous data and bulk inserts the new ones

                    // the observer in MainActivity, using the viewmodel which observe the weather entry from db,
                    // will be aware of a changes, and updates the recyclerView :
                    // MainActivity : mViewModel.getWeatherList().observe(this,weatherEntries
                    //   getWeatherList() is from MainActivityViewModel : getWeatherList() {return mListWeather;}
                    // that keep mListWeather updated with : mListWeather = mRepository.getCurrentWeatherForecasts();

                    mDownloadedWeatherForecasts.postValue(response.getWeatherForecast());

                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });

    }

}