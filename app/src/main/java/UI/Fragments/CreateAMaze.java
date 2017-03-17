package UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdv.amazingmaze.R;

import UI.Board;
import activities.MainActivity;

/**
 * Created by אילון on 26/01/2017.
 */

public class CreateAMaze extends Fragment {
    private View mSetEntrance;
    private View mSetExit;
    private MainActivity mMainActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mMainActivity = (MainActivity)getActivity();
       // if ( mMainActivity.GetController().GetUser().GetBoard() == null) {
        mMainActivity.GetController().GetUser().SetBoard(new Board(mMainActivity, mMainActivity.GetController().GetUser(),  mMainActivity.GetController().GetUser().GetListDataBoardFromFireBase(), R.id.activity_create_maze_board, false));

        this.mSetEntrance = getActivity().findViewById(R.id.set_entrance);
        mSetEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mMainActivity.GetController().GetUser().GetBoard().IsEntranceActivated()) {
                     mMainActivity.GetController().GetUser().GetBoard().SetEntranceActivated(false);
                } else {
                     mMainActivity.GetController().GetUser().GetBoard().SetEntranceActivated(true);
                     mMainActivity.GetController().GetUser().GetBoard().SetExitActivated(false);
                }
            }
        });
        this.mSetExit = getActivity().findViewById(R.id.set_exit);
        mSetExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mMainActivity.GetController().GetUser().GetBoard().IsExitActivated()) {
                     mMainActivity.GetController().GetUser().GetBoard().SetExitActivated(false);
                } else {
                     mMainActivity.GetController().GetUser().GetBoard().SetExitActivated(true);
                     mMainActivity.GetController().GetUser().GetBoard().SetEntranceActivated(false);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_maze, container, false);
        return view;
    }

}




