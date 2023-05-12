package com.example.project2;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.project2.DB.AppDataBase;

@Entity(tableName = AppDataBase.BOOKING_TABLE)
public class Booking {
    @PrimaryKey(autoGenerate = true)
    private int mBookingId;
    private int mUserId;
    private int mFlightId;
    private int mQuantity;

    public Booking(int userId, int flightId, int quantity) {
        mUserId = userId;
        mFlightId = flightId;
        mQuantity = quantity;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "mBookingId=" + mBookingId +
                ", mUserId=" + mUserId +
                ", mFlightId=" + mFlightId +
                ", mQuantity=" + mQuantity +
                '}';
    }

    public int getBookingId() {
        return mBookingId;
    }

    public void setBookingId(int bookingId) {
        mBookingId = bookingId;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getFlightId() {
        return mFlightId;
    }

    public void setFlightId(int flightId) {
        mFlightId = flightId;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }
}
