package com.indiewalk.mystic.weatherapp.ui.list;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.indiewalk.mystic.weatherapp.R;
import com.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import com.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;

import java.util.List;


public class ForecastAdapter extends  RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{

    // IDs for the ViewType : today and  future day
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;


    // Clicking on list item handler interface
    private final ForecastAdapterOnClickHandler mClickHandler;

    // private List<WeatherEntry> mForecast;
    private List<ListWeatherEntry> mForecast;

    private final Context mContext;
    private       Cursor  mCursor;

    // flag for using or not the today forecast highlighted layout
    private boolean mUseTodayLayout;



    public interface ForecastAdapterOnClickHandler{
        void onClick(long date);
    }


    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler  clickHandler) {
        mClickHandler = clickHandler;
        mContext      = context;
        // set layout
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    // Single item content holder
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        final ImageView iconView;

        public ForecastAdapterViewHolder(View view) {
            super(view);

            iconView        = (ImageView) view.findViewById(R.id.weather_icon);
            dateView        = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView    = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView     = (TextView) view.findViewById(R.id.low_temperature);

            itemView.setOnClickListener(this);
        }


        /**
         * -----------------------------------------------------------------------------------------
         * Get the date from item clicked and handle with adapater onClick
         * @param v the View that was clicked
         * -----------------------------------------------------------------------------------------
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new view holder ForecastAdapterViewHolder
     * @param parent
     * @param viewType
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // set layout based n view type, toady or future day
        int layoutId = getLayoutIdByType(viewType);

        // Item view
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        // return new viewHolder with the item view inside
        ForecastAdapterViewHolder forecastAdapterViewHolder = new ForecastAdapterViewHolder(view);
        return forecastAdapterViewHolder;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return layout based n view type, toady or future day
     * @param viewType
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @NonNull
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                return R.layout.list_item_forecast_today;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                return R.layout.forecast_list_item;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Connect viewHolder with item position
     * @param forecastAdapterViewHolder
     * @param position
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        // go to the right position for get the data
        mCursor.moveToPosition(position);

        // Weather Icon
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;

        weatherImageId = getWeatherImageId(position, weatherId);
        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);


        // Weather Date
        // Get date from cursor
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        // Date human readable format
        String dateString = WeatherAppDateUtility.getFriendlyDateString(mContext, dateInMillis, false);
        forecastAdapterViewHolder.dateView.setText(dateString);


        // Weather Description
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(mContext, weatherId);
        String descriptionAcc = mContext.getString(R.string.acc_forecast, description);
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionAcc);

        // High (max) temperature
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        // Conversion if needed
        String highString = WeatherAppGenericUtility.formatTemperature(mContext, highInCelsius);
        String highAcc = mContext.getString(R.string.acc_max_temp, highString);
        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highAcc);

        // Low (min) temperature
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        // Conversion if needed
        String lowString = WeatherAppGenericUtility.formatTemperature(mContext, lowInCelsius);
        String lowAcc = mContext.getString(R.string.acc_min_temp, lowString);
        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowAcc);


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Get correct weather icon from weatherId,
     * based on whether the forecast is for today(large)
     * or the future(small ).
     *
     * @param weatherId Open Weather icon id
     * @param position  Position in list
     * @return Drawable image resource id for weather
     * ---------------------------------------------------------------------------------------------
     */
    private int getWeatherImageId(int position, int weatherId) {
        int weatherImageId;
        int viewType = getItemViewType(position);

        // large icon in case of today highlighted forecast
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                weatherImageId = WeatherAppGenericUtility
                        .getLargeArtResourceIdForWeatherCondition(weatherId);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = WeatherAppGenericUtility
                        .getSmallArtResourceIdForWeatherCondition(weatherId);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        return weatherImageId;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return the number of item from the cursor
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return an id for item view depending on the position.
     * For 0 position (first item) it is needed to create (PORTRAIT ONLY) a today highlighted
     * forecast
     * @param position index within our RecyclerView and Cursor
     * @return the view type (today or future day)
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Swaps the list used by the ForecastAdapter for showing new data weather data, and notifying
     * about changes.
     * Called by {@link MainActivity} after a load has finished or to reset them.
     *
     * @param newForecast the new list of forecasts to use as ForecastAdapter's data source
     * ---------------------------------------------------------------------------------------------
     */
    void swapForecast(final List<ListWeatherEntry> newForecast) {
        // No forecast data, recreate all
        if (mForecast == null) {
            mForecast = newForecast;
            notifyDataSetChanged();
        } else {
            /*
             * Otherwise we use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in mForecast, where the new list is the new
             * values passed in from the observing the database.
             */

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mForecast.size();
                }

                @Override
                public int getNewListSize() {
                    return newForecast.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mForecast.get(oldItemPosition).getId() ==
                            newForecast.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ListWeatherEntry newWeather = newForecast.get(newItemPosition);
                    ListWeatherEntry oldWeather = mForecast.get(oldItemPosition);
                    return newWeather.getId() == oldWeather.getId() && newWeather.getDate().equals(oldWeather.getDate());
                }
            });
            mForecast = newForecast;
            result.dispatchUpdatesTo(this);
        }



    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Swap the cursor for showing new data, and notifying about changes.
     * Called from MainActivity after finishing loading data, or to reset them.
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     * ---------------------------------------------------------------------------------------------
     */
    /*
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
    */



}
