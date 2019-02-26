package com.indiewalk.mystic.weatherapp.old;

import android.content.ContentValues;
import android.content.Context;

import com.indiewalk.mystic.weatherapp.ui.settings.UserPreferencesData;
import com.indiewalk.mystic.weatherapp.data.provider.WeatherContract;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * ---------------------------------------------------------------------------------------------
 * Utility functions to handle OpenWeatherMap JSON data.
 * ---------------------------------------------------------------------------------------------
 */
public final class OpenWeatherJsonUtility {

    // Location information
    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";

    // Location coordinate
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    // Weather information. Each day's forecast info is an element of the "list" array
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    // All temperatures are children of the "temp" object
    private static final String OWM_TEMPERATURE = "temp";

    // Max temperature for the day
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";



    /**
     * ---------------------------------------------------------------------------------------------
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     * ---------------------------------------------------------------------------------------------
     */

    /*
    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Is there an error?
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

        UserPreferencesData.setLocationDetails(context, cityLatitude, cityLongitude);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];


        // OWM returns daily forecasts based upon the local time of the city that is being asked
        // for, which means that we need to know the GMT offset to translate this data properly.
        // Since this data is also sent in-order and the first day is always the current day, we're
        // going to take advantage of that to get a nice normalized UTC date for all of our weather.
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);

        long normalizedUtcStartDay = WeatherAppDateUtility.getNormalizedUtcDateForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {

            long dateTimeMillis;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            int weatherId;

            // Get the JSON object representing the day
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

            // We ignore all the datetime values embedded in the JSON and assume that
            // the values are returned in-order by day (which is not guaranteed to be correct).
            dateTimeMillis = normalizedUtcStartDay + WeatherAppDateUtility.DAY_IN_MILLIS * i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            // Description is in a child array called "weather", which is 1 element long.
            // That element also contains a weather code.
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            // Temperatures are sent by Open Weather Map in a child object called "temp".
            //
            // Editor's Note: Try not to name variables "temp" when working with temperature.
            // It confuses everybody. Temp could easily mean any number of things, including
            // temperature, temporary variable, temporary folder, temporary employee, or many
            // others, and is just a bad variable name.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            weatherContentValues[i] = weatherValues;
        }

        return weatherContentValues;
    }
   */


    /** OLD
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */

    /*
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        // Weather information. Each day's forecast info is an element of the "list" array
        final String OWM_LIST = "list";

        // All temperatures are children of the "temp" object
        final String OWM_TEMPERATURE = "temp";

        // Max temperature for the day
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        // String array to hold each day's weather String
        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Error handling
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new String[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = WeatherAppDateUtility.getUTCDateFromLocal(localDate);
        long startDay = WeatherAppDateUtility.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highAndLow;

            // These are the values that will be collected
            long dateTimeMillis;
            double high;
            double low;
            String description;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            //
            // We ignore all the datetime values embedded in the JSON and assume that
            // the values are returned in-order by day (which is not guaranteed to be correct).
            //
            dateTimeMillis = startDay + WeatherAppDateUtility.DAY_IN_MILLIS * i;
            date = WeatherAppDateUtility.getFriendlyDateString(context, dateTimeMillis, false);


            // Description is in a child array called "weather", which is 1 element long.
            // That element also contains a weather code.

            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);


            // Temperatures are sent by Open Weather Map in a child object called "temp".
            // Editor's Note: Try not to name variables "temp" when working with temperature.
            // It confuses everybody. Temp could easily mean any number of things, including
            // temperature, temporary and is just a bad variable name.
            //
            /*
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = WeatherAppGenericUtility.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;

        }

        return parsedWeatherData;
    }
    */

    /**
     * ---------------------------------------------------------------------------------------------
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     * @return An array of ContentValues parsed from the JSON.
     * ---------------------------------------------------------------------------------------------
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }




}