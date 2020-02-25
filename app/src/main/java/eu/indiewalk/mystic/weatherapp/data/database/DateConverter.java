

package eu.indiewalk.mystic.weatherapp.data.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * TypeConverter for long to Date and viceversa.
 * This stores the date as a long in the database, but returns it as a Date
 * -------------------------------------------------------------------------------------------------
 */
class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}