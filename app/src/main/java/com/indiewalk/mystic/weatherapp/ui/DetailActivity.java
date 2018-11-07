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

    private static final String FORECAST_SHARE_HASHTAG = " #GoodWeatherApp";
    private TextView choosenDayWeather;
    private String   choosenDayWeatherParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // TextView to contains/shows detailed weather informations 
        // on day clicked in MainActivity list
        choosenDayWeather = (TextView) findViewById(R.id.show_today_weather_detail);

        // Get day
        Intent intentDetailShowing = getIntent();
        if (intentDetailShowing.hasExtra(Intent.EXTRA_TEXT)){
            choosenDayWeatherParam = intentDetailShowing.getStringExtra(Intent.EXTRA_TEXT);
            choosenDayWeather.setText(choosenDayWeatherParam);
        }

    }


    // -----------------------------------------[ MENU STUFF ]--------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }



    // -------[ MENU FUNCTIONS IMPLEMENTATION ]-----------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Sharing detail day forecast, using a app chooser
        if (id == R.id.action_share) {
            forecastSharing(choosenDayWeatherParam);
            return true;
        }

        // Settings
        if (id == R.id.action_settings){
            openSettings();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void forecastSharing(String choosenDayWeatherParam){
        Log.d(TAG, "forecastSharing Press ");
        String mimeType = "text/plain";
        String title    = "Today weather forecast details.";
        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(choosenDayWeatherParam + FORECAST_SHARE_HASHTAG)
                .startChooser();
    }

    // Open settings
    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
