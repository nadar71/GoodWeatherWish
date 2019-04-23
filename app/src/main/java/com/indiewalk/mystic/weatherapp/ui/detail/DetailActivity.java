
package com.indiewalk.mystic.weatherapp.ui.detail;

// import mystic.arch.lifecycle.LifecycleActivity;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.indiewalk.mystic.weatherapp.databinding.ActivityDetailBinding;
import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.database.WeatherEntry;
import com.indiewalk.mystic.weatherapp.utilities.InjectorUtils;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;


import java.util.Date;



/**
 * -------------------------------------------------------------------------------------------------
 * Activity for showing each day forecast from main list in detail,
 * with main part  with main info above, and below the detailed ones.
 * -------------------------------------------------------------------------------------------------
 */


// public class DetailActivity extends LifecycleActivity
public class DetailActivity extends AppCompatActivity {

    private static  final  String TAG = DetailActivity.class.getSimpleName();

    private static final   String FORECAST_SHARE_HASHTAG = " #GoodWeatherApp";

    public static final String WEATHER_ID_EXTRA = "WEATHER_ID_EXTRA";

    /*
    // columns of data displayed in particular day detailed weather forecast
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_DATE,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_DEGREES,
            com.indiewalk.mystic.weatherapp.data.provider.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };


    // ...and related indexes
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;
    */


    // Loader id, to load weather details
    private static final int ID_DETAIL_LOADER = 353;


    private String   choosenDayWeatherParam;

    // ViewModel reference
    DetailActivityViewModel mViewModel;

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // instantiate data binding object
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        long timestamp = getIntent().getLongExtra(WEATHER_ID_EXTRA, -1);

        // Make date for today
        // Date date = WeatherAppDateUtility.getNormalizedUtcDateForToday();

        // Make date for date clicked in main activity converting from timestamp
        Date date = new Date(timestamp);

        // init ViewModel providers
        // mViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(),date);
        mViewModel = ViewModelProviders.of(this,factory).get(DetailActivityViewModel.class);

        // observe live data WeatherEntry, provided from repository above, if have been changed
        mViewModel.getWeather().observe(this, weatherEntry -> {
            // Update the UI
            if(weatherEntry != null) bindWeatherToUI(weatherEntry);
        });



        // DEBUG executor to test LiveData working
        /*
        AppExecutors.getInstance().diskIO().execute(()-> {
            try {

                // Pretend this is the network loading data
                Thread.sleep(4000);
                Date today = WeatherAppDateUtility.getNormalizedUtcDateForToday();
                WeatherEntry pretendWeatherFromDatabase = new WeatherEntry(1, 210, today,88.0,99.0,71,1030, 74, 5);
                mViewModel.setWeather(pretendWeatherFromDatabase);

                Thread.sleep(2000);
                pretendWeatherFromDatabase = new WeatherEntry(1, 952, today,50.0,60.0,46,1044, 70, 100);
                mViewModel.setWeather(pretendWeatherFromDatabase);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        */

        // DEBUG : THIS IS JUST TO RUN THE CODE; REPOSITORY SHOULD NEVER BE CREATED IN
        // DETAILACTIVITY
        // InjectorUtils.provideRepository(this).initializeData();
        // ************ OLD STUFF USING LOADER ******************************
        // Init loader to this activity
        // getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }



    // ---------------------------------------------------------------------------------------------
    // Update UI when weatherEntry data in ViewModel are ready.
    // @param loader The cursor loader ended.
    // @param data   The cursor returned.
    // ---------------------------------------------------------------------------------------------
    private void bindWeatherToUI(WeatherEntry weatherEntry) {

        // -----------------------
        //    Weather Icon
        // -----------------------
        // Read weather condition ID from the cursor (ID provided by Open Weather Map)
        int weatherId = weatherEntry.getWeatherIconId();
        int weatherImageId = WeatherAppGenericUtility.getLargeArtResourceIdForWeatherCondition(weatherId);

        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);


        // -----------------------
        // Weather Date
        // -----------------------
        // Read the date from the cursor.
        // Date that is stored is a GMT representation at midnight of the date
        // when the weather information was loaded for.
        //
        // Before displaying it, getFriendlyDateString add the GMT offset (in milliseconds) to acquire
        // the date representation for the local date in local time.
        long localDateMidnightGmt = weatherEntry.getDate().getTime();
        String dateText = WeatherAppDateUtility.getFriendlyDateString(DetailActivity.this, localDateMidnightGmt, true);
        mDetailBinding.primaryInfo.date.setText(dateText);


        // -----------------------
        // Weather Description
        // -----------------------
        // Use the weatherId to get the proper description and set in the view
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(DetailActivity.this, weatherId);

