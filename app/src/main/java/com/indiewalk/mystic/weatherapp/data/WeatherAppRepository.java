

package com.indiewalk.mystic.weatherapp.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.indiewalk.mystic.weatherapp.AppExecutors;
import com.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;
import com.indiewalk.mystic.weatherapp.data.database.WeatherDao;
import com.indiewalk.mystic.weatherapp.data.database.WeatherEntry;
import com.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

import java.util.Date;
import java.util.List;

/**
 * -------------------------------------------------------------------------------------------------
 * Handles data operations in WeatherApp.
 * Acts as a mediator between {@link WeatherNetworkDataSource}
 * and {@link WeatherDao}
 * -------------------------------------------------------------------------------------------------
 */
public class WeatherAppRepository {
    private static final String LOG_TAG = WeatherAppRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static WeatherAppRepository sInstance;
    private final WeatherDao mWeatherDao;
    private final WeatherNetworkDataSource mWeatherNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private WeatherAppRepository(WeatherDao weatherDao,
                                 WeatherNetworkDataSource weatherNetworkDataSource,
                                 AppExecutors executors) {
        mWeatherDao = weatherDao;
        mWeatherNetworkDataSource = weatherNetworkDataSource;
        mExecutors = executors;

        // get the data from data source, WeatherNetworkDataSource
        LiveData<WeatherEntry[]> networkData = mWeatherNetworkDataSource.getCurrentWeatherForecast();

        // observe the data source in case of change
        networkData.observeForever(newForecastsFromNetwork ->{
            mExecutors.diskIO().execute( () ->{

                        // Delete old data
                        deleteOldData();

                        // Insert new weather data into db
                        mWeatherDao.bulkInsert(newForecastsFromNetwork);
                        Log.d(LOG_TAG, "New values inserted: ");
                    }

            );
        });


    }


    // get repository's singleton instance
    public synchronized static WeatherAppRepository getInstance(
            WeatherDao weatherDao,
            WeatherNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {

        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WeatherAppRepository(weatherDao,
                        weatherNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     * ---------------------------------------------------------------------------------------------
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime.
        if (mInitialized) return;

        mInitialized = true;

        // Synchronize weather data periodically (every SYNC_INTERVAL_HOURS)
        mWeatherNetworkDataSource.scheduleRecurringFetchWeatherSync();


        // fetch weather data from restful service as bg service
        mExecutors.diskIO().execute(()->{
            if(isFetchNeeded()){
                startFetchWeatherService();
            }
        });
    }



    /**
     * ---------------------------------------------------------------------------------------------
     *  Retrieve forecast on a single date
     * ---------------------------------------------------------------------------------------------
     **/
    public LiveData<WeatherEntry> getWeatherByDate(Date date){
        initializeData();
        return mWeatherDao.getWeatherByDate(date);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *  Retrieve a list of forecast starting from current date
     * ---------------------------------------------------------------------------------------------
     **/
    public LiveData<List<ListWeatherEntry>> getCurrentWeatherForecasts(){
        initializeData();
        Date today = WeatherAppDateUtility.getNormalizedUtcDateForToday();
        return mWeatherDao.getCurrentWeatherForecasts(today);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Deletes old weather data because we don't need to keep multiple days' data
     * ---------------------------------------------------------------------------------------------
     */
    private void deleteOldData() {
        Date today = WeatherAppDateUtility.getNormalizedUtcDateForToday();
        mWeatherDao.deleteOldData(today);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Checks if there are enough days of future weather forecasts are requested by app
     * in WeatherNetworkDataSource.NUM_DAYS
     * ---------------------------------------------------------------------------------------------
     *
     * @return Whether a fetch is needed
     */
    // TODO : must make a check in date and not on rec numbers
    private boolean isFetchNeeded() {
        Date today = WeatherAppDateUtility.getNormalizedUtcDateForToday();
        int count  = mWeatherDao.countAllFutureWeather(today);
        return (count < WeatherNetworkDataSource.NUM_DAYS);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Network related operation : start IntentService present in  WeatherNetworkDataSource
     * ---------------------------------------------------------------------------------------------
     */
    private void startFetchWeatherService() {
        // call the intent service for retrieving network data daemon
        mWeatherNetworkDataSource.startFetchWeatherService();
    }

}