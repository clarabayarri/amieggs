package com.amieggs.settings;

import com.amieggs.datamanagers.SelectedEggsManager;
import com.amieggs.game.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private int[] selectedEggs = new int[6];
	private int[] mThumbIds;
	
    public ImageAdapter(Context c) {
        mContext = c;
        SelectedEggsManager sem = new SelectedEggsManager(c);
        selectedEggs = sem.getSelectedEggs();
        mThumbIds = sem.getAllEggs();
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        for(int i = 0; i < selectedEggs.length; ++i){
        	if(selectedEggs[i] == mThumbIds[position]){
        		imageView.setBackgroundResource(R.drawable.greyout);
            	imageView.setFocusable(false);
        	}
        }
        return imageView;
    }

}
