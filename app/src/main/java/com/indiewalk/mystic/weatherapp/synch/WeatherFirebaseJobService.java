package com.indiewalk.mystic.weatherapp.synch;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class WeatherFirebaseJobService extends JobService {

    // task taht will do the job on background
    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

//  TODO (5) Override onStartJob and within it, spawn off a separate ASyncTask to sync weather data
    /**
     * Job entry point, called by the Job Dispatcher .
     * Run on the application's main thread, need a synchTask that do the job
     * @return  whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

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
    }

    /**
     * To interrupt the execution of a running job,
     * @return whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}
