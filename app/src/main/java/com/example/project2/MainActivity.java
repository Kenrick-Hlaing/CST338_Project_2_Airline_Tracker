package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project2.DB.AppDataBase;
import com.example.project2.DB.UserDAO;
import com.example.project2.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER = "com.example.project2.MainActivityUser";

    TextView mMainSignInBanner;
    TextView mMainMessageDisplay;
    EditText mMainUsernameEditText;
    EditText mMainPasswordEditText;

    Button mLogIn;
    Button mSignUp;

    UserDAO mUserDAO;

    User mUser;

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMainSignInBanner = mBinding.mainSignInBanner;
        mMainMessageDisplay = mBinding.mainMessageDisplay;
        mMainMessageDisplay.setVisibility(View.INVISIBLE);
        mMainUsernameEditText = mBinding.mainUsernameEditText;
        mMainPasswordEditText = mBinding.mainPasswordEditText;
        mLogIn = mBinding.mainLogInButton;
        mSignUp = mBinding.mainSignUpButton;

        mUserDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .UserDAO();

        mLogIn.setOnClickListener(view -> LogIn());

        mSignUp.setOnClickListener(view -> {
            Intent intent = SignUpActivity.getIntent(getApplicationContext());
            startActivity(intent);
        });
    }

    private void LogIn(){
        String username = mMainUsernameEditText.getText().toString();
        String password = mMainPasswordEditText.getText().toString();
        mUser = mUserDAO.getUserByUsernameAndPassword(username, password);
        if(mUser != null){
            // The user is in the database
            // With successfully login, user should be brought to home page
            Intent intent = LandingActivity.getIntent(getApplicationContext(), username);
            startActivity(intent);
        } else {
            mMainMessageDisplay.setText(R.string.incorrect_user_cred_text);
            mMainMessageDisplay.setVisibility(View.VISIBLE);
            // The user is not in the database
        }
    }


    public static Intent getIntent(Context context){
        return new Intent(context, MainActivity.class);
    }
}