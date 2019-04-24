
package eu.indiewalk.mystic.weatherapp.ui.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.indiewalk.mystic.weatherapp.R;
import eu.indiewalk.mystic.weatherapp.data.database.ListWeatherEntry;
import eu.indiewalk.mystic.weatherapp.utilities.WeatherAppDateUtility;
import eu.indiewalk.mystic.weatherapp.utilities.WeatherAppGenericUtility;

import java.util.Date;
import java.util.List;

/**
 * -------------------------------------------------------------------------------------------------
 * Exposes a list of weather forecasts from a list of {@link ListWeatherEntry}
 * to a {@link RecyclerView}.
 * -------------------------------------------------------------------------------------------------
 */
class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // IDs for the ViewType : today and  one of future day
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private final Context mContext;

    // Clicking on list item handler interface
    private final ForecastAdapterOnItemClickHandler mClickHandler;

    // flag for using or not the today forecast highlighted layout
    private final boolean mUseTodayLayout;

    // private List<WeatherEntry> mForecast;
    private List<ListWeatherEntry> mForecast;



    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor a ForecastAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     * ---------------------------------------------------------------------------------------------
     */
    ForecastAdapter(@NonNull Context context, ForecastAdapterOnItemClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new view holder ForecastAdapterViewHolder.
     * Called when Recycler view is set on, one call to this till screen filled allowing scrolling
     * @param viewGroup
     * @param viewType
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Display/updates data in  viewHolder using item position in RecyclerView
     * @param forecastAdapterViewHolder
     * @param position
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        // go to the right position for get the data
        // mCursor.moveToPosition(position);
        ListWeatherEntry currentWeather = mForecast.get(position);

        // Weather Icon
        // int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherIconId = currentWeather.getWeatherIconId();
        int weatherImageResourceId = getWeatherImageId(weatherIconId, position);
        forecastAdapterViewHolder.iconView.setImageResource(weatherImageResourceId);

        // Weather Date
        // Get date from cursor
        // mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        long dateInMillis = currentWeather.getDate().getTime();
        /// Date human readable format
        String dateString = WeatherAppDateUtility.getFriendlyDateString(mContext, dateInMillis, false);
        forecastAdapterViewHolder.dateView.setText(dateString);

        // Weather Description
        String description = WeatherAppGenericUtility.getStringForWeatherCondition(mContext, weatherIconId);
        String descriptionAcc = mContext.getString(R.string.acc_forecast, description);
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionAcc);

        // High (max) temperature
        // mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        double highInCelsius = currentWeather.getMax();
        // Conversion if needed
        String highString = WeatherAppGenericUtility.formatTemperature(mContext, highInCelsius);
        String highAcc = mContext.getString(R.string.acc_max_temp, highString);
        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highAcc);

        // Low (min) temperature
        // mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        double lowInCelsius = currentWeather.getMin();

        // Conversion if needed
        String lowString = WeatherAppGenericUtility.formatTemperature(mContext, lowInCelsius);
        String lowAcc = mContext.getString(R.string.acc_min_temp, lowString);

        // Set the text and content description (for accessibility purposes)
        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowAcc);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Converts the weather icon id from Open Weather to the local image resource id,
     * based on whether the forecast is for today(large) or the future(small ).
     *
     * @param weatherIconId Open Weather icon id
     * @param position  Position in list
     * @return Drawable image resource id for weather
     * ---------------------------------------------------------------------------------------------
     */
    private int getWeatherImageId(int weatherIconId, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {

            // large icon in case of today highlighted forecast
            case VIEW_TYPE_TODAY:
                return WeatherAppGenericUtility
                        .getLargeArtResourceIdForWeatherCondition(weatherIconId);

            case VIEW_TYPE_FUTURE_DAY:
                return WeatherAppGenericUtility
                        .getSmallArtResourceIdForWeatherCondition(weatherIconId);

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Return the number of item to display
     * used behind the scenes to help layout our Views and for animations.
     * @return number of items available in our forecast
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public int getItemCount() {
        if (null == mForecast) return 0;
        return mForecast.size();
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
            // Check differences between :
            // - old forecast list (current list in mForecast)
            // - new forecast list (values in db)
            // dispatch result fo comparison to recycleview adapter view with dispatchUpdatesTo
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


    /**
     * ---------------------------------------------------------------------------------------------
     * Return layout based n view type, today or future day
     * @param viewType
     * @return
     * ---------------------------------------------------------------------------------------------
     */
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
     * The interface that receives onItemClick messages.
     * ---------------------------------------------------------------------------------------------
     */
    public interface ForecastAdapterOnItemClickHandler {
        void onItemClick(Date date);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Single item content holder class for each row item in RecyclerView
     * ---------------------------------------------------------------------------------------------
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconView;

        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        ForecastAdapterViewHolder(View view) {
            super(view);

            iconView = view.findViewById(R.id.weather_icon);
            dateView = view.findViewById(R.id.date);
            descriptionView = view.findViewById(R.id.weather_description);
            highTempView = view.findViewById(R.id.high_temperature);
            lowTempView = view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);
        }

        /**
         * -----------------------------------------------------------------------------------------
         * Get the date from item clicked and handle with adapater onItemClick
         * @param v the View that was clicked
         * -----------------------------------------------------------------------------------------
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Date date = mForecast.get(adapterPosition).getDate();
            mClickHandler.onItemClick(date);
        }
    }
}