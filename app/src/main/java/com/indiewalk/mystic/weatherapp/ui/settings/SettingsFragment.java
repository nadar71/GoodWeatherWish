package com.indiewalk.mystic.weatherapp.ui.settings;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.network.WeatherNetworkDataSource;
// import com.indiewalk.mystic.weatherapp.data.provider.WeatherContract;
// import com.indiewalk.mystic.weatherapp.old.WeatherSyncUtils;
import com.indiewalk.mystic.weatherapp.utilities.InjectorUtils;

/**
 * -------------------------------------------------------------------------------------------------
 * The SettingsFragment serves as the display for all of the user's settings.
 * Used in activity_settings layout
 * -------------------------------------------------------------------------------------------------
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {


    /**
     * ---------------------------------------------------------------------------------------------
     * Set preferences summary for each kind of preferences' item
     * @param preference
     * @param value
     * ---------------------------------------------------------------------------------------------
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            // list preferences : set the value corresponding to the key in entry
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }



    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Add generic preferences defined in the XML file
        addPreferencesFromResource(R.xml.setting_pref);

        // get the preference list
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        // Set summary for each preferences
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Callback for shard preferences changes
     * @param sharedPreferences
     * @param key
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {
            // location changed
            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
            UserPreferencesData.resetLocationCoordinates(activity);

            // TODO  : TEST resync weather data; data on screen must be updated too
            //
            // WeatherSyncUtils.startImmediateSync(activity);

            // resynch with remote data
            WeatherNetworkDataSource networkDataSource =
                    InjectorUtils.provideNetworkDataSource(activity.getApplicationContext());
            networkDataSource.fetchWeather();


        } else if (key.equals(getString(R.string.pref_units_key))) {
            // units  changed. update lists of weather entries accordingly
            // TODO : fix in db & c. not using provider
            // activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }
}
