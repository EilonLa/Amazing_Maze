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
 * Created by אילון on 26/01/2017.
 */

public class SignInScreen extends Activity {
    private final String ENTER_MESSAGE_NAME_TAG = "enter mUser name";
    private final String ENTER_MESSAGE_PASSWORD_TAG = "enter password";
    private final String ERROR_MESSAGE_NO_USER = "User not exists!";
    private final String ERROR_MESSAGE_PASSWORD = "wrong password!";
    private EditText mUserNameEditText;
    private EditText mPasswrdEditText;
    private Button mLoginBtn;
    private SignInScreen mSelf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mUserNameEditText = (EditText) findViewById(R.id.username_edtext);
        mPasswrdEditText = (EditText) findViewById(R.id.passwd_edtext);
        mLoginBtn = (Button) findViewById(R.id.login_button_signIn);
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

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUserNameEditText.getText().toString();
                String password = mPasswrdEditText.getText().toString();
                if (userName.length() == 0){
                    mUserNameEditText.setText(ENTER_MESSAGE_NAME_TAG);
                    mUserNameEditText.setTextColor(Color.RED);
                }
                if (password.length() == 0){
                    mPasswrdEditText.setText(ENTER_MESSAGE_PASSWORD_TAG);
                    mPasswrdEditText.setTextColor(Color.RED);
                }
                if (mUserNameEditText.getText().toString().compareTo(ENTER_MESSAGE_NAME_TAG)!=0 && mPasswrdEditText.getText().toString().compareTo(ENTER_MESSAGE_PASSWORD_TAG) != 0){
                    DataRowUser dataForUser = MainActivity.mDataBase.GetUserByName(userName);
                    if (dataForUser == null){
                        Toast.makeText(mSelf,ERROR_MESSAGE_NO_USER, Toast.LENGTH_LONG).show();
                    }else{
                        if (password.compareToIgnoreCase(dataForUser.GetPassword())!= 0){
                            Toast.makeText(mSelf,ERROR_MESSAGE_PASSWORD, Toast.LENGTH_LONG).show();
                        }else{
                            ArrayList<Trap> tempTraps = new ArrayList<>();
                            for (int trapIndex : dataForUser.GetTrapIndexes()){
                                tempTraps.add(new Trap(trapIndex));
                            }
                            MainActivity.mUser = new User(dataForUser.GetUserId(),dataForUser.GetPassword(),dataForUser.GetUserName(),dataForUser.GetCoins(),tempTraps);
                            SharedPreferences.Editor editor = MainActivity.mSharedPref.edit();
                            editor.putInt("lastId", dataForUser.GetUserId());
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                }
            }
        });

    }
}
