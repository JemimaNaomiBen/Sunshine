package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;

// TODO (15) Add a class file called ForecastAdapter
// TODO (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {


//  TODO (6) Declare constant IDs for the ViewType for today and for a future day
private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    //  TODO (1) Declare a private final Context field called mContext
    private final Context mContext;
    //  TODO (14) Remove the mWeatherData declaration and the setWeatherData method

    // TODO (3) Create a final private ForecastAdapterOnClickHandler called mClickHandler
    final private ForecastAdapterOnClickHandler mClickHandler;

    // TODO (1) Add an interface called ForecastAdapterOnClickHandler
    public interface ForecastAdapterOnClickHandler {
        // TODO (2) Within that interface, define a void method that access a String as a parameter
        //ODO (36) Refactor onClick to accept a long as its parameter rather than a String
        void onClick(long date);
    }

    //  TODO (7) Declare a private boolean called mUseTodayLayout
    private boolean mUseTodayLayout;
    //  TODO (2) Declare a private Cursor field called mCursor
    private Cursor mCursor;

    // TODO (4) Add a ForecastAdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
    //  TODO (3) Add a Context field to the constructor and store that context in mContext
    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

        // TODO (8) Set mUseTodayLayout to the value specified in resources
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    // TODO (24) Override onCreateViewHolder
    // TODO (25) Within onCreateViewHolder, inflate the list item xml into a view
    // TODO (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

                int layoutId;

        switch (viewType) {

//          COMPLETED (12) If the view type of the layout is today, use today layout
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }

//          COMPLETED (13) If the view type of the layout is future day, use future day layout
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }

//          COMPLETED (14) Otherwise, throw an IllegalArgumentException
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.forecast_list_item, viewGroup, false);

        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }
    // TODO (27) Override onBindViewHolder
    // TODO (28) Set the text of the TextView to the weather for this list item's position
    // TODO (5) Delete the current body of onBindViewHolder
    // TODO (6) Move the cursor to the appropriate position
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        // TODO (7) Replace the single TextView with Views to display all of the weather info
        /****************
         * Weather Icon *
         ****************/
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;


        int viewType = getItemViewType(position);

        switch (viewType) {
//          COMPLETED (15) If the view type of the layout is today, display a large icon
            case VIEW_TYPE_TODAY:
                weatherImageId = SunshineWeatherUtils
                        .getLargeArtResourceIdForWeatherCondition(weatherId);
                break;

//          COMPLETED (16) If the view type of the layout is today, display a small icon
            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = SunshineWeatherUtils
                        .getSmallArtResourceIdForWeatherCondition(weatherId);
                break;

//          COMPLETED (17) Otherwise, throw an IllegalArgumentException
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/
        /* Read date from the cursor */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);

        /* Display friendly date string */
        forecastAdapterViewHolder.dateView.setText(dateString);

        /***********************
         * Weather Description *
         ***********************/
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        /* Create the accessibility (a11y) String from the weather description */
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionA11y);

        /**************************
         * High (max) temperature *
         **************************/
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        /* Create the accessibility (a11y) String from the weather description */
        String highA11y = mContext.getString(R.string.a11y_high_temp, highString);

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highA11y);

        /*************************
         * Low (min) temperature *
         *************************/
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowA11y = mContext.getString(R.string.a11y_low_temp, lowString);

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowA11y);
    }


    // TODO (29) Override getItemCount
    // TODO (30) Return 0 if mWeatherData is null, or the size of mWeatherData if it is not null
    // TODO (9) Delete the current body of getItemCount
    // TODO (10) If mCursor is null, return 0. Otherwise, return the count
    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

//  TODO (9) Override getItemViewType

    //  TODO (10) Within getItemViewType, if mUseTodayLayout is true and position is 0, return the ID for today viewType
//   TODO (11) Otherwise, return the ID for future day viewType
    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0)
            return VIEW_TYPE_TODAY;
            else{
                return VIEW_TYPE_FUTURE_DAY;
        }
    }

    //  TODO (11) Create a new method that allows you to swap Cursors.
    //  TODO (12) After the new Cursor is set, call notifyDataSetChanged
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // TODO (47) Create the default constructor (we will pass in parameters in a later lesson)

    /**
     * public ForecastAdapter() {
     * <p>
     * }
     */
    // TODO (5) Implement OnClickListener in the ForecastAdapterViewHolder class

    // TODO (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // TODO (17) Extend RecyclerView.ViewHolder
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TODO (18) Create a public final TextView variable called mWeatherTextView
        //      TODO (4) Replace the weatherSummary TextView with individual weather detail TextViews
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;
        //      TODO (5) Add an ImageView for the weather icon
        final ImageView iconView;
        // TODO (19) Create a constructor for this class that accepts a View as a parameter
        // TODO (20) Call super(view) within the constructor for ForecastAdapterViewHolder
        // TODO (21) Using view.findViewById, get a reference to this layout's TextView and save it to mWeatherTextView
        ForecastAdapterViewHolder(View view) {
            super(view);
//          TODO (6) Get references to all new views and delete this line
            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);

            // TODO (7) Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }
        // TODO (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
//          TODO (37) Instead of passing the String for the clicked item, pass the date from the cursor
            mCursor.moveToPosition(adapterPosition);
            long dateMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateMillis);

        }
    }
}






