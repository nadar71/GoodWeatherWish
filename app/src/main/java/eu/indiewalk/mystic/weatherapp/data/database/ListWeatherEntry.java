package eu.indiewalk.mystic.weatherapp.data.database;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import eu.indiewalk.mystic.weatherapp.ui.forecasts.MainActivity;

/**
 * -------------------------------------------------------------------------------------------------
 * WeatherEntry for ForecastAdapter in MainActivity
 * -------------------------------------------------------------------------------------------------
 */
public class ListWeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int weatherIconId;
    private Date date;
    private double min;
    private double max;


    /**
     * ---------------------------------------------------------------------------------------------
     * Used by OpenWeatherJsonParser to convert to WeatherEntry object
     * @param weatherIconId Image id for weather
     * @param date Date of weather
     * @param min Min temperature
     * @param max Max temperature
     * ---------------------------------------------------------------------------------------------
     */
    @Ignore
    public ListWeatherEntry(int weatherIconId, Date date, double min, double max) {
        this.weatherIconId = weatherIconId;
        this.date = date;
        this.min = min;
        this.max = max;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Db object entry
     * ---------------------------------------------------------------------------------------------
     */
    public ListWeatherEntry(int id, int weatherIconId, Date date, double min, double max) {
        this.id = id;
        this.weatherIconId = weatherIconId;
        this.date = date;
        this.min = min;
        this.max = max;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getWeatherIconId() {
        return weatherIconId;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

}
