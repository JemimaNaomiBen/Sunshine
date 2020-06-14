package com.example.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.NotificationUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

//  TODO (1) Create a class called SunshineSyncTask
public class SunshineSyncTask {

    //  TODO (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//      TODO (3) Within syncWeather, fetch new weather data
//      TODO (4) If we have valid results, delete the old data and insert the new
   synchronized public static void syncWeather(Context context){

       try {
           URL weatherRequestUrl = NetworkUtils.getUrl(context);

           String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

           ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonWeatherResponse);

           if (weatherValues != null && weatherValues.length != 0){
               ContentResolver sunshineContentResolver = context.getContentResolver();

               sunshineContentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);

               sunshineContentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);

               //              TODO (13) Check if notifications are enabled
               boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);

               long timeSinceLastNotification = SunshinePreferences.getEllapsedTimeSinceLastNotification(context);

               boolean oneDayPassedSinceLastNotification = false;
//              TODO (14) Check if a day has passed since the last notification
               if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS){
                   oneDayPassedSinceLastNotification = true;
               }
//              TODO (15) If more than a day have passed and notifications are enabled, notify the user
               if (notificationsEnabled && oneDayPassedSinceLastNotification){
                   NotificationUtils.notifyUserOfNewWeather(context);
               }
               /* If the code reaches this point, we have successfully performed our sync */
           }
       }catch (Exception e){
           e.printStackTrace();
       }

   }

}