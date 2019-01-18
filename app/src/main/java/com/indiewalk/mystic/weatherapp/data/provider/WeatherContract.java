package com.indiewalk.mystic.weatherapp.data.provider;


import android.net.Uri;
import android.provider.BaseColumns;

import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

// Weather data db cache
// Show the db contract in a separated class

public class WeatherContract {

    // Name for the entire content provider (domain name <-> its website).
    // Using app package name,unique on the  Store.
    public static final String CONTENT_AUTHORITY = "com.indiewalk.mystic.weatherapp";


    // Create the base of all URI's which apps will use to contact this content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Available paths appendeable to BASE_CONTENT_URI to form valid URI's
     *     content://com.indiewalk.mystic.weatherapp/weather/
     */
    public static final String PATH_WEATHER = "weather";

    //Table contents of the daily weather cache table
    public static final class WeatherEntry implements BaseColumns {

        // The base CONTENT_URI for querying Weather table from  content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        // weather table.
        public static final String TABLE_NAME = "weather";

        // The date column will store the UTC date that correlates to the local date for which
        // each particular weather row represents.
        public static final String COLUMN_DATE = "date";

        // ID as returned by BaseColumns, used to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Min and max temperatures in °C for the day (floats in the database)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity percentage (stored float)
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure percentage (stored float)
        public static final String COLUMN_PRESSURE = "pressure";

        // Wind speed in mph (stored float)
        public static final String COLUMN_WIND_SPEED = "wind";

        // Angle Degrees for meteorological degrees (e.g, 0 is north, 180 is south) (stored float)
        // NOT temperature degrees
        public static final String COLUMN_DEGREES = "degrees";


        /**
         * ---------------------------------------------------------------------------------------------
         * Return URI To Query details about a single weather entry by date, for DetailsView.
         * @param date   Normalized date in milliseconds
         * @return       Uri to query details about a single weather entry
         * ---------------------------------------------------------------------------------------------
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * ---------------------------------------------------------------------------------------------
         * Get a weather forecast from today's date
         * @return The selection part of the weather query for today onwards
         * ---------------------------------------------------------------------------------------------
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = WeatherAppDateUtility.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }

}
