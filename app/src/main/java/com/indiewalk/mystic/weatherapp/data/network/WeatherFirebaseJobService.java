package com.indiewalk.mystic.weatherapp.data.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;
import com.indiewalk.mystic.weatherapp.old.WeatherSyncTask;
import com.indiewalk.mystic.weatherapp.utilities.InjectorUtils;

public class WeatherFirebaseJobService extends JobService {

    private static final String LOG_TAG = WeatherFirebaseJobService.class.getSimpleName();

    // task that will do the job on background
    // private AsyncTask<Void, Void, Void> mFetchWeatherTask;



    /**
     * Job entry point, called by the Job Dispatcher .
     * Run on the application's main thread, so fetchWeather() do the offload work
     * in a background thread
     *
     * @return  whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        Log.d(LOG_TAG, "Job service started");

        WeatherNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchWeather();

        jobFinished(jobParameters, false);

        return true;


        /*
        mFetchWeatherTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeather(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //  weather data synched
                jobFinished(jobParameters, false);
            }
        };

        mFetchWeatherTask.execute();
        return true;
        */

    }



    /**
     * To interrupt the execution of a running job,
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        /*
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        */
        return true;
    }
}
