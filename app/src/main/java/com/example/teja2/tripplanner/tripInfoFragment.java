package com.example.teja2.tripplanner;
/*
Created by
Bala Guna Teja Karlapudi
*/
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class tripInfoFragment extends Fragment {

    TextView tName, tDate, tCity;
    ListView lv;
    ArrayAdapter<String> adapter;
    List<String> selectedPlaceNamesList=new ArrayList<>();
    trips trip =new trips();
    Button showMap;

    public tripInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_info, container, false);

        if (getArguments() != null) {
            trip = (trips)getArguments().getSerializable("trip");
        }
        tName=view.findViewById(R.id.tripName);
        tDate=view.findViewById(R.id.tripDate);
        tCity=view.findViewById(R.id.tripCity);
        lv=view.findViewById(R.id.displayPlaces);
        showMap=view.findViewById(R.id.mapButton);

        for(int i=0;i<trip.getPlaces().size();i++)
            selectedPlaceNamesList.add(trip.getPlaces().get(i).getName());
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selectedPlaceNamesList);

        lv.setAdapter(adapter);
        tName.setText(trip.getTripName());
        tDate.setText(trip.getTripDate());
        tCity.setText(trip.getTripCity());

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MapsActivity.class);
                intent.putExtra( "placesList", (Serializable) trip.getPlaces());
                startActivity(intent);
            }
        });

        return view;
    }

}
