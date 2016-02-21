package com.example.dhudupires.inlocoweathermap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class WeatherDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        TextView mCityTitle;
        TextView mCityMinTemp;
        TextView mCityMaxTemp;
        TextView mCityWeatherDesc;

        String mName = "";
        String mMinTemp = "";
        String mMaxTemp = "";
        String mWeatherDesc = "";
        JSONObject mJsonResponseObject = null;

        Intent intent = getIntent();

        try {
            mJsonResponseObject = new JSONObject(intent.getStringExtra(CityListActivity.JSON_RESPONSE_OBJECT));

            if(mJsonResponseObject != null){ //check if the object is null, in case of application stays for a long time in background and the so kill the proccess
                //get the information in the json
                mName = mJsonResponseObject.getString("name");
                mMinTemp = mJsonResponseObject.getJSONObject("main").getString("temp_min");
                mMaxTemp = mJsonResponseObject.getJSONObject("main").getString("temp_max");
                mWeatherDesc = mJsonResponseObject.getJSONArray("weather").getJSONObject(0).getString("description");
            }
            else{
                Toast.makeText(WeatherDetailActivity.this, "Error, try again.",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCityTitle = (TextView) findViewById(R.id.city_name);
        mCityMinTemp = (TextView) findViewById(R.id.city_min_temp);
        mCityMaxTemp = (TextView) findViewById(R.id.city_max_temp);
        mCityWeatherDesc = (TextView) findViewById(R.id.city_weather_desc);

        //display the information
        mCityTitle.setText(mName);
        if(mJsonResponseObject != null){//check if the object is null, in case of application stays for a long time in background and the so kill the proccess
            mCityMinTemp.setText("Minimum temperature: "+ String.format("%.2f", kelvinToCelcius(mMinTemp)) + " °C");
            mCityMaxTemp.setText("Maximum temperature: "+ String.format("%.2f", kelvinToCelcius(mMaxTemp)) + " °C");
        }
        mCityWeatherDesc.setText("Weather description: "+ mWeatherDesc);

    }

    public double kelvinToCelcius(String kelvinTemp){ //convert a kelvin string to a celcius double
        double kelvin = Double.parseDouble(kelvinTemp);
        double celcius = kelvin - 273.15;
        return celcius;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
