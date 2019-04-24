package eu.indiewalk.mystic.weatherapp.ui.settings;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import eu.indiewalk.mystic.weatherapp.R;


/**
 * -------------------------------------------------------------------------------------------------
 * Where all the setting are summarized and defined
 * -------------------------------------------------------------------------------------------------
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button as up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Back to home with back btn pressed
     * @param item
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
