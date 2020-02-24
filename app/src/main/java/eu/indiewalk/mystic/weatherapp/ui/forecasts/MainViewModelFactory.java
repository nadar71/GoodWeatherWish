package eu.indiewalk.mystic.weatherapp.ui.forecasts;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import eu.indiewalk.mystic.weatherapp.data.WeatherAppRepository;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

/**
 * ---------------------------------------------------------------------------------------------
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link WeatherAppRepository} and an ID for the current {@link WeatherEntry}
 * ---------------------------------------------------------------------------------------------
 */
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WeatherAppRepository mRepository;
    // private final Date mDate;

    public MainViewModelFactory(WeatherAppRepository repository) {
        this.mRepository = repository;
        // this.mDate       = date;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
