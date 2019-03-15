package com.example.teja2.tripplanner;

/*
Created by
Bala Guna Teja Karlapudi
*/
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class newTripFragment extends Fragment {

    String keyString = "AIzaSyDTxJkR-0s-0kqIDm4IZIiN5ZtcJL6Uzbc";
    String placesSearchStr = null;
    List<places> placesList=new ArrayList<>();
    List<places> selectedPlacesList=new ArrayList<>();
    List<String> placeNamesList=new ArrayList<>();
    List<String> selectedPlaceNamesList=new ArrayList<>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    EditText tName, tDate, tCity, tPlaces;
    TextView sPlacesText;
    Button cancel, save;
    Place city;
    ArrayAdapter<String> adapter;
    ListView lv;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar = Calendar.getInstance();
    Date currentDate=new Date();
    Date mdate=new Date();
    DatabaseReference mRootRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    ProgressBar pb;


    public newTripFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_trip, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("trips");
        firebaseUser=mAuth.getCurrentUser();

        tName=view.findViewById(R.id.tName);
        tDate=view.findViewById(R.id.tDate);
        tCity=view.findViewById(R.id.tCity);
        tPlaces=view.findViewById(R.id.tPlaces);
        cancel=view.findViewById(R.id.cancelButton);
        save=view.findViewById(R.id.saveButton);
        lv=view.findViewById(R.id.selectedPlaces);
        sPlacesText=view.findViewById(R.id.sPlaces);
        pb=view.findViewById(R.id.progressBar);
        sPlacesText.setVisibility(View.INVISIBLE);
        tDate.setKeyListener(null);
        tPlaces.setKeyListener(null);
        tCity.setKeyListener(null);
        tPlaces.setEnabled(false);
        pb.setVisibility(View.INVISIBLE);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tDate.getText().toString().isEmpty()){
                    currentDate=Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                    try {
                        mdate = sdf.parse(tDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(tName.getText().toString().trim().isEmpty()
                        || tDate.getText().toString().isEmpty()
                        || tCity.getText().toString().isEmpty()
                        || selectedPlacesList.size()==0)
                    Toast.makeText(getActivity(),"Missing Values", Toast.LENGTH_LONG).show();
                else if(selectedPlacesList.size()>15)
                    Toast.makeText(getActivity(),"Number of selected places should be 15 or below", Toast.LENGTH_LONG).show();
                else if(currentDate.after(mdate)){
                    Toast.makeText(getActivity(),"Trip Date should be at least one day Greater than today", Toast.LENGTH_LONG).show();
                }
                else{
                    trips trips =new trips();
                    trips.setTripName(tName.getText().toString());
                    trips.setTripDate(tDate.getText().toString());
                    trips.setTripCity(city.getName().toString());
                    trips.setPlaces(selectedPlacesList);
                    trips.setTripID(mRootRef.push().getKey());
                    trips.setUserID(firebaseUser.getUid());
                    trips.setUsername(firebaseUser.getDisplayName());
                    mRootRef.child(trips.getTripID()).setValue(trips);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        tCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setCountry("US")
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                        .build();

                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(getActivity());

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });

        tPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    String JSON_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
                    placesSearchStr = JSON_BASE + city.getLatLng().latitude+ ","+city.getLatLng().longitude+
                                "&radius=1609.34&types=restaurant&key=" + keyString;
                    pb.setVisibility(View.VISIBLE);
                    new HttpGetAsync().execute(placesSearchStr);
                }
                else
                    Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();

            }
        });

        tDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                tDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        return view;
    }

    class HttpGetAsync extends AsyncTask< String, Void, List<places>> {
        @Override
        protected List <places> doInBackground(String...strings) {

            try {
                placeNamesList.clear();
                placesList.clear();
                String json = getAPIData(strings[0]);
                JSONObject root = new JSONObject(json);
                JSONArray JSONresults = root.getJSONArray("results");

                for (int i = 0; i < JSONresults.length(); i++) {
                    places places=new places();
                    JSONObject placeJSON = JSONresults.getJSONObject(i);
                    places.setName(placeJSON.getString("name"));
                    JSONObject geometry = placeJSON.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    places.setLatitude(location.getString("lat"));
                    places.setLongitude(location.getString("lng"));
                    placeNamesList.add(places.getName());
                    placesList.add(places);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", placesList.toString());
            return placesList;
        }

        @Override
        protected void onPostExecute(final List < places > placeList) {
            super.onPostExecute(placeList);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Select the Places");
            final boolean[] checkedItems = new boolean[placeList.size()];


            String[] placeNames = new String[placeList.size()];
            placeNames = placeNamesList.toArray(placeNames);

            for (int i = 0; i < placeNamesList.size(); i++) {
                if (selectedPlaceNamesList.contains(placeNamesList.get(i)))
                    checkedItems[i] = true;
            }

            dialogBuilder.setMultiChoiceItems(placeNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                }
            });

            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // user clicked OK
                    selectedPlaceNamesList.clear();
                    selectedPlacesList.clear();
                    for (int i = 0; i < checkedItems.length; i++) {
                        boolean checked = checkedItems[i];
                        if (checked) {
                            selectedPlaceNamesList.add(placeNamesList.get(i));
                            selectedPlacesList.add(placeList.get(i));
                        }
                    }
                    if (selectedPlaceNamesList != null) {
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selectedPlaceNamesList);
                        lv.setAdapter(adapter);
                        sPlacesText.setVisibility(View.VISIBLE);
                    }
                    tPlaces.setHint(selectedPlacesList.size()+" Places Selected. Tap here to Add/Remove Places");
                }
            });
            dialogBuilder.setNegativeButton("Cancel", null);
            AlertDialog dialog = dialogBuilder.create();
            pb.setVisibility(View.INVISIBLE);
            dialog.show();
        }
    }
    String getAPIData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                city = PlaceAutocomplete.getPlace(getActivity(), data);
                tCity.setText(city.getName().toString());
                tPlaces.setEnabled(true);
                //Log.d("demo", "Place: " + city.getLatLng().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
                        networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

}
