package com.example.dhudupires.inlocoweathermap;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CityListActivity extends ActionBarActivity {

    ProgressBar mProgressBar;
    ListView mCityList;
    public final static String JSON_RESPONSE_OBJECT = "com.example.dhudupires.inlocoweathermap.JSONRESPONSEOBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        Bundle bundle;
        final LatLng mChoosenLocation;
        final String[] mCitiesName = new String[15];
        final JSONCapsule jsonCapsule = new JSONCapsule();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.MULTIPLY);
        //run a progress bar to tell the user that some data is being loaded
        bundle = getIntent().getParcelableExtra(MapsActivity.BUNDLE);
        mChoosenLocation = bundle.getParcelable(MapsActivity.CHOOSEN_LATLNG); //receive the choosen location from the other acvity

        mCityList = (ListView) findViewById(R.id.city_list_view);
        mCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) { //this method is executed when the user tap a city in the list

                Intent intent = new Intent(getApplicationContext(), WeatherDetailActivity.class);
                try {
                    /*
                    pass to the other activity only the json string related to the choosen city
                    it avoids running the json with all cities
                     */
                    intent.putExtra(JSON_RESPONSE_OBJECT, jsonCapsule.jsonArray.getJSONObject(position).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });

        mCityList.setVisibility(View.GONE);



        new AsyncTask<Void, Void, Void>(){ //creating an AsyncTask, because is not allowed to make internet procedures in the main thread

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    if (mChoosenLocation != null) {//check if the object is null, in case of application stays for a long time in background and the so kill the proccess
                        jsonCapsule.jsonObject = getJSON("http://api.openweathermap.org/data/2.5/find?lat=" + mChoosenLocation.latitude + "&lon=" + mChoosenLocation.longitude + "&cnt=15&APPID=d5e5e7bf0036493556227d17d41219bd", 1000000000);
                        //load the json response from the Google Open Weather API, with the choosen latitude and longitude as parameters
                    }
                    else{
                        Toast.makeText(CityListActivity.this, "Error, try again.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(CityListActivity.this, "Error, try again.",
                            Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v){

                try {
                    JSONObject jsonObject = jsonCapsule.jsonObject;
                    if(jsonObject != null){
                        jsonCapsule.jsonArray = jsonObject.getJSONArray("list"); //create an json array with the array of list tag
                        String name;

                        for(int i=0;i< jsonCapsule.jsonArray.length();i++) { //for each object in this array, get it's name.
                            name = jsonCapsule.jsonArray.getJSONObject(i).getString("name");
                            mCitiesName[i] = name;
                            showList(mCitiesName);
                        }
                    }
                    else{
                        Toast.makeText(CityListActivity.this, "Error, try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CityListActivity.this, "Error, try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();


    }

    protected void showList(String[] citiesName){

        //show the list with all 15 nearest cities names, and make the progress circle disappear
        mProgressBar.setVisibility(View.GONE);
        ArrayAdapter<String> itensAdapter;
        itensAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, citiesName) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        mCityList.setAdapter(itensAdapter);
        mCityList.setVisibility(View.VISIBLE);
    }


    public JSONObject getJSON(String url, int timeout) { //this method is designed to receive the url and get the response.
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();

            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb2 = new StringBuilder();
                    String line2;
                    while ((line2 = br2.readLine()) != null) {
                        sb2.append(line2+"\n");
                    }
                    br2.close();
                    JSONObject jsonObjectReturn = null;
                    try {
                        jsonObjectReturn = new JSONObject(sb2.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return jsonObjectReturn;
                default:
                    Toast.makeText(CityListActivity.this, "Error, try again.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
