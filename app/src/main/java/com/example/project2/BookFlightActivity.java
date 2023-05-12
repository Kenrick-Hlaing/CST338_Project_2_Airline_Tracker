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
import com.example.project2.DB.BookingDAO;
import com.example.project2.DB.FlightDAO;
import com.example.project2.DB.UserDAO;
import com.example.project2.databinding.ActivityBookFlightBinding;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class BookFlightActivity extends AppCompatActivity {
    private static final String BOOK_FLIGHT_ACTIVITY_USER = "com.example.project2.BookFlightActivityUser";

    TextView mBookFlightBanner;
    TextView mBookFlightFlightListTitle;
    TextView mBookFlightListDisplay;
    TextView mBookFlightMessageDisplay;

    EditText mBookFlightByCity;
    Button mBookFlightByCityButton;
    EditText mBookFlightByFlightNumber;
    EditText mBookFlightBookingQuantity;
    Button mBookFlightByNumberButton;
    Button mBookFlightBackButton;

    UserDAO mBookFlightUserDAO;
    FlightDAO mBookFlightFlightDAO;
    BookingDAO mBookFlightBookingDAO;

    User mCurrentUser;

    ActivityBookFlightBinding mBookFlightBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_flight);

        mBookFlightBinding = ActivityBookFlightBinding.inflate(getLayoutInflater());
        setContentView(mBookFlightBinding.getRoot());

        // Wiring up stuff
        mBookFlightBanner = mBookFlightBinding.bookFlightBanner;
        mBookFlightFlightListTitle = mBookFlightBinding.bookFlightFlightListTitle;
        mBookFlightListDisplay = mBookFlightBinding.bookFlightListDisplay;
        mBookFlightListDisplay.setMovementMethod(new ScrollingMovementMethod());
        mBookFlightMessageDisplay = mBookFlightBinding.bookFlightMessageDisplay;
        mBookFlightByCity = mBookFlightBinding.bookFlightByCity;
        mBookFlightByCityButton = mBookFlightBinding.bookFlightByCityButton;
        mBookFlightByFlightNumber = mBookFlightBinding.bookFlightByFlightNumber;
        mBookFlightBookingQuantity = mBookFlightBinding.bookFlightBookingQuantity;
        mBookFlightByNumberButton = mBookFlightBinding.bookFlightByNumberButton;
        mBookFlightBackButton = mBookFlightBinding.bookFlightBackButton;

        mBookFlightUserDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .UserDAO();

        mBookFlightFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        mBookFlightBookingDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BookingDAO();

        // Add the flights to mBookFlightListDisplay if isFull == 0
        List<Flight> mFlightList = mBookFlightFlightDAO.getFlights();
        ArrayList<String> cityList = new ArrayList<>();
        if(!mFlightList.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for(Flight flight : mFlightList){
                if(flight.getIsFull() == 0){
                    sb.append(flight.toString());
                }
            }
            mBookFlightListDisplay.setText(sb.toString());
        } else {
            mBookFlightListDisplay.setText(R.string.no_flights_available);
        }

        // Grab all cities from available flight list

        for(Flight flight : mFlightList){
            if(flight.getIsFull() == 0) {
                if (!cityList.contains(flight.getOrigin())) {
                    cityList.add(flight.getOrigin());
                }
                if (!cityList.contains(flight.getDestination())) {
                    cityList.add(flight.getDestination());
                }
            }
        }

        // Set the mCurrentUser based on the currentUsername
        // This is for assigning the userId field for the booking table
        String currentUsername = getIntent().getStringExtra(BOOK_FLIGHT_ACTIVITY_USER);
        mCurrentUser = mBookFlightUserDAO.getUserByUsername(currentUsername);
//        for(User user : mUserList){
//            if(user.getUsername().equals(currentUsername)){
//                mCurrentUser = user;
//                break;
//            }
//        }

        // Add an onClickListener for 'Back' button
        mBookFlightBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LandingActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });

        // Add an onClickListener for Book Flight By City Button
        mBookFlightByCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredCity = mBookFlightByCity.getText().toString();
                if(cityList.contains(enteredCity)){
                    // Intent to CityFlightActivity
                    Intent intent = CityFlightActivity.getIntent(getApplicationContext(), currentUsername, enteredCity);
                    startActivity(intent);
                } else if(enteredCity.equals("")){
                    mBookFlightMessageDisplay.setText(R.string.book_flight_error_no_city);
                    mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
                } else {
                    mBookFlightMessageDisplay.setText(R.string.book_flight_error_invalid_city);
                    mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add a onClickListener for add by flight number button
        mBookFlightByNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a function for booking by flight number
                // Pass String from mBookFlightByFlightNumber
                if(byFlightNumber()){
                    Intent intent = LandingActivity.getIntent(getApplicationContext(), currentUsername);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean byFlightNumber(){
        // Grab list of all available flights
        List<Flight> flightList = mBookFlightFlightDAO.getFlights();
        if(!flightList.isEmpty()){
            flightList.removeIf(flight -> flight.getIsFull() != 0);
            flightList.removeIf(flight -> flight.getCapacity() >= 200);
        } else {
            mBookFlightMessageDisplay.setText(R.string.no_flights_available);
            mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        String flightNumber = mBookFlightByFlightNumber.getText().toString();
        // Check if user entered a flight number
        if(flightNumber.equals("")){
            mBookFlightMessageDisplay.setText(R.string.add_flight_error_no_flight_number);
            mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        // Check if flight Number is within list
        for(Flight flight : flightList){
            if(flightNumber.equals(flight.getFlightNumber())){
                // If flight number matches a flight
                // Check if the user entered a booking quantity
                int quantity;
                if(mBookFlightBookingQuantity.getText().toString().equals("")){
                    mBookFlightMessageDisplay.setText(R.string.book_flight_error_no_booking_amount);
                    mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    quantity = Integer.parseInt(mBookFlightBookingQuantity.getText().toString());
                }
                // Check to see if quantity fits flight capacity
                if(quantity > (200 - flight.getCapacity())){
                    mBookFlightMessageDisplay.setText(R.string.book_flight_error_booking_amount_too_big);
                    mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                }
                if(quantity == 0){
                    mBookFlightMessageDisplay.setText(R.string.book_flight_error_zero_amount);
                    mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                }

                // At this point flight number and quantity should be valid
                // Add a new booking to the booking table
                // Using the userId from the mCurrentUser, flightId from flight, and quantity
                Booking newBooking = new Booking(mCurrentUser.getUserId(), flight.getFlightId(), quantity);
                mBookFlightBookingDAO.insert(newBooking);
                // Update the flight capacity and availability
                mBookFlightFlightDAO.updateFlightCapacity(flight.getFlightId(), (quantity + flight.getCapacity()));
                if(flight.getCapacity() >= 200){
                    mBookFlightFlightDAO.updateFlightAvailability(flight.getFlightId(), 1);
                }
                return true;
            }
        }
        mBookFlightMessageDisplay.setText(R.string.edit_flight_number_error);
        mBookFlightMessageDisplay.setVisibility(View.VISIBLE);
        return false;
    }

    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, BookFlightActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, String username){
        Intent intent = new Intent(context, BookFlightActivity.class);
        intent.putExtra(BOOK_FLIGHT_ACTIVITY_USER, username);
        return intent;
    }
}