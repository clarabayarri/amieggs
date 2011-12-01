package com.amieggs.datamanagers;

import com.amieggs.game.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SelectedEggsManager {

	private SharedPreferences selectedEggsPreferences;
	int[] defaultEggs = {R.drawable.egg_bear, R.drawable.egg_rooster, R.drawable.egg_corncob, R.drawable.egg_dog, R.drawable.egg_elephant, R.drawable.egg_penguin};

	int[] allEggs = {
			R.drawable.egg_banana, R.drawable.egg_bear,
	        R.drawable.egg_bee, R.drawable.egg_blackcat,
	        R.drawable.egg_bunny, R.drawable.egg_cactus,
	        R.drawable.egg_carrot, R.drawable.egg_cat,
	        R.drawable.egg_chick, R.drawable.egg_corncob,
	        R.drawable.egg_dog, R.drawable.egg_eggplant,
	        R.drawable.egg_elephant, R.drawable.egg_fox,
	        R.drawable.egg_frog, R.drawable.egg_koala,
	        R.drawable.egg_monkey, R.drawable.egg_mouse,
	        R.drawable.egg_penguin, R.drawable.egg_pig,
	        R.drawable.egg_present,
	        R.drawable.egg_rooster, R.drawable.egg_rudolph,
	        R.drawable.egg_santa, R.drawable.egg_shortcake,
	        R.drawable.egg_skunk, R.drawable.egg_snowman,
	        R.drawable.egg_strawberry, R.drawable.egg_turkey,
	        R.drawable.egg_whale, R.drawable.egg_xmastree
	};
	
	public SelectedEggsManager(Context context){
		selectedEggsPreferences = context.getSharedPreferences("selectedEggs", 0);
	}
	
	public int[] getSelectedEggs(){
		int[] selection = new int[6];
		for (int i = 0; i < 6; ++i){
	    	selection[i] = selectedEggsPreferences.getInt(String.valueOf(i), defaultEggs[i]);
		}
		return selection;
	}
	
	public int[] getAllEggs(){
		return allEggs;
	}
	
	public void setDrawableIdForIndex(int index, int id){
		SharedPreferences.Editor prefEditor = selectedEggsPreferences.edit();
		prefEditor.putInt(String.valueOf(index), id);
		prefEditor.commit();
	}
}
