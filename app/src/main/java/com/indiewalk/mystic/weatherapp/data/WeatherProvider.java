package com.indiewalk.mystic.weatherapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;

import java.net.URI;

// ContentProvider for app's data. Allows bulkInsert data, query , delete data.
public class WeatherProvider extends ContentProvider {

    // static constant  to identify the URIs this ContentProvider can handles.
    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    // Static URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher.
     * It will matches each URI to the CODE_WEATHER and  CODE_WEATHER_WITH_DATE constants defined above.
     * @return A UriMatcher
     */
    public static UriMatcher buildUriMatcher() {

        // Init UriMatcher to root URI passing the path code for NO_MATCH as commonly used.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Set authority=domain here
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // Add each URIs to UriMatcher
        // content://com.indiewalk.mystic.weatherapp/weather/ */
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER);

        // content://com.indiewalk.mystic.weatherapp/weather/1472214172
        // "/#" means for any numbers following PATH_WEATHER return CODE_WEATHER_WITH_DATE code
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return matcher;
    }

    /**
     * Init content provider on startup (NB : not heavy load here, it starts in main thread)
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        // instantiate our mOpenHelper (lightweight operation)
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }




    /**
     * Handles requests for inserting a set of new rows use case.
     * @param uri    The content:// URI of the insertion request.
     * @param values Array of column_name/value pairs to add .
     * @return       Rows counts inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long weatherDate =
                                value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if (!WeatherAppDateUtility.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }

                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++; // increment for successful insertion
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // notify changes
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }





    /**
     * Handles query requests from clients.
     * @param uri           The URI to query
     * @param projection    List of columns to put into cursor. If null, all columns included.
     * @param selection     A selection criteria . If null, then all rows.
     * @param selectionArgs Include ?s in selection, will be replaced by  values from selectionArgs, in order of appeareance
     * @param sortOrder     Sorting method.
     * @return              A Cursor with query results
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            //content://com.example.android.sunshine/weather/1472214172
            case CODE_WEATHER_WITH_DATE: {

                // Get the date from URI from last path segment
                String normalizedUtcDateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(

                        WeatherContract.WeatherEntry.TABLE_NAME,
                        //columns we want returned in Cursor, otherwise null for all
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            // content://com.example.android.sunshine/weather/
            case CODE_WEATHER: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at given URI.
     * Optional : arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query.
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement.
     * @return              Number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        // if selection == null, set to 1 : this ensure to return the number of rows deleted,
        // otherwise deleting with null it won't happens (SQLite documentations)
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // notifying deletion just happened
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implemented");
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "Not need to implemented. Only bulkInsert necessary");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }

    // For testing
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}