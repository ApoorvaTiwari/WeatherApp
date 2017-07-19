package com.example.weatherapoorva.weatherapp;

import android.app.Activity;
import android.content.SharedPreferences;


/**
 * Created by Apoorva on 16-07-2017.
 */

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
       }

       String getCity() {
           return prefs.getString("city", "Hyderabad, IN");
       }

       void setCity(String city) {
           prefs.edit().putString("city", city).commit();
       }
}
