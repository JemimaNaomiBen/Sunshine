package com.example.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.sync.SunshineSyncUtils;

;

// TODO (1) Implement the proper LoaderCallbacks interface and the methods of that interface-asynctaskloader
// TODO (8) Implement ForecastAdapterOnClickHandler from the MainActivity
// TODO (3) Implement OnSharedPreferenceChangeListener on MainActivity
// TODO (15) Remove the implements declaration for SharedPreferences change listener and methods
//      TODO (20) Implement LoaderCallbacks<Cursor> instead of String[]
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, ForecastAdapter.ForecastAdapterOnClickHandler{
    private static final String TAG = MainActivity.class.getSimpleName();
    //  TODO (16) Create a String array containing the names of the desired data columns from our ContentProvider
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    //  TODO (17) Create constant int values representing each column name's position above
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private static final int ID_FORECAST_LOADER = 44;
    // TODO (33) Delete weatherDisplayTextView
    // TODO (34) Add a private RecyclerView variable called mRecyclerView
    // TODO (35) Add a private ForecastAdapter variable called mForecastAdapter
    private ForecastAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    //  TODO (37) Remove the error TextView

    // TODO (16) Add a ProgressBar variable to show and hide the progress bar-polish
    private ProgressBar displayProgressBar;

    //  TODO (35) Remove the preference change flag

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

// TODO (12) Remove the fake data creation since we can now sync with live data

        // TODO (36) Delete the line where you get a reference to mWeatherTextView
        // TODO (37) Use findViewById to get a reference to the RecyclerView
        mRecyclerView = findViewById(R.id.recyclerview_forecast);

        //TODO (36) Remove the findViewById call for the error TextView

        // TODO (38) Create layoutManager, a LinearLayoutManager with VERTICAL orientation and shouldReverseLayout == false
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // TODO (39) Set the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // TODO (40) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        mRecyclerView.setHasFixedSize(true);
        // TODO (11) Pass in 'this' as the ForecastAdapterOnClickHandler-clickhandling
        // TODO (41) set mForecastAdapter equal to a new ForecastAdapter
        mForecastAdapter = new ForecastAdapter(this,this);
        // TODO (42) Use mRecyclerView.setAdapter and pass in mForecastAdapter
        mRecyclerView.setAdapter(mForecastAdapter);

           // TODO (17) Find the ProgressBar using findViewById-polish
           displayProgressBar = (ProgressBar) findViewById(R.id.pb_display_progressbar);

        //      TODO (18) Call the showLoading method
        showLoading();


    /*
     * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
     * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
     * to the call to initLoader below. This means that whenever the loaderManager has
     * something to notify us of, it will do so through this callback.
     */
   // LoaderManager.LoaderCallbacks<Cursor> callback = MainActivity.this;

    /*
     * The second parameter of the initLoader method below is a Bundle. Optionally, you can
     * pass a Bundle to initLoader that you can then access from within the onCreateLoader
     * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
     * to.
     */
   // Bundle bundleForLoader = null;

    /*
     * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
     * created and (if the activity/fragment is currently started) starts the loader. Otherwise
     * the last created loader is re-used.
     */
        //TODO (19) Remove the statement that registers Mainactivity as a preference change listener
    getSupportLoaderManager().initLoader(ID_FORECAST_LOADER,null,this);

           //  TODO (13) Call SunshineSyncUtils's startImmediateSync method

           //  TODO (7) Call SunshineSyncUtils's initialize method instead of startImmediateSync
           SunshineSyncUtils.initialize(this);
       }

    // TODO (2) Launch the map when the map menu item is clicked-method
    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    // TODO (2) Within onCreateLoader, return a new AsyncTaskLoader that looks a lot like the existing FetchWeatherTask
    //  TODO (21) Refactor onCreateLoader to return a Loader<Cursor>, not Loader<String[]>
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, final Bundle bundle) {
        //TODO (22) If the loader requested is our forecast loader, return the appropriate CursorLoader
        switch (loaderId) {
            case ID_FORECAST_LOADER:
                Uri forecastQueUri = WeatherContract.WeatherEntry.CONTENT_URI;

                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }
            //TODO (23) Remove the onStartLoading method declaration
            //TODO (24) Remove the loadInBackground method declaration
           //   TODO (25) Remove the deliverResult method declaration
    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */

        // TODO (26) Change onLoadFinished parameter to a Loader<Cursor> instead of a Loader<String[]>
    @Override
    public void onLoadFinished(Loader<Cursor> loader,  Cursor data) {
        //      TODO (27) Remove the previous body of onLoadFinished
        //      TODO (28) Call mForecastAdapter's swapCursor method and pass in the new Cursor
        mForecastAdapter.swapCursor(data);
        //      TODO (29) If mPosition equals RecyclerView.NO_POSITION, set it to 0
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        //      TODO (30) Smooth scroll the RecyclerView to mPosition
        mRecyclerView.smoothScrollToPosition(mPosition);
        //      TODO (31) If the Cursor's size is not equal to 0, call showWeatherDataView
        if (data.getCount() != 0) showWeatherDataView();
    }
    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO (32) Call mForecastAdapter's swapCursor method and pass in null
        mForecastAdapter.swapCursor(null);

        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }
    // TODO (9) Override ForecastAdapterOnClickHandler's onClick method-recyclerviewclickhandling
    // TODO (10) Show a Toast when an item is clicked, displaying that item's weather data

    //  TODO (38) Refactor onClick to accept a long instead of a String as its parameter
    @Override
    public void onClick(long date) {
        // TODO (3) Remove the Toast and launch the DetailActivity using an explicit Intent
        //Toast.makeText(this,weatherForDay,Toast.LENGTH_SHORT).show();
        Intent detailsIntent = new Intent(this,DetailActivity.class);
        // TODO (1) Pass the weather to the DetailActivity-displayforecast
        //      TODO (39) Refactor onClick to build a URI for the clicked date and and pass it with the Intent using setData
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        detailsIntent.setData(uriForDateClicked);
        startActivity(detailsIntent);


    }
    // TODO (8) Create a method called showWeatherDataView that will hide the error message and show the weather data-polish
    private void showWeatherDataView(){
        displayProgressBar.setVisibility(View.INVISIBLE);
        // TODO (43) Show mRecyclerView, not mWeatherTextView
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    //  TODO (33) Delete showErrorMessage
    //  TODO (34) Create a method called showLoading that shows the loading indicator and hides the data
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);

        displayProgressBar.setVisibility(View.VISIBLE);
    }


    // COMPLETED (5) Override onCreateOptionsMenu to inflate the menu for this Activity
    // COMPLETED (6) Return true to display the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    // COMPLETED (7) Override onOptionsItemSelected to handle clicks on the refresh button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO (2) Launch the map when the map menu item is clicked
        int mapId = item.getItemId();
        if (mapId == R.id.action_map){
            openPreferredLocationInMap();
            return true;

        }
// TODO (6) Launch SettingsActivity when the Settings option is clicked
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
