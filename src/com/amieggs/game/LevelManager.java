package com.amieggs.game;

import android.content.Context;
import android.content.SharedPreferences;

public class LevelManager {
	
	private SharedPreferences levelPreferences;
	
	public LevelManager(Context context){
		levelPreferences = context.getSharedPreferences("levels", 0);
	}
	
	public int getCountersValueForLevel(int level){
		return levelPreferences.getInt("counterLevel"+level, 3);
	}
}
