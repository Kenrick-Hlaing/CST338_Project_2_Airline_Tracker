package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.FlightDAO;
import com.example.project2.databinding.ActivityAddFlightBinding;

import java.util.List;

public class AddFlightActivity extends AppCompatActivity {

    private static final String ADD_FLIGHT_ACTIVITY_USER = "com.example.project2.AddFlightActivityUser";

    TextView mAddFlightBanner;
    EditText mAddFlightEnterFlightNumber;
    EditText mAddFlightEnterFlightOrigin;
    EditText mAddFlightEnterFlightDestination;
    TextView mAddFlightMessageDisplay;

    Button mAddFlightAddButton;
    Button mAddFlightBackButton;

    FlightDAO mAddFlightDAO;



    ActivityAddFlightBinding mAddFlightBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);

        mAddFlightBinding = ActivityAddFlightBinding.inflate(getLayoutInflater());
        setContentView(mAddFlightBinding.getRoot());

        // Wiring up all layout stuff
        mAddFlightBanner = mAddFlightBinding.addFlightBanner;
        mAddFlightEnterFlightNumber = mAddFlightBinding.addFlightEnterFlightNumber;
        mAddFlightEnterFlightOrigin = mAddFlightBinding.addFlightEnterFlightOrigin;
        mAddFlightEnterFlightDestination = mAddFlightBinding.addFlightEnterFlightDestination;
        mAddFlightMessageDisplay = mAddFlightBinding.addFlightMessageDisplay;
        mAddFlightAddButton = mAddFlightBinding.addFlightAddButton;
        mAddFlightBackButton = mAddFlightBinding.addFlightBackButton;

        mAddFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        // Extra from the intent
        String currentUsername = getIntent().getStringExtra(ADD_FLIGHT_ACTIVITY_USER);

        // Add onClickListener for add flight
        mAddFlightAddButton.setOnClickListener(view -> {
            // Add a function for adding a flight
            if(addFlight()){
                Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });

        // Add onClickListener for back button
        mAddFlightBackButton.setOnClickListener(view -> {
            Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });
    }

    public boolean addFlight(){
        String flightNumber = mAddFlightEnterFlightNumber.getText().toString();
        // Check if flight number is entered
        if(flightNumber.equals("")){
            mAddFlightMessageDisplay.setText(R.string.add_flight_error_no_flight_number);
            mAddFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        // Check if flight number is unique
        List<Flight> mFlightList = mAddFlightDAO.getFlights();
        for(Flight flight : mFlightList){
            // If there is a flight number that matches the entered flight number
            // Display error
            if(flightNumber.equals(flight.getFlightNumber())){
                mAddFlightMessageDisplay.setText(R.string.add_flight_error_not_unique);
                mAddFlightMessageDisplay.setVisibility(View.VISIBLE);
                return false;
            }
        }

        // Check if flight number is correct length
        if(flightNumber.length() > 6){
            mAddFlightMessageDisplay.setText(R.string.add_flight_error_flight_number_length);
            mAddFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        String origin = mAddFlightEnterFlightOrigin.getText().toString();
        // Check if Origin is entered
        if(origin.equals("")){
            mAddFlightMessageDisplay.setText(R.string.add_flight_error_no_origin);
            mAddFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        String destination = mAddFlightEnterFlightDestination.getText().toString();
        // Check if Destination is entered
        if(destination.equals("")){
            mAddFlightMessageDisplay.setText(R.string.add_flight_error_no_destination);
            mAddFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        // At this point all entered details should be valid
        // So insert all details into the flight table
        Flight newFlight = new Flight(flightNumber, origin, destination);
        mAddFlightDAO.insert(newFlight);
        return true;
    }


    public static Intent getIntent(Context context, String username){
        Intent intent = new Intent(context, AddFlightActivity.class);
        intent.putExtra(ADD_FLIGHT_ACTIVITY_USER, username);
        return intent;
    }
}