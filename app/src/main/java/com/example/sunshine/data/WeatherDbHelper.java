package com.example.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO (11) Extend SQLiteOpenHelper from WeatherDbHelper
public class WeatherDbHelper extends SQLiteOpenHelper {

//  TODO (12) Create a public static final String called DATABASE_NAME with value "weather.db"
public static final String DATABASE_NAME = "weather.db";
    //  TODO (2) Increment the database version after altering the behavior of the table
    // TODO (2) Increment the database version after changing the create table statement
//  TODO (13) Create a private static final int called DATABASE_VERSION and set it to 1
private static final int DATABASE_VERSION = 3;
//  TODO (14) Create a constructor that accepts a context and call through to the superclass constructor
public WeatherDbHelper (Context context){super(context,DATABASE_NAME,null,DATABASE_VERSION);
}
//  TODO (15) Override onCreate and create the weather table from within it
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +

                        /*
                         * WeatherEntry did not explicitly declare a column called "_ID". However,
                         * WeatherEntry implements the interface, "BaseColumns", which does have a field
                         * named "_ID". We use that here to designate our table's primary key.
                         */
                        WeatherContract.WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherContract.WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL,"                  +

                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, "                    +

                        /*
                         * To ensure this table can only contain one weather entry per date, we declare
                         * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                         * SQLite that if we have a weather entry for a certain date and we attempt to
                         * insert another weather entry with that date, we replace the old weather entry.
                         */
                        " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }
//  TODO (16) Override onUpgrade, but don't do anything within it yet

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO (3) Within onUpgrade, drop the weather table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        // TODO (4) call onCreate and pass in the SQLiteDatabase (passed in to onUpgrade)
        onCreate(sqLiteDatabase);
    }
}
