package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.UserDAO;
import com.example.project2.databinding.ActivitySignUpBinding;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    TextView mSignUpBanner;
    TextView mSignUpMessageDisplay;
    EditText mSignUpUsernameEditText;
    EditText mSignupPasswordEditText;
    EditText mSignupPasswordConfirmEditText;

    Button mSuSignUpButton;
    Button mSignUpBackButton;
    RadioButton mIsAdminRadioButton;
    RadioButton mIsUserRadioButton;

    UserDAO suUserDAO;
    List<User> mUserList;

    ActivitySignUpBinding mSignUpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mSignUpBinding.getRoot());

        mSignUpBanner = mSignUpBinding.signUpBanner;
        mSignUpMessageDisplay = mSignUpBinding.signUpMessageDisplay;
        mSignUpMessageDisplay.setVisibility(View.INVISIBLE);
        mSignUpUsernameEditText = mSignUpBinding.signUpUsernameEditText;
        mSignupPasswordEditText = mSignUpBinding.signUpPasswordEditText;
        mSignupPasswordConfirmEditText = mSignUpBinding.signUpPasswordConfirmEditText;
        mIsAdminRadioButton = mSignUpBinding.isAdminRadioButton;
        mIsUserRadioButton = mSignUpBinding.isUserRadioButton;
        mSuSignUpButton = mSignUpBinding.suSignUpButton;
        mSignUpBackButton = mSignUpBinding.suBackButton;

        suUserDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .UserDAO();

        mSuSignUpButton.setOnClickListener(view -> SignUp());

        mSignUpBackButton.setOnClickListener(view -> {
            Intent intent = MainActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }

    private void SignUp(){
        // Pull list of users from database
        mUserList = suUserDAO.getUsers();
        // Now holds list of all users

        // Check to see if password is written twice
        String password = mSignupPasswordEditText.getText().toString();
        String passwordConfirm = mSignupPasswordConfirmEditText.getText().toString();
        if(!password.equals(passwordConfirm)){
            mSignUpMessageDisplay.setText(R.string.sign_up_password_error);
            mSignUpMessageDisplay.setVisibility(View.VISIBLE);
            return;
        }

        // check to see if username is available
        String username = mSignUpUsernameEditText.getText().toString();
        for(User user : mUserList){
            if(user.getUsername().equals(username)){
                mSignUpMessageDisplay.setText(R.string.sign_up_username_error);
                mSignUpMessageDisplay.setVisibility(View.VISIBLE);
                return;
            }
        }


        // User or Admin now needs to be confirmed
        int isAdmin;
        if(mIsAdminRadioButton.isChecked()){
            // User is an admin
            isAdmin = 1;
        } else if (mIsUserRadioButton.isChecked()){
            // User is not an admin
            isAdmin = 0;
        } else {
            // User did not click radio buttons
            mSignUpMessageDisplay.setText(R.string.sign_up_radio_error);
            mSignUpMessageDisplay.setVisibility(View.VISIBLE);
            return;
        }

        // At this point all credentials should be fine
        // insert that info into the user database
        User newUser = new User(username, password, isAdmin);
        suUserDAO.insert(newUser);
        // Then return to log in page
        Intent intent = MainActivity.getIntent(getApplicationContext());
        startActivity(intent);
    }

    public static Intent getIntent(Context context){
        return new Intent(context, SignUpActivity.class);
    }
}