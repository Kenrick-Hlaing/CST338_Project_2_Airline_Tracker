package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.BookingDAO;
import com.example.project2.DB.FlightDAO;
import com.example.project2.databinding.ActivityEditFlightBinding;


import java.util.ArrayList;
import java.util.List;

public class EditFlightActivity extends AppCompatActivity {

    private static final String EDIT_FLIGHT_ACTIVITY_USER = "com.example.project2.EditFlightActivityUser";
    private static final String EDIT_FLIGHT_ACTIVITY_FLIGHT = "com.example.project2.EditFlightActivityFlight";

    TextView mEditFlightBanner;
    TextView mEditFlightBookingTitle;
    ListView mEditFlightBookingList;

    RadioButton mNotAvailableRadioButton;
    RadioButton mIsAvailableRadioButton;

    TextView mEditFlightMessageDisplay;

    Button mEditFlightEditButton;
    Button mEditFlightRemoveButton;
    Button mEditFlightBackButton;

    Flight mCurrentFlight;

    FlightDAO mEditFlightDAO;
    BookingDAO mEditBookingDAO;

    List<Booking> mBookingList;
    List<Flight> mFlightList;

    ActivityEditFlightBinding mEditFlightBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);

        mEditFlightBinding = ActivityEditFlightBinding.inflate(getLayoutInflater());
        setContentView(mEditFlightBinding.getRoot());

        mEditFlightBanner = mEditFlightBinding.editFlightBanner;
        mEditFlightBookingTitle = mEditFlightBinding.editFlightBookingTitle;
        mEditFlightBookingList = mEditFlightBinding.editFlightBookingList;
        mNotAvailableRadioButton = mEditFlightBinding.notAvailableRadioButton;
        mIsAvailableRadioButton = mEditFlightBinding.isAvailableRadioButton;
        mEditFlightMessageDisplay = mEditFlightBinding.editFlightMessageDisplay;
        mEditFlightEditButton = mEditFlightBinding.editFlightEditButton;
        mEditFlightRemoveButton = mEditFlightBinding.editFlightRemoveButton;
        mEditFlightBackButton = mEditFlightBinding.editFlightBackButton;

        mEditFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        mEditBookingDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BookingDAO();

        // Extra from the intent
        String currentUsername = getIntent().getStringExtra(EDIT_FLIGHT_ACTIVITY_USER);
        String currentFlightNumber = getIntent().getStringExtra(EDIT_FLIGHT_ACTIVITY_FLIGHT);

        // Make the Title of the Booking List using the currentFlightNumber
        StringBuilder stb = new StringBuilder();
        stb.append("Bookings For Flight ");
        stb.append(currentFlightNumber);
        mEditFlightBookingTitle.setText(stb.toString());


        // refresh the display for the Booking list
        refreshDisplay(currentFlightNumber);

        // Add onClickListener for back button
        mEditFlightBackButton.setOnClickListener(view -> {
            Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });


        mEditFlightRemoveButton.setOnClickListener(view -> {
            // Make a function for removing flight;
            removeFlight(currentFlightNumber);
            Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });


        // Add onClickListener for edit flight button
        // Check which radio button was checked
        mEditFlightEditButton.setOnClickListener(view -> {
            // Make function for editing flight
            if(editFlight(currentFlightNumber)){
                Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });
    }

    public void refreshDisplay(String currentFlightNumber){
        mBookingList = mEditBookingDAO.getBookings();
        // Find the mCurrentFlight
        mFlightList = mEditFlightDAO.getFlights();
        for(Flight flight : mFlightList){
            if(currentFlightNumber.equals(flight.getFlightNumber())){
                mCurrentFlight = flight;
                break;
            }
        }
        ArrayList<String> list = new ArrayList<>();
        for(Booking booking : mBookingList){
            StringBuilder stb = new StringBuilder();
            if(booking.getFlightId() == mCurrentFlight.getFlightId()){
                stb.append("BookingId: ").append(booking.getBookingId()).append("\n")
                        .append("Flight: ").append(mCurrentFlight.getFlightNumber()).append("\n")
                        .append("From ").append(mCurrentFlight.getOrigin())
                        .append(" to ").append(mCurrentFlight.getDestination()).append("\n")
                        .append("Seats: ").append(booking.getQuantity()).append("\n");
                list.add(stb.toString());
            }
        }
        // list now has strings of all bookings made by users for mCurrentFlight
        mEditFlightBookingList.setAdapter(new MyCustomAdapter(list, getApplicationContext()));
    }

    public boolean editFlight(String currentFlightNumber){
        // Finding the mCurrentFlight
        mFlightList = mEditFlightDAO.getFlights();
        for(Flight flight : mFlightList){
            if(currentFlightNumber.equals(flight.getFlightNumber())){
                mCurrentFlight = flight;
                break;
            }
        }

        if(mNotAvailableRadioButton.isChecked()){
            // Admin has chosen to remove flight from availability
            mEditFlightDAO.updateFlightAvailability(mCurrentFlight.getFlightId(), 1);
            return true;
        } else if (mIsAvailableRadioButton.isChecked()){
            // Admin has chosen to keep flight in availability
            mEditFlightDAO.updateFlightAvailability(mCurrentFlight.getFlightId(), 0);
            return true;
        } else {
            // User did not click radio buttons
            mEditFlightMessageDisplay.setText(R.string.sign_up_radio_error);
            mEditFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public void removeFlight(String currentFlightNumber){
        // All bookings for flight should be deleted
        // Then flight should be deleted last
        // Finding the mCurrentFlight
        mFlightList = mEditFlightDAO.getFlights();
        for(Flight flight : mFlightList){
            if(currentFlightNumber.equals(flight.getFlightNumber())){
                mCurrentFlight = flight;
                break;
            }
        }

        // Delete all bookings for that flight
        for(Booking booking : mBookingList){
            if(booking.getFlightId() == mCurrentFlight.getFlightId()){
                mEditBookingDAO.delete(booking);
            }
        }

        // All bookings for that flight should now be deleted
        mEditFlightDAO.delete(mCurrentFlight);
    }


    public static Intent getIntent(Context context, String username, String flightNumber){
        Intent intent = new Intent(context, EditFlightActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EDIT_FLIGHT_ACTIVITY_USER, username);
        extras.putString(EDIT_FLIGHT_ACTIVITY_FLIGHT, flightNumber);
        intent.putExtras(extras);
        return intent;
    }
}