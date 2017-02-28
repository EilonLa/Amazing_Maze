package UI;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cdv.amazingmaze.R;

import activities.SignInScreen;
import activities.SignUpScreen;

/**
 * Created by אילון on 29/01/2017.
 */


public class LoginFragment extends Fragment {
    private Button mSignIn;
    private Button mSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        Inflate();
    }

    public void Inflate(){
        mSignIn = (Button) getActivity().findViewById(R.id.activity_main_sign_in);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SignInScreen.class);
                intent.putExtra("mSignIn",true);
                startActivity(intent);
            }
        });
        mSignUp = (Button) getActivity().findViewById(R.id.activity_main_sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SignUpScreen.class);
                intent.putExtra("mSignUp",false);
                startActivity(intent);
            }
        });
    }

}
