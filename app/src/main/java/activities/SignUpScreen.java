package activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import DB.DataRowUser;
import Logic.Trap;
import Logic.User;

/**
 * Created by אילון on 31/01/2017.
 */

public class SignUpScreen extends Activity {
    private final String ENTER_NAME_TAG = "enter user name";
    private final String ENTER_PASSWORD_TAG = "enter password";
    private final String CONFIRM_PASSWORD_TAG = "confirm password";
    private final String PASSWORD_ERROR_TAG = "passwords do not match!";
    private final String USER_EXISTS_TAG = "User exists! choose another";
    private EditText mUserNameEditText;
    private EditText mPasswrdEditText;
    private EditText mPasswrdConEditText;
    private Button mLoginBtn;
    private SignUpScreen mSelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mUserNameEditText = (EditText) findViewById(R.id.username_edtext_signUp);
        mPasswrdEditText = (EditText) findViewById(R.id.passwd_edtext_signUp);
        mLoginBtn = (Button) findViewById(R.id.register_button_signUp);
        mPasswrdConEditText = (EditText) findViewById(R.id.cnfpasswd_edtext_signUp);
        mSelf = this;
        mUserNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserNameEditText.setText("");
                mUserNameEditText.setTextColor(Color.BLACK);
            }
        });
        mPasswrdEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPasswrdEditText.setText("");
                mPasswrdEditText.setTextColor(Color.BLACK);
            }
        });
        mPasswrdConEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPasswrdConEditText.setText("");
                mPasswrdConEditText.setTextColor(Color.BLACK);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserNameEditText.getText().toString();
                String password = mPasswrdEditText.getText().toString();
                String cnPassword = mPasswrdConEditText.getText().toString();
                if (userName.length() == 0){
                    mUserNameEditText.setText(ENTER_NAME_TAG);
                    mUserNameEditText.setTextColor(Color.RED);
                }
                if (password.length() == 0){
                    mPasswrdEditText.setText(ENTER_PASSWORD_TAG);
                    mPasswrdEditText.setTextColor(Color.RED);
                }
                if (cnPassword.length() == 0){
                    mPasswrdConEditText.setText(CONFIRM_PASSWORD_TAG);
                    mPasswrdConEditText.setTextColor(Color.RED);
                }
                boolean okToContinue = false;
                if (password.compareTo(cnPassword) == 0)
                    okToContinue = true;
                else{
                    Toast.makeText(mSelf,PASSWORD_ERROR_TAG, Toast.LENGTH_LONG).show();
                }

                if (okToContinue && mUserNameEditText.getText().toString().compareTo(ENTER_NAME_TAG)!=0 && mPasswrdEditText.getText().toString().compareTo(ENTER_PASSWORD_TAG) != 0){
                    DataRowUser dataForUser = MainActivity.mDataBase.GetUserByName(userName);
                    if (dataForUser != null){
                        Toast.makeText(mSelf,USER_EXISTS_TAG, Toast.LENGTH_LONG).show();
                    }else{
                        dataForUser = new DataRowUser(userName,password,User.DEFAULT_NUM_OF_COINS,null);
                        MainActivity.mDataBase.AddRow_User(dataForUser);
                        int userId = MainActivity.mDataBase.GetUserByName(userName).GetUserId();
                        MainActivity.mUser = new User (userId,password,userName,User.DEFAULT_NUM_OF_COINS, new ArrayList<Trap>());
                        SharedPreferences.Editor editor = MainActivity.mSharedPref.edit();
                        editor.putInt("lastId", dataForUser.GetUserId());
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            }
        });
    }
}
