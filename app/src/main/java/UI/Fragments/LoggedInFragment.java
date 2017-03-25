package UI.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cdv.amazingmaze.R;

import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The LoggedInFragment fragment is inflated when the user was created properly
 *
 */

public class LoggedInFragment extends Fragment {
    private TextView mWelcomeUser;
    private MainActivity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signed_in_main, container, false);
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
        mWelcomeUser = (TextView) getActivity().findViewById(R.id.user_name_logged);
        mWelcomeUser.setText(mActivity.GetController().GetUser().GetUserName());

    }
}
