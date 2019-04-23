

package com.indiewalk.mystic.weatherapp.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.indiewalk.mystic.weatherapp.data.WeatherAppRepository;

import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link WeatherAppRepository} and an ID for the current
 * {@link com.indiewalk.mystic.weatherapp.data.database.WeatherEntry}
 * -------------------------------------------------------------------------------------------------
 */
public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WeatherAppRepository mRepository;
    private final Date mDate;

    public DetailViewModelFactory(WeatherAppRepository repository, Date date) {
        this.mRepository = repository;
        this.mDate       = date;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailActivityViewModel(mRepository, mDate);
    }
}
