package com.indiewalk.mystic.weatherapp.data.network;

import android.support.annotation.NonNull;

import com.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

/**
 * Weather response from the backend. Contains the weather forecasts.
 */
class WeatherResponse {

    @NonNull
    private final WeatherEntry[] mWeatherForecast;

    public WeatherResponse(@NonNull final WeatherEntry[] weatherForecast) {
        mWeatherForecast = weatherForecast;
    }

    public WeatherEntry[] getWeatherForecast() {
        return mWeatherForecast;
    }
}