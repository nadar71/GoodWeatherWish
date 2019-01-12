package com.indiewalk.mystic.weatherapp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.WeatherContract;
import com.indiewalk.mystic.weatherapp.databinding.ActivityDetailBinding;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;


/**
 * -------------------------------------------------------------------------------------------------
 * Activity for showing each forecast detail, with main part above with main info, and below
 * the detailed ones.
 * -------------------------------------------------------------------------------------------------
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static  final  String TAG = DetailActivity.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #GoodWeatherApp";

    // columns of data displayed in particular day detailed weather forecast
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
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


    // Loader id, to load weather details
    private static final int ID_DETAIL_LOADER = 353;

    private String   choosenDayWeatherParam;

    // URI for accessing chosen day's weather details
    private Uri mUri;


    // Data binding to activity_detail layout declaration
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate data binding object
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Get reference to content providers URI
        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        // Init loader to this activity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Creates and returns a CursorLoader that loads  data for our URI stored  in Cursor.
     * @param loaderId   
     * @param loaderArgs arguments 
     * @return           Loader instance to start loading.
     * ---------------------------------------------------------------------------------------------
     */
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


    /**
     * ---------------------------------------------------------------------------------------------
     * Runs on the main thread when a load is complete. 
     * @param loader The cursor loader ended.
     * @param data   The cursor returned.
     * ---------------------------------------------------------------------------------------------
     */
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
            /* No data to display, simply return and do nothing */
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

        //  Store the forecast summary String in our forecast summary field to share later */
        choosenDayWeatherParam = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
