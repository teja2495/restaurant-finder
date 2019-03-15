package com.example.teja2.tripplanner;
/*
Created by
Bala Guna Teja Karlapudi
*/
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class TripsActivity extends AppCompatActivity implements tripsFragment.tripInfoInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new tripsFragment(), "tripsFragment")
                .commit();

    }

    @Override
    public void tripInfoInterface(trips trip) {
        tripInfoFragment obj = new tripInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", trip);
        obj.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, obj, "TripInfoFragment")
                .addToBackStack(null)
                .commit();
    }
}
