
package eu.indiewalk.mystic.weatherapp.data.network;

import android.support.annotation.Nullable;

import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;
import eu.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * Parser for OpenWeatherMap JSON data.
 * -------------------------------------------------------------------------------------------------
 */
final class OpenWeatherJsonParser {

    private static final String TAG = OpenWeatherJsonParser.class.getSimpleName();

    // Weather information. Each day's forecast info is an element of the "list" array
    private static final String OWM_LIST           = "list";



    // root : All temperatures are children of the "main" object
    private static final String OWM_TEMPERATURE    = "main";

    // main -> Max temperature for the day
    private static final String OWM_MAX        = "temp_max";
    private static final String OWM_MIN        = "temp_min";
    private static final String OWM_PRESSURE   = "pressure";
    private static final String OWM_HUMIDITY   = "humidity";


    // root : weather
    private static final String OWM_WEATHER    = "weather";
    // weather --> id
    private static final String OWM_WEATHER_ID = "id";

    // root : wind
    private static final String OWM_WIND           = "wind";
    // wind -> wind attributes
    private static final String OWM_WINDSPEED      = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    private static final String OWM_MESSAGE_CODE   = "cod";

    private static boolean hasHttpError(JSONObject forecastJson) throws JSONException {
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    return false;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                default:
                    // Server probably down
                    return true;
            }
        }
        return false;
    }




    private static WeatherEntry[] fromJson(final JSONObject forecastJson) throws JSONException {
        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

        WeatherEntry[] weatherEntries = new WeatherEntry[jsonWeatherArray.length()];

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
        long normalizedUtcStartDay = WeatherAppDateUtility.getNormalizedUtcMsForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

            // Create the weather entry object
            long dateTimeMillis = normalizedUtcStartDay + WeatherAppDateUtility.DAY_IN_MILLIS * i;
            WeatherEntry weather = fromJson(dayForecast, dateTimeMillis);

            weatherEntries[i] = weather;
        }
        return weatherEntries;
    }








    private static WeatherEntry fromJson(final JSONObject dayForecast,
                                         long dateTimeMillis) throws JSONException {
        // We ignore all the datetime values embedded in the JSON and assume that
        // the values are returned in-order by day (which is not guaranteed to be correct).


        // Description is in a child array called "weather", which is 1 element long.
        // That element also contains a weather code.
        JSONObject weatherObject =
                dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

        int weatherId = weatherObject.getInt(OWM_WEATHER_ID);


        //  Temperatures are sent by Open Weather Map in a child object called "main".
        JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
        double max      = temperatureObject.getDouble(OWM_MAX);
        double min      = temperatureObject.getDouble(OWM_MIN);
        double pressure = temperatureObject.getDouble(OWM_PRESSURE);
        int humidity    = temperatureObject.getInt(OWM_HUMIDITY);

        // Wind features
        JSONObject windObject = dayForecast.getJSONObject(OWM_WIND);
        double windSpeed      = windObject.getDouble(OWM_WINDSPEED);
        double windDirection  = windObject.getDouble(OWM_WIND_DIRECTION);


        // Create the weather entry object
        return new WeatherEntry(weatherId, new Date(dateTimeMillis), max, min,
                humidity, pressure, windSpeed, windDirection);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     *
     * @param forecastJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     * ---------------------------------------------------------------------------------------------
     */
    @Nullable
    WeatherResponse parse(final String forecastJsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Is there an error?
        if (hasHttpError(forecastJson)) {
            return null;
        }

        WeatherEntry[] weatherForecast = fromJson(forecastJson);

        return new WeatherResponse(weatherForecast);
    }
}