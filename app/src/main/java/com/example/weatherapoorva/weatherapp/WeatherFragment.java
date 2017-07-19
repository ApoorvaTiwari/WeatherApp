package com.example.weatherapoorva.weatherapp;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    Typeface weatherFont;

    TextView cityField;
    TextView updateField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;
    private long sunset;


    public WeatherFragment() {
        handler = new Handler();
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        UpdateWeatherData(new CityPreference(getActivity()).getCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updateField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        return rootView;

    }

    private void UpdateWeatherData(final String city){
        new Thread(){
            @Override
            public void run() {
                JSONObject json = null;
                try {
                    json = RemoteFetch.getJSON(getActivity(), city);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    final JSONObject finalJson = json;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(finalJson);
                        }
                    });

                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
            ", " + json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").optJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            detailsField.setText(details.getString("description").toUpperCase(Locale.US) + "\n"
            + "Humidity: " + main.getString("humidity") + "%" + "Pressure: "
            + main.getString("pressure") + "hPa");

            currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + " °C");

            DateFormat df = DateFormat.getDateInstance();
            String updateOn = df.format(new Date(json.getLong("dt") + 1000));
            updateField.setText("Last update: " + updateOn);

            setWeatherIcon(details.getInt("id"), json.getJSONObject("sys").getLong("sunset") * 1000);
        } catch (Exception e) {
    }

}

    private void setWeatherIcon(int actualId, long sunrise) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city) {
        UpdateWeatherData(city);

}
}
