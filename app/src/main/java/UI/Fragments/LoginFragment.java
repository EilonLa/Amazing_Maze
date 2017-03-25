package UI.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cdv.amazingmaze.R;

import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The LoginFragment fragment is inflated when the user is logged out
 *
 */

public class LoginFragment extends Fragment {
    private Button mSignIn;
    private Button mSignUp;
    private MainActivity mActivity;

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
        mActivity = (MainActivity)getActivity();
        Inflate();
    }

    public void Inflate(){
        mSignIn = (Button) getActivity().findViewById(R.id.activity_main_sign_in);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().add(R.id.log_container,new SignInFragment()).commit();
                mActivity.GetController().SetIsSignIn(true);
            }
        });
        mSignUp = (Button) getActivity().findViewById(R.id.activity_main_sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().add(R.id.log_container,new SignUpFragment()).commit();
                mActivity.GetController().SetIsSignUp(true);
            }
        });

    }

}
