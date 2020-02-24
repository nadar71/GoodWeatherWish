

package eu.indiewalk.mystic.weatherapp.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import eu.indiewalk.mystic.weatherapp.AppExecutors;
import eu.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherDao;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;
import eu.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource;
import eu.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

import java.util.Date;
import java.util.List;

/**
 * -------------------------------------------------------------------------------------------------
 * Handles data operations in WeatherApp.
 * Acts as a mediator between {@link WeatherNetworkDataSource} ,
 * which retrieve remote data and scheduled it
 * and {@link WeatherDao} : insert data in db taken by {@link WeatherNetworkDataSource} and
 * retrieve data from db
 * -------------------------------------------------------------------------------------------------
 */
public class WeatherAppRepository {
    private static final String TAG = WeatherAppRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object            LOCK = new Object();
    private static WeatherAppRepository    sInstance;
    private final WeatherDao               mWeatherDao;
    private final WeatherNetworkDataSource networkDataSource;
    private final AppExecutors             mExecutors;
    private boolean                        mInitialized = false;

    // Main constructor
    private WeatherAppRepository(WeatherDao weatherDao,
                                 WeatherNetworkDataSource weatherNetworkDataSource,
                                 AppExecutors executors) {
        mWeatherDao       = weatherDao;
        networkDataSource = weatherNetworkDataSource;
        mExecutors        = executors;

        // check data from data source, WeatherNetworkDataSource : if they are new, due to
        // fresh job scheduling retrieving, with the observer below update db
        LiveData<WeatherEntry[]> networkData = networkDataSource.getCurrentWeatherForecast();


        // TODO : only for DEBUG, delete this!!!
        /*
        mExecutors.diskIO().execute( () ->{
                    Log.d(TAG, "WeatherAppRepository observer : New values found, deletes previous and insert new ones: ");

                    // Delete old data
                    deleteOldData();

                }

        );
        */

        // Keep data always updated with remote ones.
        // Observe the data source retrieved in networkData above, because in case of change
        // WeatherNetworkDataSource have done a postValue in
        // MutableLiveData<WeatherEntry[]> networkData = networkDataSource.getCurrentWeatherForecast()
        // newForecastsFromNetwork = networkData so there are new data and observer will be alerted
        networkData.observeForever(newForecastsFromNetwork ->{
            mExecutors.diskIO().execute( () ->{
                        Log.d(TAG, "WeatherAppRepository observer : New values found, deletes previous and insert new ones: ");

                        // Delete old and insert new data
                        deleteOldData();
                        mWeatherDao.bulkInsert(newForecastsFromNetwork);
                        Log.d(TAG, "WeatherAppRepository observer : New values inserted: ");
                    }

            );
        });


    }


    // get repository's singleton instance
    public synchronized static WeatherAppRepository getInstance(
            WeatherDao weatherDao,
            WeatherNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {

        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WeatherAppRepository(weatherDao,
                        weatherNetworkDataSource,
                        executors);
                Log.d(TAG, "Made new repository");
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

        // 1 - To avoid requesting remote data, perform initialization once per app lifetime.
        if (mInitialized) {
            Log.d(TAG, "initializeData: data already initialized, return");
            return;
        }

        // 2- if not initialized, ask for remote data
        mInitialized = true;

        // Set Synchronizing weather data periodically (every SYNC_INTERVAL_HOURS)
        networkDataSource.scheduleRecurringFetchWeatherSync();


        // fetch weather data from restful service as bg service if db are empty or old
        mExecutors.diskIO().execute(()->{
            if(isFetchNeeded()){
                Log.d(TAG, "initializeData: isFetchNeeded == true, run the intent from fetching data from remote");
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
     * Checks if there are enough days of future weather forecasts as needed by app
     * in WeatherNetworkDataSource.NUM_DAYS
     *
     * @return Whether a fetch is needed
     * ---------------------------------------------------------------------------------------------
     */
    // TODO : must make a check in date and not on rec numbers
    private boolean isFetchNeeded() {
        Date today = WeatherAppDateUtility.getNormalizedUtcDateForToday();
        int count  = mWeatherDao.countAllFutureWeather(today);
        Log.d(TAG, "isFetchNeeded: date : "+ today.toString() + " mWeatherDao.countAllFutureWeather(today) : " + count );
        return (count < WeatherNetworkDataSource.NUM_DAYS);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Network related operation : start IntentService present in  WeatherNetworkDataSource
     * ---------------------------------------------------------------------------------------------
     */
    private void startFetchWeatherService() {
        // call the intent service for retrieving network data daemon
        networkDataSource.startFetchWeatherService();
    }

}