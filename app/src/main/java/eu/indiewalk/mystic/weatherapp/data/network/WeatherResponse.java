package eu.indiewalk.mystic.weatherapp.data.network;

import android.support.annotation.NonNull;

import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

/**
 * -------------------------------------------------------------------------------------------------
 * Class to store weather forecasts response from remote request.
 * Used in WetherNetworkDataSource.fetchWeather
 * -------------------------------------------------------------------------------------------------
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