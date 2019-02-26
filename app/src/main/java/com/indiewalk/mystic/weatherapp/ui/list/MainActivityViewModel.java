package com.indiewalk.mystic.weatherapp.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.indiewalk.mystic.weatherapp.data.SunshineRepository;
import com.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;
import com.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

import java.util.Date;
import java.util.List;

/**
 * {@link ViewModel} for {@link MainActivity}
 */
public class MainActivityViewModel extends ViewModel {

    // List of Weather forecasts the user is looking at
    // Set as live data to be observed in DetailActivity
    private final LiveData<List<ListWeatherEntry>> mListWeather;

    // Data for weather forecasts
    // private final Date mDate;
    private final SunshineRepository mRepository;

    public MainActivityViewModel(SunshineRepository repository){ //, Date date) {
        mRepository  = repository;
        // mDate        = date;
        mListWeather = mRepository.getCurrentWeatherForecasts();

    }

    public LiveData<List<ListWeatherEntry>> getWeatherList() {
        return mListWeather;
    }
}


