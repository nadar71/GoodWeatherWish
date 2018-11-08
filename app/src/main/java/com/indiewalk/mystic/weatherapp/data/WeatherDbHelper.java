package com.indiewalk.mystic.weatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.indiewalk.mystic.weatherapp.data.WeatherContract.WeatherEntry;


// Weather data db cache
// Db helper and data manager
public class WeatherDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "weather.db";

    // TODO : increment it everytime schema changes
    private static final int DATABASE_VERSION = 3;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called at first time db creation. Init first population
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Cretional string
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                        //_ID" generatated by implementation of BaseColumns" interface, which does have a field
                        // Using it as primary key.
                        WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL,"                  +

                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, "                    +

                        // Add a UNIQUE constraint on the date column to replace on conflict :
                        // one weather entry per date, new one replace old one
                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        // creation execute
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * Because database is only a cache for online data, no pb in wiping out data in case of db version changing
     * They are reloaded with next remote service request.
     * so its upgrade policy is simply to discard
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
