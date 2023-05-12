package com.example.project2.DB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.project2.Booking;
import com.example.project2.Flight;
import com.example.project2.User;

@Database(entities = {User.class, Flight.class, Booking.class}, version = 2)
public abstract class AppDataBase extends RoomDatabase {
    public static final String DATABASE_NAME = "User.db";
    public static final String USER_TABLE = "user_table";
    public static final String FLIGHT_TABLE = "flight_table";
    public static final String BOOKING_TABLE = "booking_table";

    private static volatile AppDataBase instance;
    private static final Object LOCK = new Object();

    public abstract UserDAO UserDAO();
    public abstract FlightDAO FlightDAO();
    public abstract BookingDAO BookingDAO();

    public static AppDataBase getInstance(Context context){
        if(instance == null){
            synchronized (LOCK){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class,
                            DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

}
