package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.FlightDAO;
import com.example.project2.databinding.ActivityAdminActionBinding;

import java.util.List;

public class AdminActionActivity extends AppCompatActivity {

    private static final String ADMIN_ACTION_ACTIVITY_USER = "com.example.project2.AdminActionActivityUser";

    TextView mAdminActionBanner;
    TextView mAdminActionMessageDisplay;
    TextView mAdminActionFlightListTitle;
    TextView mAdminActionFlightListDisplay;

    EditText mAdminActionEditFlight;

    Button mAdminActionAddButton;
    Button mAdminActionEditButton;
    Button mAdminActionBackButton;

    FlightDAO mLandingFlightDAO;


    ActivityAdminActionBinding mAdminActionBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_action);

        mAdminActionBinding = ActivityAdminActionBinding.inflate(getLayoutInflater());
        setContentView(mAdminActionBinding.getRoot());

        // Wiring up all layout stuff
        mAdminActionBanner = mAdminActionBinding.adminActionBanner;
        mAdminActionMessageDisplay = mAdminActionBinding.adminActionMessageDisplay;
        mAdminActionFlightListTitle = mAdminActionBinding.adminActionFlightListTitle;
        mAdminActionFlightListDisplay = mAdminActionBinding.adminActionFlightListDisplay;
        mAdminActionFlightListDisplay.setMovementMethod(new ScrollingMovementMethod());

        mAdminActionEditFlight = mAdminActionBinding.adminActionEditFlight;

        mAdminActionAddButton = mAdminActionBinding.adminActionAddButton;
        mAdminActionEditButton = mAdminActionBinding.adminActionEditButton;
        mAdminActionBackButton = mAdminActionBinding.adminActionBackButton;

        mLandingFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        // Add all the flights to mAdminActionFlightListDisplay
        List<Flight> mFlightList = mLandingFlightDAO.getFlights();
        if(!mFlightList.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for(Flight flight : mFlightList){
                sb.append(flight.toString());
            }
            mAdminActionFlightListDisplay.setText(sb.toString());
        } else {
            mAdminActionFlightListDisplay.setText(R.string.add_flight);
        }

        // Extra from the intent
        String currentUsername = getIntent().getStringExtra(ADMIN_ACTION_ACTIVITY_USER);
        // Add onClickListener for Back Button
        mAdminActionBackButton.setOnClickListener(view -> {
            Intent intent = LandingActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });

        // Add onClickListener for Edit Flight Button
        mAdminActionEditButton.setOnClickListener(view -> {
            // Make a function for Editing a Flight, pass in the string in mAdminActionEditFlight
            String flightNumber = mAdminActionEditFlight.getText().toString();
            if(editFlight(flightNumber)){
                // pass the currentUsername into an extra
                // pass the flight id or the flight number into an extra for an intent
                // Go to the edit flight page
                Intent intent = EditFlightActivity.getIntent(getApplicationContext(), currentUsername, flightNumber);
                startActivity(intent);
            }
        });

        // Add onClickListener for Add Flight Button
        mAdminActionAddButton.setOnClickListener(view -> {
            Intent intent = AddFlightActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });
    }

    // A method for editing a flight
    public boolean editFlight(String flightNumber){
        // Grab list of flights from FlightDAO
        List<Flight> mFlightList = mLandingFlightDAO.getFlights();
        // See if flightNumber matches any Flights
        for(Flight flight : mFlightList){
            if(flightNumber.equals(flight.getFlightNumber())){
                // Matching flight number is found
                return true;
            }
        }
        // At this point, no flight number matches the entered flight Number
        // Show an Error
        mAdminActionMessageDisplay.setText(R.string.edit_flight_number_error);
        mAdminActionMessageDisplay.setVisibility(View.VISIBLE);
        return false;
    }


    public static Intent getIntent(Context context, String username){
        Intent intent = new Intent(context, AdminActionActivity.class);
        intent.putExtra(ADMIN_ACTION_ACTIVITY_USER, username);
        return intent;
    }
}