package com.example.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.sunshine.utilities.SunshineDateUtils;

public class WeatherContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.sunshine";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_WEATHER = "weather";

    //  TODO (1) Within WeatherContract, create a public static final class called WeatherEntry that implements BaseColumns
public static final class WeatherEntry implements BaseColumns {


        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();


        //      TODO (2) Create a public static final String call TABLE_NAME with the value "weather"
public static final String TABLE_NAME = "weather";
//      TODO (3) Create a public static final String call COLUMN_DATE with the value "date"
public static final String COLUMN_DATE = "date";
//      TODO (4) Create a public static final String call COLUMN_WEATHER_ID with the value "weather_id"
public static final String COLUMN_WEATHER_ID = "weather_id";
//      TODO (5) Create a public static final String call COLUMN_MIN_TEMP with the value "min"
public static final String COLUMN_MIN_TEMP = "min";
//      TODO (6) Create a public static final String call COLUMN_MAX_TEMP with the value "max"
public static final String COLUMN_MAX_TEMP = "max";
//      TODO (7) Create a public static final String call COLUMN_HUMIDITY with the value "humidity"
public static final String COLUMN_HUMIDITY = "humidity";
//      TODO (8) Create a public static final String call COLUMN_PRESSURE with the value "pressure"
public static final String COLUMN_PRESSURE = "pressure";
//      TODO (9) Create a public static final String call COLUMN_WIND_SPEED with the value "wind"
public static final String COLUMN_WIND_SPEED = "wind";
//      TODO (10) Create a public static final String call COLUMN_DEGREES with the value "degrees"
public static final String COLUMN_DEGREES = "degrees";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param date Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;

        }
    }
// Do steps 2 through 10 within the WeatherEntry class
}
