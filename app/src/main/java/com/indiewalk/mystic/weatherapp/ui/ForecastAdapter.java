package com.indiewalk.mystic.weatherapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.R;

public class ForecastAdapter extends  RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{

    private String[] mWeatherData;

    // Std Constructor
    public ForecastAdapter() {
    }

    public ForecastAdapter(String[] mWeatherData) {
        this.mWeatherData = mWeatherData;
    }

    // ViewHolder class for item content
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView)  itemView.findViewById(R.id.tv_weather_data);
        }

    }


    // Create a new view holder ForecastAdapterViewHolder
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Item view
        View view = layoutInflater.inflate(R.layout.forecast_list_item, parent, false);

        // return new viewHolder with the item view inside
        ForecastAdapterViewHolder forecastAdapterViewHolder = new ForecastAdapterViewHolder(view);
        return forecastAdapterViewHolder;
    }


    // Connect adpater view holder with item position
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        String forecastForThisDay = mWeatherData[position];
        holder.mWeatherTextView.setText(forecastForThisDay);

    }

    // Return the number of item on list
    @Override
    public int getItemCount() {
        if (mWeatherData != null && mWeatherData.length > 0){
            return mWeatherData.length;
        } else {
            return 0;
        }
    }

    // Update data in the recycle view
    void setWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }


}