package UI.Fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

import activities.MainActivity;

/**
 * Created by Eilon Laor & Dvir Twina on 06/02/2017.
 *
 * The FindAMaze fragment is inflated when the user wants to play and he needs to search for a maze
 *
 */

public class FindAMaze extends Fragment {
    private static final String NO_BOARD_FOR_RIVAL_ERROR = "The requested user is not ready with his Maze!";
    private static final String OWN_MAZE_ERROR = "You can't play your own Maze!";
    private SearchView mSearchView;
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_maze, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mSearchView = (SearchView) mActivity.findViewById(R.id.searchView);
        mSearchView.setQueryHint("Search for Mazes...");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.GetController().GetOptionalUsers().clear();
                        mActivity.GetController().IsWaitingForFireBase(true);
                        mActivity.GetFireBaseOperator().GetOptionalUsersForGame(query);
                        while (mActivity.GetController().IsWaitingForFireBase()) {
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                QueryResultListFragment q = new QueryResultListFragment();
                                mActivity.getFragmentManager().beginTransaction().add(R.id.searchResults, q).addToBackStack(null).commit();
                            }
                        });
                        //mActivity.onBackPressed();
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(mActivity.getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });


    }



    public static class QueryResultListFragment extends ListFragment implements AdapterView.OnItemClickListener {
        private RivalViewAdapter mAdapter;
        private MainActivity mActivity;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.optional_rivals_list, container, false);
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mActivity = (MainActivity) getActivity();
            SetListOfItems();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final String userName = getListView().getAdapter().getItem(i).toString();
            if (userName != null && !userName.equals(mActivity.GetController().GetUser().GetUserName())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.GetController().IsWaitingForFireBase(true);
                        mActivity.GetFireBaseOperator().GetRivalUser(userName);
                        while (mActivity.GetController().IsWaitingForFireBase()) {
                        }
                        if (mActivity.GetController().GetDoesRivalHasBoard()) {
                            mActivity.GetController().SetIsSearching(false);
                            mActivity.getFragmentManager().beginTransaction().remove(mActivity.getFragmentManager().findFragmentById(R.id.container_find)).addToBackStack(null).commit();
                            mActivity.getFragmentManager().beginTransaction().add(R.id.container_board, new GamePlay()).addToBackStack(null).commit();
                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity,NO_BOARD_FOR_RIVAL_ERROR,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
            if (userName != null && userName.equals(mActivity.GetController().GetUser().GetUserName())){
                Toast.makeText(mActivity,OWN_MAZE_ERROR,Toast.LENGTH_LONG).show();
            }
        }



        public void SetListOfItems() {
            mAdapter = new RivalViewAdapter(getActivity(), R.layout.items_list, mActivity.GetController().GetOptionalUsers());
            setListAdapter(mAdapter);
            getListView().setOnItemClickListener(this);
        }
    }

    public static class RivalViewAdapter extends ArrayAdapter {
        private int mCount = 1;
        private String mNameText;
        private TextView mName;
        private ArrayList<String> mData;
        private static LayoutInflater mInflater = null;


        public RivalViewAdapter(Context context, int ResourceId, ArrayList<String> data/*, int iconId,String mName, String mDescription, int mCount*/) {
            super(context, ResourceId);
            Log.i("mData size", "" + data.size());
            this.mData = data;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public String GetName() {
            return this.mNameText;
        }

        @Override
        public int getCount() {
            if (mData != null && !mData.isEmpty() && mData.get(0) != null)
                return mData.size();
            else
                return 0;
        }

        public void RemoveView(int index) {
            if (mData.size() > index) {
                mData.remove(index);
                notifyDataSetChanged();
            }
        }

        @Override
        public Object getItem(int i) {
            if (mData != null && mData.get(i) != null)
                return mData.get(i);
            else
                return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi = view;
            try {
                if (vi == null) {
                    vi = mInflater.inflate(R.layout.rival_row_list, null);
                }
                if (mData.size() > 0) {
                    mName = (TextView) vi.findViewById(R.id.name_rival);
                    if (mData.get(i) != null) {
                        mName.setText(mData.get(i));
                    }
                }
                return vi;
            } catch (Exception e) {
                return vi = mInflater.inflate(R.layout.rival_row_list, null);
            }
        }
    }


}
