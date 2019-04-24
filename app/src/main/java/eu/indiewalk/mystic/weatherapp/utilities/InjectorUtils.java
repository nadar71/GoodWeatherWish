

package eu.indiewalk.mystic.weatherapp.utilities;

import android.content.Context;

import eu.indiewalk.mystic.weatherapp.AppExecutors;
import eu.indiewalk.mystic.weatherapp.data.WeatherAppRepository;
import eu.indiewalk.mystic.weatherapp.data.database.WeatherAppDatabase;
import eu.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource;
import eu.indiewalk.mystic.weatherapp.ui.detail.DetailViewModelFactory;
import eu.indiewalk.mystic.weatherapp.ui.list.MainViewModelFactory;

import java.util.Date;

/**
 * -------------------------------------------------------------------------------------------------
 * Provides static methods to inject the various classes needed for Weatherapp
 * -------------------------------------------------------------------------------------------------
 */
public class InjectorUtils {

    // Inject the component WeatherAppRepository
    public static WeatherAppRepository provideRepository(Context context) {
        WeatherAppDatabase database = WeatherAppDatabase.getInstance(context.getApplicationContext());

        AppExecutors executors = AppExecutors.getInstance();

        WeatherNetworkDataSource networkDataSource =
                WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);

        return WeatherAppRepository.getInstance(database.weatherDao(), networkDataSource, executors);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Inject the component WeatherNetworkDataSource (used for example in WeatherSyncIntentService)
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static WeatherNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());

        AppExecutors executors = AppExecutors.getInstance();

        return WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Observe repository changes for detail activity data
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Date date) {
        WeatherAppRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, date);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Observe repository changes for main activity data
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        WeatherAppRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

}