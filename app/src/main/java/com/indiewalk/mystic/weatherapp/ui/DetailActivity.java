package com.indiewalk.mystic.weatherapp.ui;

import android.content.Intent;
import android.database.Cursor;
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
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;

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

    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    // URI for accessing chosen day's weather details
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // TextViews for detailed weather informations
        mDateView = (TextView) findViewById(R.id.date);
        mDescriptionView = (TextView) findViewById(R.id.weather_description);
        mHighTemperatureView = (TextView) findViewById(R.id.high_temperature);
        mLowTemperatureView = (TextView) findViewById(R.id.low_temperature);
        mHumidityView = (TextView) findViewById(R.id.humidity);
        mWindView = (TextView) findViewById(R.id.wind);
        mPressureView = (TextView) findViewById(R.id.pressure);

        // Get refernce to content providers URI
        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        // Init loader to this activity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }

    /**
     * Creates and returns a CursorLoader that loads  data for our URI stored  in Cursor.
     * @param loaderId   
     * @param loaderArgs arguments 
     * @return          Loader instance to start loading.
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
     * Runs on the main thread when a load is complete. 
     * @param loader The cursor loader ended.
     * @param data   The cursor returned.
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

        /**
         * Weather Date 
         * Read the date from the cursor. 
         * Date that is stored is a GMT representation at midnight of the date 
         * when the weather information was loaded for.
         *
         * Before displaying it, getFriendlyDateString add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         */
        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = WeatherAppDateUtility.getFriendlyDateString(this, localDateMidnightGmt, true);

        mDateView.setText(dateText);

        // WEATHER FORECAST DESCRIPTION IN DETAILS
        // Read weather condition ID from the cursor (ID provided by Open Weather Map) 
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        // Use the weatherId to get the proper description and set in the view
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(this, weatherId);
        mDescriptionView.setText(description);

        // High (max) temperature 
        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        // Convert measure if prefferred
        String highString = WeatherAppGenericUtility.formatTemperature(this, highInCelsius);
        mHighTemperatureView.setText(highString);

        // Low (min) temperature 
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = WeatherAppGenericUtility.formatTemperature(this, lowInCelsius);
        mLowTemperatureView.setText(lowString);

        // Humidity 
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        mHumidityView.setText(humidityString);

        //  Wind speed and direction (in MPH) (in compass degrees)
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = WeatherAppGenericUtility.getFormattedWind(this, windSpeed, windDirection);

        mWindView.setText(windString);

        // Pressure
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);

        mPressureView.setText(pressureString);

        //  Store the forecast summary String in our forecast summary field to share later */
        choosenDayWeatherParam = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    // -----------------------------------------[ MENU STUFF ]--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }



    // -------[ MENU FUNCTIONS IMPLEMENTATION ]-----------------------------------------------------

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

    // Open settings
    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
