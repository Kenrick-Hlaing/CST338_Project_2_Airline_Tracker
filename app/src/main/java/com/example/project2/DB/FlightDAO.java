package com.example.project2.DB;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project2.Flight;
import com.example.project2.User;


import java.util.List;

@Dao
public interface FlightDAO {
    @Insert
    void insert(Flight...flights);

    @Update
    void update(Flight...flights);

    @Delete
    void delete(Flight flight);

    @Query("SELECT * FROM " + AppDataBase.FLIGHT_TABLE)
    List<Flight> getFlights();

    @Query("SELECT * FROM " + AppDataBase.FLIGHT_TABLE + " WHERE mFlightId = :flightId")
    Flight getFlightByFlightId(int flightId);

    @Query("UPDATE flight_table SET mCapacity = :capacity WHERE mFlightId = :flightId")
    void updateFlightCapacity(int flightId, int capacity);

    @Query("UPDATE flight_table SET mIsFull = :isFull WHERE mFlightId = :flightId")
    void updateFlightAvailability(int flightId, int isFull);
}
