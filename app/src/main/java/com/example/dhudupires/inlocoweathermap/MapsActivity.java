package com.example.dhudupires.inlocoweathermap;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private Animation mfadeInAnimation;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng mChoosenLocation;
    public final static String CHOOSEN_LATLNG = "com.example.dhudupires.inlocoweathermap.CHOOSENLATLNG";
    public final static String BUNDLE = "com.example.dhudupires.inlocoweathermap.BUNDLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        final Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setVisibility(View.GONE);
        mfadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_effect);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) { //this method is executed when the user clicks the map

                mMap.clear(); //clear the map to avoid displaying multiple pins
                mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); //create a pin in the clicked position

                float zoom = mMap.getCameraPosition().zoom; //get the current camera zoom in the map

                if (zoom < 4.0f) { //if the zoom is too low, display a zoom in animation, with the point in map center
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 4.0f));
                } else { //display the point in map center, without changing the zoom
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
                }

                if (searchButton.getVisibility() == View.GONE) {
                    //make a search button visible when the user click the map for the first time
                    searchButton.setVisibility(View.VISIBLE);
                    searchButton.startAnimation(mfadeInAnimation);
                }

                mChoosenLocation = point; //get the choosen location
            }

        });
    }

    public void searchClick(View v){

        Intent intent = new Intent(MapsActivity.this, CityListActivity.class);
        Bundle args = new Bundle();
        args.putParcelable(CHOOSEN_LATLNG, mChoosenLocation);
        intent.putExtra(BUNDLE, args); //pass the choosen location to the other activity
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        }
    }
}
