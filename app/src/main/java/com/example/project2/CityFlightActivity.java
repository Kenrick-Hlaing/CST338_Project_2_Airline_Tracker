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
import com.example.project2.databinding.ActivityCityFlightBinding;


import java.util.List;

public class CityFlightActivity extends AppCompatActivity {

    private static final String CITY_FLIGHT_ACTIVITY_USER = "com.example.project2.CityFlightActivityUser";
    private static final String CITY_FLIGHT_ACTIVITY_CITY = "com.example.project2.CityFlightActivityCity";

    TextView mCityFlightBanner;
    TextView mCityFlightArrivingTitle;
    TextView mCityFlightArrivingDisplay;
    TextView mCityFlightDepartingTitle;
    TextView mCityFlightDepartingDisplay;
    TextView mCityFlightMessageDisplay;

    EditText mCityFlightFlightNumber;
    EditText mCityFlightBookingQuantity;

    Button mCityFlightBookButton;
    Button mCityFlightBackButton;

    UserDAO mCityFlightUserDAO;
    FlightDAO mCityFlightFlightDAO;
    BookingDAO mCityFlightBookingDAO;

    User mCurrentUser;

    ActivityCityFlightBinding mCityFlightBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_flight);

        mCityFlightBinding = ActivityCityFlightBinding.inflate(getLayoutInflater());
        setContentView(mCityFlightBinding.getRoot());

        mCityFlightBanner = mCityFlightBinding.cityFlightBanner;
        mCityFlightArrivingTitle = mCityFlightBinding.cityFlightArrivingTitle;
        mCityFlightArrivingDisplay = mCityFlightBinding.cityFlightArrivingDisplay;
        mCityFlightArrivingDisplay.setMovementMethod(new ScrollingMovementMethod());
        mCityFlightDepartingTitle = mCityFlightBinding.cityFlightDepartingTitle;
        mCityFlightDepartingDisplay = mCityFlightBinding.cityFlightDepartingDisplay;
        mCityFlightDepartingDisplay.setMovementMethod(new ScrollingMovementMethod());
        mCityFlightMessageDisplay = mCityFlightBinding.cityFlightMessageDisplay;

        mCityFlightFlightNumber = mCityFlightBinding.cityFlightFlightNumber;
        mCityFlightBookingQuantity = mCityFlightBinding.cityFlightBookingQuantity;

        mCityFlightBookButton = mCityFlightBinding.cityFlightBookButton;
        mCityFlightBackButton = mCityFlightBinding.cityFlightBackButton;

        mCityFlightUserDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .UserDAO();

        mCityFlightFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        mCityFlightBookingDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BookingDAO();

        // Pull Username and City ame from extras
        String currentUsername = getIntent().getStringExtra(CITY_FLIGHT_ACTIVITY_USER);
        List<User> mUserList = mCityFlightUserDAO.getUsers();
        for(User user : mUserList){
            if(user.getUsername().equals(currentUsername)){
                mCurrentUser = user;
                break;
            }
        }
        String currentCity = getIntent().getStringExtra(CITY_FLIGHT_ACTIVITY_CITY);
        StringBuilder banner = new StringBuilder();
        banner.append(currentCity).append(" Flights");
        mCityFlightBanner.setText(banner.toString());

        // Add all available flights to lists
        List<Flight> mFlightList = mCityFlightFlightDAO.getFlights();
        mFlightList.removeIf(flight -> flight.getIsFull() == 1);
        StringBuilder arrival = new StringBuilder();
        StringBuilder departure = new StringBuilder();
        for(Flight flight : mFlightList){
            // if the flight's origin is the currentCity, append to departure
            if(flight.getOrigin().equals(currentCity)){
                departure.append("ID: ").append(flight.getFlightId())
                        .append("\n").append("Number: ").append(flight.getFlightNumber()).append("\n")
                        .append("From ").append(flight.getOrigin())
                        .append("\nTo ").append(flight.getDestination())
                        .append("\n").append("Capacity: ").append(flight.getCapacity()).append("/200\n");
                departure.append("\n\n\n");
            }
            // if the flight's destination is the currentCity, append to arrival
            if(flight.getDestination().equals(currentCity)){
                arrival.append("ID: ").append(flight.getFlightId())
                        .append("\n").append("Number: ").append(flight.getFlightNumber()).append("\n")
                        .append("From ").append(flight.getOrigin())
                        .append("\nTo ").append(flight.getDestination())
                        .append("\n").append("Capacity: ").append(flight.getCapacity()).append("/200\n");
                arrival.append("\n\n\n");
            }
        }

        // Add strings to the list displays
        if(!arrival.toString().equals("")){
            mCityFlightArrivingDisplay.setText(arrival.toString());
        } else {
            mCityFlightArrivingDisplay.setText(R.string.no_flights_available);
        }
        if(!departure.toString().equals("")){
            mCityFlightDepartingDisplay.setText(departure.toString());
        } else {
            mCityFlightDepartingDisplay.setText(R.string.no_flights_available);
        }

        // Add onClickListener for Back Button
        mCityFlightBackButton.setOnClickListener(view -> {
            Intent intent = BookFlightActivity.getIntent(getApplicationContext(), currentUsername);
            startActivity(intent);
        });

        // Add an onClickListener for book flight button
        mCityFlightBookButton.setOnClickListener(view -> {
            if(bookFlight()){
                Intent intent = LandingActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });
    }

    public boolean bookFlight(){
        List<Flight> flightList = mCityFlightFlightDAO.getFlights();
        if(!flightList.isEmpty()){
            flightList.removeIf(flight -> flight.getIsFull() != 0);
            flightList.removeIf(flight -> flight.getCapacity() >= 200);
        } else {
            mCityFlightMessageDisplay.setText(R.string.no_flights_available);
            mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        String flightNumber = mCityFlightFlightNumber.getText().toString();
        // Check if user entered a flight number
        if(flightNumber.equals("")){
            mCityFlightMessageDisplay.setText(R.string.add_flight_error_no_flight_number);
            mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
            return false;
        }

        // Check if flight Number is within list
        for(Flight flight : flightList){
            if(flightNumber.equals(flight.getFlightNumber())){
                // If flight number matches a flight
                // Check if the user entered a booking quantity
                int quantity;
                if(mCityFlightBookingQuantity.getText().toString().equals("")){
                    mCityFlightMessageDisplay.setText(R.string.book_flight_error_no_booking_amount);
                    mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    quantity = Integer.parseInt(mCityFlightBookingQuantity.getText().toString());
                }
                // Check to see if quantity fits flight capacity
                if(quantity > (200 - flight.getCapacity())){
                    mCityFlightMessageDisplay.setText(R.string.book_flight_error_booking_amount_too_big);
                    mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                }
                if(quantity == 0){
                    mCityFlightMessageDisplay.setText(R.string.book_flight_error_zero_amount);
                    mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
                    return false;
                }

                // At this point flight number and quantity should be valid
                // Add a new booking to the booking table
                // Using the userId from the mCurrentUser, flightId from flight, and quantity
                Booking newBooking = new Booking(mCurrentUser.getUserId(), flight.getFlightId(), quantity);
                mCityFlightBookingDAO.insert(newBooking);
                // Update the flight capacity and availability
                mCityFlightFlightDAO.updateFlightCapacity(flight.getFlightId(), (quantity + flight.getCapacity()));
                if(flight.getCapacity() >= 200){
                    mCityFlightFlightDAO.updateFlightAvailability(flight.getFlightId(), 1);
                }
                return true;
            }
        }
        mCityFlightMessageDisplay.setText(R.string.edit_flight_number_error);
        mCityFlightMessageDisplay.setVisibility(View.VISIBLE);
        return false;
    }

    public static Intent getIntent(Context context, String username, String cityName){
        Intent intent = new Intent(context, CityFlightActivity.class);
        Bundle extras = new Bundle();
        extras.putString(CITY_FLIGHT_ACTIVITY_USER, username);
        extras.putString(CITY_FLIGHT_ACTIVITY_CITY, cityName);
        intent.putExtras(extras);
        return intent;
    }
}