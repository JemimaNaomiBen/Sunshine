package com.example.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.databinding.ActivityDetailBinding;
import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;

// TODO (1) Create a new Activity called DetailActivity using Android Studio's wizard
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    //  TODO (18) Create a String array containing the names of the desired data columns from our ContentProvider

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    //  TODO (19) Create constant int values representing each column name's position above
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES= 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

//  TODO (20) Create a constant int to identify our loader used in DetailActivity
    private static final int ID_DETAIL_LOADER = 353;

    private String mForecastSummary;

    //  TODO (15) Declare a private Uri field called mUri
    private Uri mUri;


    //  TODO (2) Remove all the TextView declarations

    //  TODO (3) Declare an ActivityDetailBinding field called mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      TODO (4) Remove the call to setContentView
//      TODO (6) Instantiate mDetailBinding using DataBindingUtil
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        //      TODO (14) Remove the code that checks for extra text

    // TODO (16) Use getData to get a reference to the URI passed with this Activity's Intent
    mUri = getIntent().getData();
//      TODO (17) Throw a NullPointerException if that URI is null
        if (mUri == null){
        throw new NullPointerException("URI for detail activity cannot be null");
    }
        //      TODO (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }



    // TODO (4) Display the menu and implement the forecast sharing functionality

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }
        //  TODO (22) Override onCreateLoader

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            switch (id){
                case ID_DETAIL_LOADER:
                    return new CursorLoader(this,
                            mUri,
                            WEATHER_DETAIL_PROJECTION,
                            null,
                            null,
                            null);
                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }

        }


//          TODO (23) If the loader requested is our detail loader, return the appropriate CursorLoader

//  TODO (24) Override onLoadFinished

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

//      TODO (25) Check before doing anything that the Cursor has valid data
            boolean cursorHasValidData = false;
            if (data != null && data.moveToFirst()){
                cursorHasValidData = true;
            }

            if (!cursorHasValidData){
                return;
            }

            //      TODO (7) Display the weather icon using mDetailBinding
            /****************
             * Weather Icon *
             ****************/
            /* Read weather condition ID from the cursor (ID provided by Open Weather Map) */
            int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
            /* Use our utility method to determine the resource ID for the proper art */
            int weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

            /* Set the resource ID on the icon to display the art */
            mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

            /****************
             * Weather Date *
             ****************/
            /*
             * Read the date from the cursor. It is important to note that the date from the cursor
             * is the same date from the weather SQL table. The date that is stored is a GMT
             * representation at midnight of the date when the weather information was loaded for.
             *
             * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
             * the date representation for the local date in local time.
             * SunshineDateUtils#getFriendlyDateString takes care of this for us.
             */
//      TODO (26) Display a readable data string

            long localDateMidNightGmt = data.getLong(INDEX_WEATHER_DATE);
            String dateText = SunshineDateUtils.getFriendlyDateString(this, localDateMidNightGmt, true);

            //  TODO (8) Use mDetailBinding to display the date
            mDetailBinding.primaryInfo.date.setText(dateText);

            /***********************
             * Weather Description *
             ***********************/
            /* Use the weatherId to obtain the proper description */
            String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);

//      TODO (15) Create the content description for the description for a11y
            String descriptionA11y = getString(R.string.a11y_forecast, description);

//      TODO (9) Use mDetailBinding to display the description and set the content description
            mDetailBinding.primaryInfo.weatherDescription.setText(description);
            mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);

//      TODO (16) Set the content description of the icon to the same as the weather description a11y text
//      COMPLETED (16) Set the content description of the icon to the same as the weather description a11y text
            /* Set the content description on the weather image (for accessibility purposes) */
            mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);
//      TODO (28) Display the high temperature
            double highInCelcius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
            String highString = SunshineWeatherUtils.formatTemperature(this, highInCelcius);


//      COMPLETED (17) Create the content description for the high temperature for a11y
            /* Create the accessibility (a11y) String from the weather description */
            String highA11y = getString(R.string.a11y_high_temp, highString);
//      TODO (10) Use mDetailBinding to display the high temperature and set the content description
            /* Set the text and content description (for accessibility purposes) */
            mDetailBinding.primaryInfo.highTemperature.setText(highString);
            mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

            /*************************
             * Low (min) temperature *
             *************************/
//      TODO (29) Display the low temperature
            double lowInCelcius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
            String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelcius);


//      COMPLETED (18) Create the content description for the low temperature for a11y
            String lowA11y = getString(R.string.a11y_low_temp, lowString);
//      TODO (11) Use mDetailBinding to display the low temperature and set the content description
            /* Set the text and content description (for accessibility purposes) */
            mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
            mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

            /************
             * Humidity *
             ************/
//      TODO (30) Display the humidity
            float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
            String humidityString = getString(R.string.format_humidity, humidity);

//      COMPLETED (20) Create the content description for the humidity for a11y
            String humidityA11y = getString(R.string.a11y_humidity, humidityString);

//      TODO (12) Use mDetailBinding to display the humidity and set the content description
            mDetailBinding.extraDetails.humidity.setText(humidityString);
            mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

//      COMPLETED (19) Set the content description of the humidity label to the humidity a11y String
            mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);

            /****************************
             * Wind speed and direction *
             ****************************/
//      TODO (31) Display the wind speed and direction
            float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
            float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
            String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);

//      COMPLETED (21) Create the content description for the wind for a11y
            String windA11y = getString(R.string.a11y_wind, windString);


//      TODO (13) Use mDetailBinding to display the wind and set the content description
            mDetailBinding.extraDetails.windMeasurement.setText(windString);
            mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);

//      COMPLETED (22) Set the content description of the wind label to the wind a11y String
            mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);
//      TODO (32) Display the pressure
            float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
            String pressureString = getString(R.string.format_pressure, pressure);


//      COMPLETED (23) Create the content description for the pressure for a11y
            String pressureA11y = getString(R.string.a11y_pressure, pressureString);
//      TODO (14) Use mDetailBinding to display the pressure and set the content description
            /* Set the text and content description (for accessibility purposes) */
            mDetailBinding.extraDetails.pressure.setText(pressureString);
            mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);

//      COMPLETED (24) Set the content description of the pressure label to the pressure a11y String
            mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);


            //      TODO (33) Store a forecast summary in mForecastSummary
            mForecastSummary= String.format("%s - %s - %s/%s", dateText, description, highString, lowString);

        }
//  TODO (34) Override onLoaderReset, but don't do anything in it yet

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
            MenuInflater inflater = getMenuInflater();
            /* Use the inflater's inflate method to inflate our menu layout to this menu */
            inflater.inflate(R.menu.detail, menu);
            /* Return true so that the menu is displayed in the Toolbar */
            return true;
    }

    // TODO (7) Launch SettingsActivity when the Settings option is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int settingsId = item.getItemId();
        if (settingsId == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        int id = item.getItemId();
        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
