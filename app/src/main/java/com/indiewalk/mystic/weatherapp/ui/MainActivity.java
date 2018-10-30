/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indiewalk.mystic.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.SunshinePreferences;
import com.indiewalk.mystic.weatherapp.utilities.NetworkUtils;
import com.indiewalk.mystic.weatherapp.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements 
	ForecastAdapter.ForecastAdapterOnClickHandler ,
        LoaderCallbacks<String[]>{

    private static  final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    // id for the loader
    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        /* Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // Create a LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // Set the layoutManager in mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        // set mRecyclerView's Adapter
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        // The ProgressBar that will indicate to the user that we are loading data.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //Init Loader using loader manager
	int loaderId = FORECAST_LOADER_ID;
	Bundle bundleForLoader = null;
	LoaderCallbacks<String[]> callback = MainActivity.this;
	getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        
    }

    // Create the Loader, define it's callback function
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

                String locationQuery = SunshinePreferences
                        .getPreferredWeatherLocation(MainActivity.this);

                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
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

    // Called qhen loader finishes
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

    /**
     * To reset data list
     */
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
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Make the error message visible and hide the weather View.
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }






    // -----------------------------------------[ MENU STUFF ]--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    // Open location in map :
    // @see <a"http://developer.android.com/guide/components/intents-common.html#Maps">
    private void openLocationInMap(){
        //String addressString = "20 via Giotto,chignolo d'isola, IT";
        String addressString = "1600 Amphitheatre Parkway, CA";
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Refresh data
        if (id == R.id.action_refresh) {
            // delete the data in list
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        // Open map
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }



}
