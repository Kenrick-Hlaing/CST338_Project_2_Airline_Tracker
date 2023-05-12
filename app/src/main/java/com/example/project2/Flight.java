package com.example.project2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.project2.DB.AppDataBase;

@Entity(tableName = AppDataBase.FLIGHT_TABLE)
public class Flight {
    @PrimaryKey(autoGenerate = true)
    private int mFlightId;
    private String mFlightNumber;
    private String mOrigin;
    private String mDestination;
    private int mCapacity;
    private int mIsFull;

    public Flight(String flightNumber, String origin, String destination) {
        mFlightNumber = flightNumber;
        mOrigin = origin;
        mDestination = destination;
        mCapacity = 0;
        mIsFull = 0;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder("Flight ID: " + mFlightId + "\n"
                + "Flight Number: " + mFlightNumber + "\n"
                + "From " + mOrigin + " to " + mDestination + "\n"
                + "Capacity: " + mCapacity + "/200, Availability: ");
        if(mIsFull == 1){
            str.append("Not Available");
        } else {
            str.append("Available");
        }
        str.append("\n");
        str.append("================================\n");
        return str.toString();
    }

    public int getFlightId() {
        return mFlightId;
    }

    public void setFlightId(int flightId) {
        mFlightId = flightId;
    }

    public String getFlightNumber() {
        return mFlightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        mFlightNumber = flightNumber;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }

    public int getIsFull() {
        return mIsFull;
    }

    public void setIsFull(int isFull) {
        mIsFull = isFull;
    }
}
