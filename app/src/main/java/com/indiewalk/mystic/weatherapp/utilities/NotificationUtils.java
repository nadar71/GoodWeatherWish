package com.indiewalk.mystic.weatherapp.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.ui.settings.UserPreferencesData;
// import com.indiewalk.mystic.weatherapp.data.provider.WeatherContract;
import com.indiewalk.mystic.weatherapp.ui.detail.DetailActivity;


public class NotificationUtils {


// TODO : must use livedata/viewmodel/room for notifications, no more provider
/*
    // The columns of data displayed within notification for short forecast
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    // ... indexes fro array above for quick access
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;


    // Notification ID to refer to notification displayed.
    private static final int WEATHER_NOTIFICATION_ID = 1536;


    */

    /**
     * ---------------------------------------------------------------------------------------------
     * Build and displays new notification for today updated forecast
     * @param context Context used to query our ContentProvider and use various Utility methods
     * ---------------------------------------------------------------------------------------------
     */

    /*
    public static void notifyUserOfNewWeather(Context context) {

        // Build the URI for today request
        Uri todaysWeatherUri = WeatherContract.WeatherEntry
                .buildWeatherUriWithDate(WeatherAppDateUtility.normalizeDate(System.currentTimeMillis()));

        // get Cursor
        Cursor todayWeatherCursor = context.getContentResolver().query(
                todaysWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION, // limits the columns returned as above definition
                null,
                null,
                null);

        // Move cursor to first
        if (todayWeatherCursor.moveToFirst()) {

            // Weather ID as returned by API, used to identify the icon to be used
            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);

            Resources resources = context.getResources();
            // laarge icons
            int largeArtResourceId = WeatherAppGenericUtility.getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    resources,
                    largeArtResourceId);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, weatherId, high, low);

            // small icos
            int smallArtResourceId = WeatherAppGenericUtility.getSmallArtResourceIdForWeatherCondition(weatherId);

            // NotificationCompat Builder is a very convenient way to build backward-compatible
            // notifications. In order to use it, we provide a context and specify a color for the
            // notification, a couple of different icons, the title for the notification, and
            // finally the text of the notification, which in our case in a summary of today's
            // forecast.
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            // Start the DetailActivity by Intent clicking on notification.
            Intent detailIntentForToday = new Intent(context, DetailActivity.class);
            detailIntentForToday.setData(todaysWeatherUri);

            // Create PendingINtent
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set content Intent of the NotificationBuilder
            notificationBuilder.setContentIntent(resultPendingIntent);

            // Reference to the NotificationManager
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Notify user withWEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on
            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

            // Save the current time after notification showed.
            // Useful at the next forecast update if we must show another notfication or not
            UserPreferencesData.saveLastNotificationTime(context, System.currentTimeMillis());
        }

        todayWeatherCursor.close();
    }

    */

    /**
     * ---------------------------------------------------------------------------------------------
     * Create notification text at weather refresh
     * The String model:
     * Forecast: Sunny - High: 14°C Low 7°C
     * ---------------------------------------------------------------------------------------------
     *
     * @param context
     * @param weatherId ID as determined by Open Weather Map
     * @param high      High temperature
     * @param low       Low temperature
     * @return          Summary of a particular day's forecast
     */

    /*
    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        // Short description of the weather, as provided by the API. e.g "clear" vs "sky is clear".
        String shortDescription = WeatherAppGenericUtility
                .getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        // forecast summary using String's format method
        String notificationText = String.format(notificationFormat,
                shortDescription,
                WeatherAppGenericUtility.formatTemperature(context, high),
                WeatherAppGenericUtility.formatTemperature(context, low));

        return notificationText;
    }

    */



}
