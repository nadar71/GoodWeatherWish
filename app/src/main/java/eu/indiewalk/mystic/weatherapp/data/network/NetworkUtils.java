
package eu.indiewalk.mystic.weatherapp.data.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*
     * Sunshine was originally built to use OpenWeatherMap's API. However, we wanted to provide
     * a way to much more easily test the app and provide more varied weather data. After all, in
     * Mountain View (Google's HQ), it gets very boring looking at a forecast of perfectly clear
     * skies at 75°F every day... (UGH!) The solution we came up with was to host our own fake
     * weather server. With this server, there are two URL's you can use. The first (and default)
     * URL will return dynamic weather data. Each time the app refreshes, you will get different,
     * completely random weather data. This is incredibly useful for testing the robustness of your
     * application, as different weather JSON will provide edge cases for some of your methods.
     *
     */
    private static final String DYNAMIC_WEATHER_URL =
            // "https://andfun-weather.udacity.com/weather";
            "https://api.openweathermap.org/data/2.5/weather";

    private static final String STATIC_WEATHER_URL =
            // "https://andfun-weather.udacity.com/staticweather";
            "https://api.openweathermap.org/data/2.5/forecast";

    public static final String MY_OPENWEATHER_APPID = "736a5abc6c0957977e0e75002fbdca8e";

    // private static final String FORECAST_BASE_URL   = DYNAMIC_WEATHER_URL;

    private static final String FORECAST_BASE_URL   = STATIC_WEATHER_URL;

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API. If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this training.
     */

    // The format we want our API to return
    private static final String format = "json";

    // The units we want our API to return
    private static final String units  = "metric";


    // The query parameter allows us to provide a location string to the API
    private static final String QUERY_PARAM   = "q";
    private static final String LAT_PARAM     = "lat";
    private static final String LON_PARAM     = "lon";

    // The format parameter allows us to designate whether we want JSON or XML from our API
    private static final String FORMAT_PARAM  = "mode";

    // The units parameter allows us to designate whether we want metric units or imperial units
    private static final String UNITS_PARAM   = "units";

    // The days parameter allows us to designate how many days of weather data we want
    private static final String DAYS_PARAM    = "cnt";
    private static final String APPID         = "APPID";

    /**
     * ---------------------------------------------------------------------------------------------
     * Retrieves the proper URL to query for the weather data. The reason for both this method as
     * well as {@link #buildUrlWithLocationQuery(String)} is two fold.
     * <p>
     * 1) You should be able to just use one method when you need to create the URL within the
     * app instead of calling both methods.
     * 2) Later , you are going to add an alternate method of allowing the user
     * to select their preferred location. Once you do so, there will be another way to form
     * the URL using a latitude and longitude rather than just a location String. This method
     * will "decide" which URL to build and return it.
     *
     * @param context used to access other Utility methods
     * @return URL to query weather service
     * ---------------------------------------------------------------------------------------------
     */
    public static URL getUrl(Context context) {
       /*
        if (UserPreferencesData.isLocationLatLonAvailable(context)) {
            double[] preferredCoordinates = UserPreferencesData.getLocationCoordinates(context);
            double latitude = preferredCoordinates[0];
            double longitude = preferredCoordinates[1];
            return buildUrlWithLatitudeLongitude(latitude, longitude);
        } else {
            String locationQuery = UserPreferencesData.getPreferredWeatherLocation(context);
            return buildUrlWithLocationQuery(locationQuery);
        }
       */

        // String locationQuery = "Mountain View,CA";
        // String locationQuery = "Sidney,US";
        // String locationQuery = "Mountain View,CA 94043";
        // String locationQuery = "Chignolo d'isola,IT";
        // String locationQuery = "London,us";
        String locationQuery = "Rome,IT";


        return buildUrlWithLocationQuery(locationQuery);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param latitude  The latitude of the location
     * @param longitude The longitude of the location
     * @return The Url to use to query the weather server.
     * ---------------------------------------------------------------------------------------------
     */
    private static URL buildUrlWithLatitudeLongitude(Double latitude, Double longitude) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, String.valueOf(latitude))
                .appendQueryParameter(LON_PARAM, String.valueOf(longitude))
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(WeatherNetworkDataSource.NUM_DAYS))
                .appendQueryParameter(APPID, MY_OPENWEATHER_APPID)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     * ---------------------------------------------------------------------------------------------
     */
    private static URL buildUrlWithLocationQuery(String locationQuery) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(WeatherNetworkDataSource.NUM_DAYS))
                .appendQueryParameter(APPID, MY_OPENWEATHER_APPID)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     * ---------------------------------------------------------------------------------------------
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}