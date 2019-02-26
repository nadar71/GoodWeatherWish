package com.indiewalk.mystic.weatherapp.data.database;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * More efficient {@link WeatherEntry} with the only fields need for
 * {@link com.indiewalk.mystic.weatherapp.ui.list.ForecastAdapter}
 * in {@link com.indiewalk.mystic.weatherapp.ui.list.MainActivity}
 * -------------------------------------------------------------------------------------------------
 */
public class ListWeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int    id;
    private int    weatherIconId;
    private Date   date;
    private double min;
    private double max;


    /**
     * ---------------------------------------------------------------------------------------------
     * This constructor is used by OpenWeatherJsonParser. When the network fetch has JSON data, it
     * converts this data to WeatherEntry objects using this constructor.
     * @param weatherIconId Image id for weather
     * @param date Date of weather
     * @param min Min temperature
     * @param max Max temperature
     * ---------------------------------------------------------------------------------------------
     */
    @Ignore
    public ListWeatherEntry(int weatherIconId, Date date, double min, double max) {
        this.weatherIconId =  weatherIconId;
        this.date =           date;
        this.min =            min;
        this.max =            max;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor used by Rooms
     * @param id
     * @param weatherIconId
     * @param date
     * @param min
     * @param max
     * ---------------------------------------------------------------------------------------------
     */
    public ListWeatherEntry(int id, int weatherIconId, Date date, double min, double max) {
        this.id =             id;
        this.weatherIconId =  weatherIconId;
        this.date =           date;
        this.min =            min;
        this.max =            max;
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
