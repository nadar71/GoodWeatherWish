package com.indiewalk.mystic.weatherapp.data;


import android.provider.BaseColumns;

// Weather data db cache
// Show the db contract in a separated class

public class WeatherContract {

    //Table contents of the daily weather cache table
    public static final class WeatherEntry implements BaseColumns {

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
    }

}
