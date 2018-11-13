package com.indiewalk.mystic.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
 
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.UserPreferencesData;
import com.indiewalk.mystic.weatherapp.data.WeatherContract;
import com.indiewalk.mystic.weatherapp.utilities.FakeDataUtils;

/**
 * MainActivity
 * Implements :
 * - ForecastAdapter.ForecastAdapterOnClickHandler : for clicking on an list item
 * - LoaderCallbacks<Cursor[]> : for processing list of data loaded through cursor loader
 * loading/changing data displayed
 */
public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Data columns from contentprovider displayed in  weather data.
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    //  Index of name columns position in MAIN_FORECAST_PROJECTION : to keep synch with position in array
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private RecyclerView    mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private ProgressBar mLoadingIndicator;

    // id for the loader
    private static final int FORECAST_LOADER_ID = 10;

    // position in RecyclerView, init with no position
    private int mPosition = RecyclerView.NO_POSITION;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);


        FakeDataUtils.insertFakeData(this);

        // Get the recycle view layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        // Create a LayoutManager for the recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Set the layoutManager in mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        // set mRecyclerView's Adapter
        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);

        // The ProgressBar that will indicate to the user that we are loading data.
        // Invisible when data woud have been loaded
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Show load in progress
        showLoading();


        //Init Loader using loader manager
        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

    }


    // Create the Loader, override it's callback functions, return a Loader<Cursor>
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle loaderArgs) {
        switch (loaderId) {
            // return CursorLoader in case of FORECAST_LOADER_ID request
            case FORECAST_LOADER_ID:
                // URI for all rows of weather data in weather table
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                // Get all weather data from today onwards that is stored in our weather table.
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    // Called when loader finishes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // swap cursor with new data
        mForecastAdapter.swapCursor(data);
        // go to position
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        // if data not empty, show them
        if (data.getCount() != 0) showWeatherDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear the Adapter to show no data
        mForecastAdapter.swapCursor(null);
    }


    // Touch item action
    @Override
    public void onClick(long weatherForDay) {
        Context context = this;
        Intent showDetailActivity = new Intent(context, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(weatherForDay);
        showDetailActivity.setData(uriForDateClicked);
        startActivity(showDetailActivity);
    }


    // Make the View for the weather data visible and hide the error message.
    private void showWeatherDataView() {
        // Progress bar invisible and show data
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    // Show progress bar loader of data; hide weather data view
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
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
            // delete and reload the data in RecyclerView
            mForecastAdapter.swapCursor(null);
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
        if (id == R.id.action_settings) {
            openSettings();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    // -------[ MENU FUNCTIONS IMPLEMENTATION ]-----------------------------------------------------

    // Open location in map :
    // @see <a"http://developer.android.com/guide/components/intents-common.html#Maps">
    private void openLocationInMap() {
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

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }

    }

    // Open settings
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
