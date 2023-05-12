package com.example.project2;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.project2.DB.AppDataBase;

@Entity(tableName = AppDataBase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int mUserId;

    private String mUsername;
    private String mPassword;
    private int mIsAdmin;

    public User(String username, String password, int isAdmin) {
        mUsername = username;
        mPassword = password;
        mIsAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserId='" + mUserId + '\'' +
                "mUsername='" + mUsername + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mIsAdmin=" + mIsAdmin +
                '}';
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        mIsAdmin = isAdmin;
    }
}
