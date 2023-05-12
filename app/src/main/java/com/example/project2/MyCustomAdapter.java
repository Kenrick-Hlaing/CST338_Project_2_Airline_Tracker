package com.example.project2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.BookingDAO;
import com.example.project2.DB.FlightDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_layout, null);
        }

        //Handle TextView and display string from your list
        TextView tvContact= (TextView)view.findViewById(R.id.textViewContent);
        tvContact.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button callbtn= (Button)view.findViewById(R.id.btn);

        BookingDAO landingRemoverBookingDAO = Room.databaseBuilder(context, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BookingDAO();

        FlightDAO landingRemoverFlightDAO = Room.databaseBuilder(context, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .FlightDAO();

        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                Matcher matcher = Pattern.compile("\\d+").matcher(list.get(position));
                matcher.find();
                int id = Integer.parseInt(matcher.group());
                List<Booking> bookingList = landingRemoverBookingDAO.getBookings();
                List<Flight> flightList = landingRemoverFlightDAO.getFlights();
                for(Booking booking : bookingList){
                    if(booking.getBookingId() == id){
                        // update the capacity and availability of the flight
                        int currentSeats = 0;
                        for(Flight flight : flightList){
                            if(booking.getFlightId() == flight.getFlightId()){
                                currentSeats = flight.getCapacity();
                                break;
                            }
                        }
                        currentSeats = (currentSeats - booking.getQuantity());
                        landingRemoverFlightDAO.updateFlightCapacity
                                (booking.getFlightId(), currentSeats);
                        if(currentSeats < 200){
                            landingRemoverFlightDAO.updateFlightAvailability(booking.getFlightId(), 0);
                        }
                        landingRemoverBookingDAO.delete(booking);
                        //String text = (currentSeats) + "";
                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(context, text, duration);
                        //toast.show();
                        callbtn.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }
        });

        return view;
    }
}