package com.indiewalk.mystic.weatherapp.utilities;

import android.content.Context;

import com.indiewalk.mystic.weatherapp.AppExecutors;
import com.indiewalk.mystic.weatherapp.data.SunshineRepository;
import com.indiewalk.mystic.weatherapp.data.database.SunshineDatabase;
import com.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource;
import com.indiewalk.mystic.weatherapp.ui.detail.DetailViewModelFactory;
import com.indiewalk.mystic.weatherapp.ui.list.MainActivityViewModel;
import com.indiewalk.mystic.weatherapp.ui.list.MainViewModelFactory;

import java.util.Date;
/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    // Inject the component SunshineRepository
   public static SunshineRepository provideRepository(Context context) {
       SunshineDatabase database = SunshineDatabase.getInstance(context.getApplicationContext());

       AppExecutors executors = AppExecutors.getInstance();

       WeatherNetworkDataSource networkDataSource =
               WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);

       return SunshineRepository.getInstance(database.weatherDao(), networkDataSource, executors);
   }

   // Inject the component WeatherNetworkDataSource (used for example in SunshineSyncIntentService)
   public static WeatherNetworkDataSource provideNetworkDataSource(Context context) {
       // This call to provide repository is necessary if the app starts from a service - in this
       // case the repository will not exist unless it is specifically created.
       provideRepository(context.getApplicationContext());

       AppExecutors executors = AppExecutors.getInstance();

       return WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
   }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Date date) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, date);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

}