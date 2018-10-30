package com.indiewalk.mystic.weatherapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.indiewalk.mystic.weatherapp.R;

public class DetailActivity extends AppCompatActivity {
    private static  final  String TAG = DetailActivity.class.getSimpleName();
    TextView todayWeather;
    private String todayWeatherPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        todayWeather = (TextView) findViewById(R.id.show_today_weather_detail);

        // show day clicked forecast
        Intent intentDetailShowing = getIntent();

        if (intentDetailShowing.hasExtra(Intent.EXTRA_TEXT)){
            todayWeatherPassed = intentDetailShowing.getStringExtra(Intent.EXTRA_TEXT);
            todayWeather.setText(todayWeatherPassed);
        }

    }


    // -----------------------------------------[ MENU STUFF ]--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }


    private void forecastSharing(String todayWeatherPassed){
        Log.d(TAG, "forecastSharing Press ");
        String mimeType = "text/plain";
        String title    = "Today weather forecast details.";
        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(todayWeatherPassed)
                .startChooser();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Refresh data
        if (id == R.id.action_share) {
            forecastSharing(todayWeatherPassed);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
