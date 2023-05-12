package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.BookingDAO;
import com.example.project2.DB.FlightDAO;
import com.example.project2.DB.UserDAO;
import com.example.project2.databinding.ActivityLandingBinding;

import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends AppCompatActivity {

    private static final String LANDING_ACTIVITY_USER = "com.example.project2.LandingActivityUser";

    TextView mLandingBanner;
    TextView mLandingMessageDisplay;
    ListView mLandingBookingDisplayTable;
    TextView mLandingBookingTableTitle;


    Button mLandingBookButton;
    Button mLandingAdminButton;

    UserDAO mLandingUserDAO;
    FlightDAO mLandingFlightDAO;
    BookingDAO mLandingBookingDAO;

    User mCurrentUser;
    List<Booking> mBookingList;
    List<Flight> mFlightList;

    ActivityLandingBinding mLandingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mLandingBinding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(mLandingBinding.getRoot());

        mLandingBanner = mLandingBinding.landingBanner;
        mLandingBookingTableTitle = mLandingBinding.landingBookingTableTitle;
        mLandingMessageDisplay = mLandingBinding.landingMessageDisplay;
        mLandingBookingDisplayTable = mLandingBinding.landingBookingDisplayTable;
        mLandingBookButton = mLandingBinding.landingBookButton;
        mLandingAdminButton = mLandingBinding.landingAdminButton;

        mLandingUserDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .UserDAO();

        mLandingFlightDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        mLandingBookingDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BookingDAO();

        // Find the Current User based on the currentUsername
        String currentUsername = getIntent().getStringExtra(LANDING_ACTIVITY_USER);
        mCurrentUser = mLandingUserDAO.getUserByUsername(currentUsername);
//        for(User user : mUserList){
//            if(user.getUsername().equals(currentUsername)){
//                mLandingMessageDisplay.setText(currentUsername);
//                mLandingMessageDisplay.setVisibility(View.VISIBLE);
//                mCurrentUser = user;
//                break;
//            }
//        }

        refreshDisplay();


        // if the user is an admin, reveal the admin actions button
        if(mCurrentUser.getIsAdmin() == 1){
            mLandingAdminButton.setVisibility(View.VISIBLE);
        }

        mLandingAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AdminActionActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });

        // Add a onClickListener for Book Button
        mLandingBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass currentUsername into the extra
                // Update all flights to isFull if capacity is 200
                List<Flight> mFlightList = mLandingFlightDAO.getFlights();
                for(Flight flight : mFlightList){
                    if(flight.getCapacity() == 200){
                        mLandingFlightDAO.updateFlightAvailability(flight.getFlightId(), 1);
                    }
                }
                Intent intent = BookFlightActivity.getIntent(getApplicationContext(), currentUsername);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Menu config
        if(item.getItemId() == R.id.landingLogOutMenuButton){
            toastMaker("Log Out Selected");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Log Out User?");

            alertBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = MainActivity.getIntent(getApplicationContext());
                            startActivity(intent);
                        }
                    });

            alertBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // something, don't log out
                            toastMaker("You're Still Here?");
                        }
                    });

            alertBuilder.setCancelable(true);
            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    toastMaker("You're Still Here?");
                }
            });

            alertBuilder.create().show();
            return true;
        }
        if(item.getItemId() == R.id.landingDeleteUserMenuButton){
            toastMaker("Delete User Selected");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Delete User?");

            alertBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<Booking> deletedBookings = mLandingBookingDAO.getBookingsByUserId(mCurrentUser.getUserId());
                            // Update flight availability and capacity for flights
                            for(Booking booking : deletedBookings){
                                Flight flight = mLandingFlightDAO.getFlightByFlightId(booking.getFlightId());
                                mLandingFlightDAO.updateFlightCapacity(flight.getFlightId(), (flight.getCapacity() - booking.getQuantity()));
                                if((flight.getCapacity() - booking.getQuantity()) < 200){
                                    mLandingFlightDAO.updateFlightAvailability(flight.getFlightId(), 0);
                                }
                                mLandingBookingDAO.delete(booking);
                            }
                            // All bookings by the user via user id are now deleted
                            mLandingUserDAO.delete(mCurrentUser);
                            Intent intent = MainActivity.getIntent(getApplicationContext());
                            startActivity(intent);
                        }
                    });

            alertBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // something, don't log out
                            toastMaker("You're Still Here?");
                        }
                    });

            alertBuilder.setCancelable(true);
            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    toastMaker("You're Still Here?");
                }
            });

            alertBuilder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, LandingActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, String username){
        Intent intent = new Intent(context, LandingActivity.class);
        intent.putExtra(LANDING_ACTIVITY_USER, username);
        return intent;
    }

    public void refreshDisplay(){
        mBookingList = mLandingBookingDAO.getBookings();
        mFlightList = mLandingFlightDAO.getFlights();
        ArrayList<String> list = new ArrayList<>();
        // Arrange String to look like: Flight: ####, From #### to ####, Seats: #
        for(Booking booking : mBookingList){
            if(booking.getUserId() == mCurrentUser.getUserId()){
                for(Flight flight : mFlightList){
                    if(flight.getFlightId() == booking.getFlightId()){
                        StringBuilder stb = new StringBuilder();
                        stb.append("BookingId: ").append(booking.getBookingId()).append("\n")
                                .append("Flight: ").append(flight.getFlightNumber()).append("\n")
                                .append("From ").append(flight.getOrigin())
                                .append(" to ").append(flight.getDestination()).append("\n")
                                .append("Seats: ").append(booking.getQuantity()).append("\n");
                        list.add(stb.toString());
                    }
                }
            }
        }
        // list now has strings of all bookings made by user
        mLandingBookingDisplayTable.setAdapter(new MyCustomAdapter(list, getApplicationContext()));
    }
}