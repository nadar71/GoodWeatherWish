package com.indiewalk.mystic.weatherapp.data.database;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.Date;
import java.util.List;


/**
 * Provides all the operations on {@link SunshineDatabase}
 */
@Dao
public interface WeatherDao {
    /**
     * Return the weather of a single day in date
     * @param date
     * @return {@link LiveData} with weather for a single day
     */
    @Query("SELECT  * FROM weather WHERE date = :date")
    LiveData<WeatherEntry> getWeatherByDate(Date date);

    /**
     * Insert all the weather info fetched updating the database, i.e. replace mode
     * @param weather
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(WeatherEntry... weather);

    /**
     * Get all the count of all the entries id after a given date
     * @param date given after which counts the ids
     * @return  the number of ids after the given date
     */
    @Query("SELECT COUNT(id) FROM weather WHERE date > :date")
    int countAllFutureWeather(Date date);

    /**
     * Delete all entries older than date
     * @param date
     */
    @Query("DELETE FROM weather WHERE date < :date")
    void deleteOldData(Date date);

    /**
     * Get all the weather entries starting from date
     * @param date
     * @return
     */
    /*
    @Query("SELECT * FROM weather WHERE date >= :date")
    LiveData<List<WeatherEntry>> getCurrentWeatherForecasts(Date date);
    */

    /**
     * Get all the weather entries as {@link ListWeatherEntry}
     * simplified for {@link com.example.android.sunshine.ui.list.ForecastAdapter} starting from date
     * @param date
     * @return
     */
    @Query("SELECT id, weatherIconId,date, min, max FROM weather WHERE date >= :date")
    LiveData<List<ListWeatherEntry>> getCurrentWeatherForecasts(Date date);

}
