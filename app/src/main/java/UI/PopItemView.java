package UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.cdv.amazingmaze.R;

import java.util.ArrayList;

/**
 * Created by אילון on 30/01/2017.
 */

public class PopItemView extends ArrayAdapter {
    private int mCount = 1;
    private FontCreator_Logo mDescription;
    private FontCreator_Logo mName;
    private FontCreator_Logo mCountView;
    private ImageView mIcon;
    private String mNameText;
    private ArrayList<Object[]> mData;
    private static LayoutInflater mInflater = null;


    public PopItemView(Context context,int ResourceId, ArrayList<Object[]> data/*, int iconId,String mName, String mDescription, int mCount*/) {
        super(context,ResourceId);

        Log.i("mData size",""+data.size());
        this.mData = data;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public void IncrementCount(){
        mCount++;
        mCountView.setText("X"+ mCount);
    }

    public String GetName(){
        return this.mNameText;
    }

    @Override
    public int getCount() {
        if (mData != null && !mData.isEmpty() && mData.get(0) != null)
            return mData.size();
        else
            return 0;
    }

    public void RemoveView(int index){
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
        int size = mData.size();
            if (vi == null)
                vi = mInflater.inflate(R.layout.listview_row, null);
        if (size >  i ){
            mName = (FontCreator_Logo) vi.findViewById(R.id.name_list);
            this.mName.setText(mData.get(i)[1].toString());
            this.mIcon = (ImageView) vi.findViewById(R.id.icon_list);
            mIcon.setBackgroundResource(Integer.parseInt(mData.get(i)[0].toString()));
            mCountView = (FontCreator_Logo) vi.findViewById(R.id.count_list);
            mCountView.setText("X" + mData.get(i)[3].toString());
            this.mDescription = (FontCreator_Logo) vi.findViewById(R.id.descreption_list);
            this.mDescription.setText(mData.get(i)[2].toString());
        }
        return vi;

    }
}
