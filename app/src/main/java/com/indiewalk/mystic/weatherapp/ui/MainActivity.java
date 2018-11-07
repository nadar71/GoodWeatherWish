package com.indiewalk.mystic.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.UserPreferencesData;
import com.indiewalk.mystic.weatherapp.utilities.NetworkUtils;
import com.indiewalk.mystic.weatherapp.utilities.OpenWeatherJsonUtility;

import java.net.URL;

/**
 * MainActivity
 * Implements :
 * - ForecastAdapter.ForecastAdapterOnClickHandler : for clicking on an list item
 * - LoaderCallbacks<String[]> : for precessing list of string loadeded through loader
 * - SharedPreferences.OnSharedPreferenceChangeListener : for getting aware of changes in preferences and
 *   loading/changing data displayed
 */
public class MainActivity extends AppCompatActivity implements 
	ForecastAdapter.ForecastAdapterOnClickHandler ,
        LoaderCallbacks<String[]>,
        SharedPreferences.OnSharedPreferenceChangeListener
  {

    private static  final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView    mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView        mErrorMessageDisplay;

    private ProgressBar     mLoadingIndicator;

    // id for the loader
    private static final int FORECAST_LOADER_ID = 0;

    // flag for
    private static boolean   PREFERENCES_HAVE_BEEN_UPDATED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        // Get the recycle view layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // Create a LayoutManager for the recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // Set the layoutManager in mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        // set mRecyclerView's Adapter
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        // The ProgressBar that will indicate to the user that we are loading data.
        // Invisible when data woud have been loaded
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Init Loader using loader manager
        int loaderId = FORECAST_LOADER_ID;
        Bundle bundleForLoader = null;
        LoaderCallbacks<String[]> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        Log.d(TAG, "onCreate: registering preference changed listener");
        //Register MainActivity as an OnPreferenceChangedListener for callback when a SharedPreference has changed.
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    // Create the Loader, override it's callback functions
    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle loaderArgs) {
        return new AsyncTaskLoader<String[]>(this) { // TODO : TEMPORARY , must be set in a class apart

            // This String array will hold and cache weather data
            String[] mWeatherData = null;
         
            

   	    // If data are loaded, deliver them, otherwise show loading indicator and forceload
	    @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {

                String locationQuery = UserPreferencesData
                        .getPreferredWeatherLocation(MainActivity.this);

                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    String[] simpleJsonWeatherData = OpenWeatherJsonUtility
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                    return simpleJsonWeatherData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.             
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    // Called when loader finishes
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
    }

    // To reset data list
    private void invalidateData() {
        mForecastAdapter.setWeatherData(null);
    }

    // Touch item action
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Intent showDetailActivity = new Intent(context,DetailActivity.class );
        showDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(showDetailActivity);
    }


     // Make the View for the weather data visible and hide the error message.
    private void showWeatherDataView() {
        // Make error invisible and show data
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Make the error message visible and hide the weather View.
    private void showErrorMessage() {
        // Hide the currently visible data and  show the error
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Override OnStart for :
     * - checking changes in SharedPreferences
     */
    @Override
    protected void onStart() {
        super.onStart();

        /** TODO : TEMPORARY SOLUTION
         *  in case metrics has changed in preferences, reload all data from a new query to upate the main list.
         *  NOT OPTIMAL SOLUTION, only the simplest one for getting job done at the moment
         */
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    /**
     * Override onDestroy for :
     * - unregistered checking changes in SharedPreferences
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister MainActivity as an OnPreferenceChangedListener to avoid memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * What to do when preferences changes
     * @param sharedPreferences
     * @param s
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // This flag turn to true when control returns to MainActivity in cse of SharedPreferences changes.
        //** see TODO in onStart
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }



    // -----------------------------------------[ MENU STUFF ]--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        // Display on toolbar
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Refresh data
        if (id == R.id.action_refresh) {
            // delete the data in list
            invalidateData();
            // load new data
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        // Open location in map
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        // Settings
        if (id == R.id.action_settings){           
            openSettings();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    // -------[ MENU FUNCTIONS IMPLEMENTATION ]-----------------------------------------------------

    // Open location in map :
    // @see <a"http://developer.android.com/guide/components/intents-common.html#Maps">
    private void openLocationInMap(){
        // Debug locations
        // String addressString = "20 via Giotto,chignolo d'isola, IT";
        // String addressString = "1600 Amphitheatre Parkway, CA";

        // get location from preferences
        String addressString = UserPreferencesData.getPreferredWeatherLocation(this);
        // build uri
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        // invoke a map app to show location on map
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }

    }

    // Open settings
    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



}
