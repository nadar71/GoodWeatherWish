package com.indiewalk.mystic.weatherapp.old;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.indiewalk.mystic.weatherapp.data.network.NetworkUtils;
import com.indiewalk.mystic.weatherapp.ui.settings.UserPreferencesData;
import com.indiewalk.mystic.weatherapp.data.provider.WeatherContract;
import com.indiewalk.mystic.weatherapp.utilities.NotificationUtils;

import java.net.URL;

public class WeatherSyncTask {


    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider.
     * Notify the user that new weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND
     * if they haven't disabled notifications in the preferences screen.
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context) {
        try {

            URL weatherRequestUrl = NetworkUtils.getUrl(context);

            // RetrieveJSON response
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            // Parse the JSON into a list of weather values
            ContentValues[] weatherValues = null; // OpenWeatherJsonUtility.getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            // In cases JSON contained an error code, return null.
            if (weatherValues != null && weatherValues.length != 0) {
                // handle on the ContentResolver to delete and insert data
                ContentResolver weatherContentResolver = context.getContentResolver();

                // Delete old weather data, don't need  keep multiple days' data
                weatherContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                // Insert new weather data
                weatherContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);


                // Check if notifications are enabled
                boolean notificationsEnabled = UserPreferencesData.areNotificationsEnabled(context);

                // Send a new notification if the last was shown more than 1 day ago
                long timeSinceLastNotification = UserPreferencesData.getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                // If more than a day have passed and notifications are enabled, notify the user
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
