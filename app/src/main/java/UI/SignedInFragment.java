package UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cdv.amazingmaze.R;

import activities.MainActivity;

/**
 * Created by אילון on 29/01/2017.
 */

public class SignedInFragment extends Fragment {
    private TextView mWelcomeUser;


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
        Inflate();
    }

    public void Inflate(){
        mWelcomeUser = (TextView) getActivity().findViewById(R.id.user_name_logged);
        mWelcomeUser.setText(MainActivity.mUser.GetUserName());


    }
}
