

package eu.indiewalk.mystic.weatherapp.ui.daydetail;

import android.arch.lifecycle.LiveData;

import android.arch.lifecycle.ViewModel;

import eu.indiewalk.mystic.weatherapp.data.WeatherAppRepository;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * {@link ViewModel} for {@link DetailActivity}
 * -------------------------------------------------------------------------------------------------
 */
class DetailActivityViewModel extends ViewModel {

    // Weather forecast the user is looking at
    // Set as live data to be observed in DetailActivity
    private final LiveData<WeatherEntry> mWeather;

    // Data for weather forecasts
    private final Date mDate;
    private final WeatherAppRepository mRepository;

    public DetailActivityViewModel(WeatherAppRepository repository, Date date) {
        mRepository = repository;
        mDate       = date;
        mWeather    = mRepository.getWeatherByDate(mDate);

    }

    public LiveData<WeatherEntry> getWeather() {
        return mWeather;
    }

    /* public void setWeather(WeatherEntry weatherEntry) {
        mWeather.postValue(weatherEntry);
    }*/
}
