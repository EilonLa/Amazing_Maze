package UI.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.cdv.amazingmaze.R;

import Logic.ExceptionHandler;
import UI.Board;
import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The CreateAMaze fragment is inflated when the user wants to edit his maze
 *
 */

public class CreateAMaze extends Fragment {
    private View mSetEntrance;
    private View mSetExit;
    private Button mBackBtn;
    private MainActivity mActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActivity = (MainActivity) getActivity();
        if (mActivity.GetController().GetUser().GetBoard() == null) {
            try {
                mActivity.GetController().GetUser().SetBoard(new Board(mActivity, mActivity.GetController().GetUser(), mActivity.GetController().GetUser().GetListDataBoardFromFireBase(), R.id.activity_create_maze_board, false));
            } catch (Exception e) {
                e.printStackTrace();
                
                new ExceptionHandler( e, mActivity.GetFireBaseOperator());
            }
        } else {
            try {
                new Board(mActivity.GetController().GetUser().GetBoard());
            } catch (Exception e) {
                e.printStackTrace();
                new ExceptionHandler( e, mActivity.GetFireBaseOperator());
            }
        }
        mBackBtn = (Button) getActivity().findViewById(R.id.back_create);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.GetController().GetActiveBoard().DrainStack();
                mActivity.onBackPressed();
            }
        });
        this.mSetEntrance = getActivity().findViewById(R.id.set_entrance);
        mSetEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetExit.setBackgroundColor(Color.BLACK);
                mActivity.GetController().ViewToReset(mSetEntrance);
                if (mActivity.GetController().GetUser().GetBoard().IsEntranceActivated()) {
                    mActivity.GetController().GetUser().GetBoard().SetEntranceActivated(false);
                    mSetEntrance.setBackgroundColor(Color.BLACK);
                } else {
                    mActivity.GetController().GetUser().GetBoard().SetEntranceActivated(true);
                    mActivity.GetController().GetUser().GetBoard().SetExitActivated(false);
                    mSetEntrance.setBackgroundColor(Color.parseColor("#C15823"));
                }
            }
        });
        this.mSetExit = getActivity().findViewById(R.id.set_exit);
        mSetExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetEntrance.setBackgroundColor(Color.BLACK);
                mActivity.GetController().ViewToReset(mSetExit);
                if (mActivity.GetController().GetUser() != null && mActivity.GetController().GetUser().GetBoard() != null) {
                    if (mActivity.GetController().GetUser().GetBoard().IsExitActivated()) {
                        mActivity.GetController().GetUser().GetBoard().SetExitActivated(false);
                        mSetExit.setBackgroundColor(Color.BLACK);
                    } else {
                        mActivity.GetController().GetUser().GetBoard().SetExitActivated(true);
                        mActivity.GetController().GetUser().GetBoard().SetEntranceActivated(false);
                        mSetExit.setBackgroundColor(Color.parseColor("#C15823"));
                    }
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




