/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indiewalk.mystic.weatherapp.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.arch.lifecycle.ViewModel;

import com.indiewalk.mystic.weatherapp.data.SunshineRepository;
import com.indiewalk.mystic.weatherapp.data.database.WeatherEntry;

import java.util.Date;

/**
 * {@link ViewModel} for {@link DetailActivity}
 */
class DetailActivityViewModel extends ViewModel {

    // Weather forecast the user is looking at
    // Set as live data to be observed in DetailActivity
    private final LiveData<WeatherEntry> mWeather;

    // Data for weather forecasts
    private final Date mDate;
    private final SunshineRepository mRepository;

    public DetailActivityViewModel(SunshineRepository repository, Date date) {
        mRepository = repository;
        mDate       = date;
        mWeather    = mRepository.getWeatherByDate(mDate);

    }

    public LiveData<WeatherEntry> getWeather() {
        return mWeather;
    }

    /* public void setWeather(WeatherEntry weatherEntry) {
        mWeather.postValue(weatherEntry);
    }*/
}
