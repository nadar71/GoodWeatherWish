package com.indiewalk.mystic.weatherapp.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 *  The date must be unique because we load the weather for one location only.
 *  Remember in case of reporting weather to more locations
 */
@Entity(tableName = "weather", indices = {@Index(value="date",unique=true)})
public class WeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int    id;
    private int    weatherIconId;
    private Date   date;
    private double min;
    private double max;
    private double humidity;
    private double pressure;
    private double wind;
    private double degrees;

    /**
     * This constructor is used by OpenWeatherJsonParser. When the network fetch has JSON data, it
     * converts this data to WeatherEntry objects using this constructor.
     * @param weatherIconId Image id for weather
     * @param date Date of weather
     * @param min Min temperature
     * @param max Max temperature
     * @param humidity Humidity for the day
     * @param pressure Barometric pressure
     * @param wind Wind speed
     * @param degrees Wind direction
     */
    @Ignore
    public WeatherEntry(int weatherIconId, Date date, double min, double max, double humidity, double pressure, double wind, double degrees) {
        this.weatherIconId  = weatherIconId;
        this.date           = date;
        this.min            = min;
        this.max            = max;
        this.humidity       = humidity;
        this.pressure       = pressure;
        this.wind           = wind;
        this.degrees        = degrees;
    }


    /**
     * Constructor used by Rooms
     * @param id
     * @param weatherIconId
     * @param date
     * @param min
     * @param max
     * @param humidity
     * @param pressure
     * @param wind
     * @param degrees
     */
    public WeatherEntry(int id, int weatherIconId, Date date, double min, double max, double humidity, double pressure, double wind, double degrees) {
        this.id             = id;
        this.weatherIconId  = weatherIconId;
        this.date           = date;
        this.min            = min;
        this.max            = max;
        this.humidity       = humidity;
        this.pressure       = pressure;
        this.wind           = wind;
        this.degrees        = degrees;
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

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public double getWind() {
        return wind;
    }

    public double getDegrees() {
        return degrees;
    }
}
