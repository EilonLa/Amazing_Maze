package UI.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.concurrent.atomic.AtomicBoolean;

import UI.Fragments.LoggedInFragment;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The SignInFragment fragment is inflated when the user wants to sign in
 *
 */
public class SignInFragment extends Fragment {
    private final String ENTER_MESSAGE_NAME_TAG = "enter user name";
    private final String ENTER_MESSAGE_PASSWORD_TAG = "enter password";
    private final String ERROR_MESSAGE_NO_USER = "User not exists!";
    private final String ERROR_MESSAGE_PASSWORD = "wrong password!";
    private EditText mUserNameEditText;
    private EditText mPasswrdEditText;
    private Button mLoginBtn;
    private MainActivity mActivity;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in, container, false);
        if(mActivity == null){
            mActivity = (MainActivity)getActivity();
        }
        mActivity.GetController().SetIsSignIn(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActivity = (MainActivity)getActivity();
        SetButtons();

    }

    public void SetButtons() {

        mUserNameEditText = (EditText) getActivity().findViewById(R.id.username_edtext);
        mPasswrdEditText = (EditText) getActivity().findViewById(R.id.passwd_edtext);
        mLoginBtn = (Button) getActivity().findViewById(R.id.login_button_signIn);
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
                final String userName = mUserNameEditText.getText().toString();
                final String password = mPasswrdEditText.getText().toString();
                mView = view;
                if (userName.length() == 0) {
                    mUserNameEditText.setText(ENTER_MESSAGE_NAME_TAG);
                    mUserNameEditText.setTextColor(Color.RED);
                }
                if (password.length() == 0) {
                    Toast.makeText(mActivity, ERROR_MESSAGE_PASSWORD, Toast.LENGTH_LONG).show();
                    mPasswrdEditText.setText("");
                }
                if (mUserNameEditText.getText().toString().compareTo(ENTER_MESSAGE_NAME_TAG) != 0 && mPasswrdEditText.getText().toString().compareTo(ENTER_MESSAGE_PASSWORD_TAG) != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.GetController().IsWaitingForFireBase(true);
                            mActivity.GetFireBaseOperator().GetUserFromFireBase(null,userName, password);
                            while (mActivity.GetController().IsWaitingForFireBase()){}
                            if (mActivity.GetController().GetUser() != null) {
                                Log.i("USER", "NOT NULL");
                                SharedPreferences.Editor editor = mActivity.GetController().GetSharedPref().edit();
                                editor.putString("lastId", mActivity.GetController().GetUser().GetId());
                                editor.commit();
                                getFragmentManager().beginTransaction().add(R.id.activity_main_login, new LoggedInFragment()).commit();
                                mActivity.GetController().GetSharedPref().edit();
                                editor.putString("lastId", mActivity.GetController().GetUser().GetId());
                                editor.commit();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.GetLogOut().setVisibility(View.VISIBLE);
                                        getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentById(R.id.log_container)).commit();
                                        getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentById(R.id.activity_main_login)).commit();
                                        getFragmentManager().beginTransaction().replace(R.id.activity_main_login, new LoggedInFragment()).commit();
                                        mActivity.GetController().SetIsSignIn(false);
                                        if (getActivity().getCurrentFocus() != null) {
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                                        }
                                    }
                                });

                            }
                            else
                                Log.i("USER", "IS NULL");
                        }
                    }).start();

                }
            }
        });

    }

}






