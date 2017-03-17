package UI.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import DB.DataRowUser;
import Logic.User;
import UI.Fragments.LoggedInFragment;
import activities.MainActivity;

/**
 * Created by אילון on 02/03/2017.
 */

public class SignUpFragment extends Fragment {
    private final String ENTER_NAME_TAG = "enter user name";
    private final String ENTER_PASSWORD_TAG = "enter password";
    private final String CONFIRM_PASSWORD_TAG = "confirm password";
    private final String PASSWORD_ERROR_TAG = "passwords do not match!";
    private final String USER_EXISTS_TAG = "User exists! choose another";
    private EditText mUserNameEditText;
    private EditText mPasswrdEditText;
    private EditText mPasswrdConEditText;
    private Button mLoginBtn;

    private MainActivity mActivity;

    private Object mLockObject = new Object();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActivity = (MainActivity) getActivity();
        SetButtons();

    }

    public void SetButtons() {
        mUserNameEditText = (EditText) getActivity().findViewById(R.id.username_edtext_signUp);
        mPasswrdEditText = (EditText) getActivity().findViewById(R.id.passwd_edtext_signUp);
        mLoginBtn = (Button) getActivity().findViewById(R.id.register_button_signUp);
        mPasswrdConEditText = (EditText) getActivity().findViewById(R.id.cnfpasswd_edtext_signUp);
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
                final String userName = mUserNameEditText.getText().toString();
                final String password = mPasswrdEditText.getText().toString();
                String cnPassword = mPasswrdConEditText.getText().toString();
                if (userName.length() == 0) {
                    mUserNameEditText.setText(ENTER_NAME_TAG);
                    mUserNameEditText.setTextColor(Color.RED);
                }
                if (password.length() == 0) {
                    mPasswrdEditText.setText(ENTER_PASSWORD_TAG);
                    mPasswrdEditText.setTextColor(Color.RED);
                }
                if (cnPassword.length() == 0) {
                    mPasswrdConEditText.setText(CONFIRM_PASSWORD_TAG);
                    mPasswrdConEditText.setTextColor(Color.RED);
                }
                boolean okToContinue = false;
                if (password.compareTo(cnPassword) == 0)
                    okToContinue = true;
                else {
                    Toast.makeText(getActivity().getApplicationContext(), PASSWORD_ERROR_TAG, Toast.LENGTH_LONG).show();
                }

                if (okToContinue && mUserNameEditText.getText().toString().compareTo(ENTER_NAME_TAG) != 0 && mPasswrdEditText.getText().toString().compareTo(ENTER_PASSWORD_TAG) != 0) {
                    if (mActivity != null ) {
                        DataRowUser dataForUser = mActivity.GetDBOperator().GetUserByName(userName);

                        if (dataForUser != null) {
                            Toast.makeText(getActivity().getApplicationContext(), USER_EXISTS_TAG, Toast.LENGTH_LONG).show();
                        } else {
                            dataForUser = new DataRowUser(userName, password, User.DEFAULT_NUM_OF_COINS, null);
                            if (mActivity != null) {
                                mActivity.GetDBOperator().AddRow_User(dataForUser);
                            }
                            String userId = mActivity.GetFireBaseOperator().SaveUserToFireBaseFirstTime(userName, password, User.DEFAULT_NUM_OF_COINS,null);
                            mActivity.GetController().SetUser(new User(userId, password, userName, User.DEFAULT_NUM_OF_COINS, null));
                            SharedPreferences.Editor editor = mActivity.GetController().GetSharedPref().edit();
                            editor.putString("lastId", mActivity.GetController().GetUser().GetId());
                            editor.commit();
                            mActivity.GetLogOut().setVisibility(View.VISIBLE);
                            getFragmentManager().beginTransaction().add(R.id.activity_main_login, new LoggedInFragment()).commit();
                            getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentById(R.id.log_container)).commit();
                            getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentById(R.id.activity_main_login)).commit();
                            mActivity.GetController().SetIsSignUp(false);
                            if (getActivity().getCurrentFocus() != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    }
                }
            }
        });
    }
}
