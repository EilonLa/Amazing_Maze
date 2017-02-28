package activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cdv.amazingmaze.R;

/**
 * Created by אילון on 26/01/2017.
 */

public class FindAMaze extends Activity {

    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_maze);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setQueryHint("Search for Mazes...");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                //contact fireBase to get a game here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}
