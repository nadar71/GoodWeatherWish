package eu.indiewalk.mystic.weatherapp.ui.forecasts;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import eu.indiewalk.mystic.weatherapp.data.WeatherAppRepository;
import eu.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;

import java.util.List;

/**
 * -------------------------------------------------------------------------------------------------
 * {@link ViewModel} for {@link MainActivity}
 * -------------------------------------------------------------------------------------------------
 */
public class MainActivityViewModel extends ViewModel {

    // List of Weather forecasts the user is looking at
    // Set as live data to be observed in DetailActivity
    private final LiveData<List<ListWeatherEntry>> mListWeather;

    // Data for weather forecasts
    // private final Date mDate;
    private final WeatherAppRepository mRepository;

    public MainActivityViewModel(WeatherAppRepository repository){ //, Date date) {
        mRepository  = repository;
        // mDate        = date;
        // keep ListWeatherEntry updated retrieving data from network/db
        mListWeather = mRepository.getCurrentWeatherForecasts();
    }

    public LiveData<List<ListWeatherEntry>> getWeatherList() {
        return mListWeather;
    }
}


