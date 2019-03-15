package com.example.teja2.tripplanner;

/*
Created by
Bala Guna Teja Karlapudi
*/

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<places> placesList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placesList = (List<places>) getIntent().getSerializableExtra("placesList");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for(int i=0;i<placesList.size();i++){
            LatLng latLng = new LatLng(Double.parseDouble(placesList.get(i).getLatitude()),Double.parseDouble(placesList.get(i).getLongitude()));
            latLngBuilder.include(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(placesList.get(i).getName()));
        }
        LatLngBounds LatLngBuilder = latLngBuilder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(LatLngBuilder, 150);

        this.mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cameraUpdate);
            }
        });
    }
}
