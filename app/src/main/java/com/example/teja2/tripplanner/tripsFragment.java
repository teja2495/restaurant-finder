package com.example.teja2.tripplanner;
/*
Created by
Bala Guna Teja Karlapudi
*/
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class tripsFragment extends Fragment {

    ImageView logout;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    TextView username;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    ListView lv;
    Button create;
    DatabaseReference mRootRef;
    List<trips> tripsList=new ArrayList<>();
    CustomAdapter customAdapter;
    int mPosition;
    ImageView dp;

    private tripInfoInterface mListener;

    public tripsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (tripInfoInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_trips, container, false);
        mRootRef = FirebaseDatabase.getInstance().getReference().child("trips");

        logout=view.findViewById(R.id.logout);
        username=view.findViewById(R.id.username);
        firebaseUser=mAuth.getCurrentUser();
        username.setText(firebaseUser.getDisplayName());
        lv=view.findViewById(R.id.tripsView);
        create=view.findViewById(R.id.createButton);
        dp=view.findViewById(R.id.profile_image);

        Picasso.get().load(firebaseUser.getPhotoUrl()).into(dp);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new newTripFragment(), "newTripFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tripsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    trips trips = new trips();
                    trips.setTripName(postSnapshot.child("tripName").getValue().toString());
                    trips.setTripDate(postSnapshot.child("tripDate").getValue().toString());
                    trips.setUsername(postSnapshot.child("username").getValue().toString());
                    trips.setTripID(postSnapshot.child("tripID").getValue().toString());
                    trips.setUserID(postSnapshot.child("userID").getValue().toString());
                    trips.setTripCity(postSnapshot.child("tripCity").getValue().toString());
                    List<places> placesList;
                    GenericTypeIndicator<List<places>> t = new GenericTypeIndicator<List<places>>() {};
                    placesList=postSnapshot.child("places").getValue(t);
                    trips.setPlaces(placesList);
                    tripsList.add(trips);
                }
                customAdapter = new CustomAdapter(tripsList);
                lv.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.tripInfoInterface(tripsList.get(position));
            }
        });


        return view;
    }

    class CustomAdapter extends BaseAdapter {

        List<trips> list;

        public CustomAdapter(List<trips> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_layout, null);
            final ImageView delete=view.findViewById(R.id.deleteTrip);
            TextView tripName = view.findViewById(R.id.tripName);
            TextView tripDate = view.findViewById(R.id.tripDate);
            TextView createdBy = view.findViewById(R.id.createdBy);
            tripName.setText(list.get(position).getTripName());
            tripDate.setText("Trip Date: "+list.get(position).getTripDate());
            createdBy.setText(list.get(position).getUsername());

            delete.setVisibility(View.GONE);
            if(firebaseUser.getUid().equals(list.get(position).getUserID()))
                delete.setVisibility(View.VISIBLE);
            delete.setTag(position);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = (Integer) delete.getTag();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle("Delete Trip")
                            .setMessage("Are you sure you want to delete this trip?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mRootRef.child(list.get(mPosition).getTripID()).removeValue();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            return view;
        }
    }

    public interface tripInfoInterface {
        void tripInfoInterface(trips trip);
    }

}