        // accessibility for weather description
        String descriptionAccessibility = getString(R.string.acc_forecast, description);

        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionAccessibility);

        // Content description of the icon the same as the weather description
        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionAccessibility);



        // -----------------------
        // High (max) temperature
        // -----------------------
        double maxInCelsius = weatherEntry.getMax();

        // Convert measure if preferred by users
        String maxString = WeatherAppGenericUtility.formatTemperature(DetailActivity.this, maxInCelsius);
        String maxAccessibility = getString(R.string.acc_max_temp, maxString);

        mDetailBinding.primaryInfo.highTemperature.setText(maxString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(maxAccessibility);




        // -----------------------
        // Low (min) temperature
        // -----------------------
        double minInCelsius = weatherEntry.getMin();

        String minString = WeatherAppGenericUtility.formatTemperature(DetailActivity.this, minInCelsius);
        String minAccessibility = getString(R.string.acc_min_temp, minString);

        mDetailBinding.primaryInfo.lowTemperature.setText(minString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(minAccessibility);



        // -----------------------
        // Humidity
        // -----------------------
        double humidity = weatherEntry.getHumidity();
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityAccessibility = getString(R.string.acc_humidity, humidityString);

        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityAccessibility);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityAccessibility);



        // -------------------------------------------------------
        //  Wind speed and direction (in MPH) (in compass degrees)
        // -------------------------------------------------------
        double windSpeed = weatherEntry.getWind();
        double windDirection = weatherEntry.getDegrees();
        String windString = WeatherAppGenericUtility.getFormattedWind(DetailActivity.this, windSpeed, windDirection);
        String windAccessibility = getString(R.string.acc_wind, windString);

        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windAccessibility);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windAccessibility);




        // -----------------------
        // Pressure
        // -----------------------
        double pressure = weatherEntry.getPressure();

        String pressureString = getString(R.string.format_pressure, pressure);

        String pressureAccessibility = getString(R.string.acc_pressure, pressureString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureAccessibility);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureAccessibility);

        //  Store the forecast summary String in our forecast summary field to share later
        choosenDayWeatherParam = String.format("%s - %s - %s/%s",
                dateText, description, maxString, minString);
    }



    // ---------------------------------------------------------------------------------------------
    //                                           MENU STUFF
    // ---------------------------------------------------------------------------------------------

    /**
     * ---------------------------------------------------------------------------------------------
     * @param menu
     * @return
     * ---------------------------------------------------------------------------------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Menu item behaviours
     * @param item
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Sharing detail day forecast, using a app chooser
        if (id == R.id.action_share) {
            forecastSharing(choosenDayWeatherParam);
            return true;
        }

        // Settings
        if (id == R.id.action_settings){
            openSettings();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Sharing forecast details through chooser
     * @param choosenDayWeatherParam
     * ---------------------------------------------------------------------------------------------
     */
    private void forecastSharing(String choosenDayWeatherParam){
        Log.d(TAG, "forecastSharing Press ");
        String mimeType = "text/plain";
        String title    = "Today weather forecast details.";
        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(choosenDayWeatherParam + FORECAST_SHARE_HASHTAG)
                .startChooser();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Open settings
     * ---------------------------------------------------------------------------------------------
     */
    private void openSettings(){
        // Intent intent = new Intent(this, SettingsActivity.class);
        // startActivity(intent);
    }





    // ---------------------------------------------------------------------------------------------
    // ********************************** Using loader : OLD CODE **********************************
    // ---------------------------------------------------------------------------------------------


/*
    //---------------------------------------------------------------------------------------------
    //Creates and returns a CursorLoader that loads  data for our URI stored  in Cursor.
    //@param loaderId
    //@param loaderArgs arguments
    //@return           Loader instance to start loading.
    //---------------------------------------------------------------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }



    // ---------------------------------------------------------------------------------------------
    // Runs on the main thread when a load is complete.
    // @param loader The cursor loader ended.
    // @param data   The cursor returned.
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // If data present, move cursor to first row, and binds the ui
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            // valid data, continue binding it to  UI
            cursorHasValidData = true;
        }

        // No data
        if (!cursorHasValidData) {
            // No data to display, simply return and do nothing
            return;
        }



        // -----------------------
        //    Weather Icon
        // -----------------------
        //Read weather condition ID from the cursor (ID provided by Open Weather Map)
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        int weatherImageId = WeatherAppGenericUtility.getLargeArtResourceIdForWeatherCondition(weatherId);

        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);



        // -----------------------
        // Weather Date
        // -----------------------
        // Read the date from the cursor.
        // Date that is stored is a GMT representation at midnight of the date
        // when the weather information was loaded for.
        //
        // Before displaying it, getFriendlyDateString add the GMT offset (in milliseconds) to acquire
        // the date representation for the local date in local time.
        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = WeatherAppDateUtility.getFriendlyDateString(this, localDateMidnightGmt, true);

        mDetailBinding.primaryInfo.date.setText(dateText);



        // -----------------------
        // Weather Description
        // -----------------------
        // Use the weatherId to get the proper description and set in the view
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(this, weatherId);
        // accessibility for weather description
        String descriptionAccessibility = getString(R.string.acc_forecast, description);

        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionAccessibility);

        // Content description of the icon the same as the weather description
        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionAccessibility);



        // -----------------------
        // High (max) temperature
        // -----------------------
        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        // Convert measure if preferred by users
        String highString = WeatherAppGenericUtility.formatTemperature(this, highInCelsius);
        String highAccessibility = getString(R.string.acc_high_temp, highString);

        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highAccessibility);



        // -----------------------
        // Low (min) temperature
        // -----------------------
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = WeatherAppGenericUtility.formatTemperature(this, lowInCelsius);
        String lowAccessibility = getString(R.string.acc_low_temp, lowString);

        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowAccessibility);

        // -----------------------
        // Humidity
        // -----------------------
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityAccessibility = getString(R.string.acc_humidity, humidityString);

        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityAccessibility);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityAccessibility);


        // -------------------------------------------------------
        //  Wind speed and direction (in MPH) (in compass degrees)
        // -------------------------------------------------------
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = WeatherAppGenericUtility.getFormattedWind(this, windSpeed, windDirection);
        String windAccessibility = getString(R.string.acc_wind, windString);

        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windAccessibility);

        mDetailBinding.extraDetails.windLabel.setContentDescription(windAccessibility);

        // -----------------------
        // Pressure
        // -----------------------
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        String pressureAccessibility = getString(R.string.acc_pressure, pressureString);


        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureAccessibility);

        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureAccessibility);

        //  Store the forecast summary String in our forecast summary field to share later
        choosenDayWeatherParam = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    */


}