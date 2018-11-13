package com.indiewalk.mystic.weatherapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;


/**
 *  Adapter class for recycle view
 */
public class ForecastAdapter extends  RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{

    private final ForecastAdapterOnClickHandler mClickHandler;

    private final Context mContext;
    private Cursor mCursor;

    public interface ForecastAdapterOnClickHandler{
        void onClick(long date);
    }


    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler  clickHandler) {
        mClickHandler = clickHandler;
        mContext      = context;
    }

    // ViewHolder class for single item content
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = (TextView)  itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }


        /**
         * Get the date from item clicked and handle with adapater onClick
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String daySelectedForecast  = mWeatherTextView.getText().toString();
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
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


    // Connect viewHolder with item position
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        // go to the right position for get the data
        mCursor.moveToPosition(position);

        // Weather Summary
        // Read date from cursor
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);

        // Get human readable string
        String dateString = WeatherAppDateUtility.getFriendlyDateString(mContext, dateInMillis, false);

        // Use the weatherId to link description
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(mContext, weatherId);

        // High temperature from cursor (in celsius)
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);

        // Low temperature from cursor (in celsius)
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String highAndLowTemperature =
                WeatherAppGenericUtility.formatHighLows(mContext, highInCelsius, lowInCelsius);

        String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;

        forecastAdapterViewHolder.mWeatherTextView.setText(weatherSummary);


    }

    // Return the number of item from the cursor
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    /**
     * Swap the cursor for showing new data, and notifying about changes.
     * Called from MainActivity after finishing loading data, or to reset them.
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }



}
