package com.amieggs.datamanagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

public class HighscoreManager {

	private SharedPreferences highscorePreferences;
	
	public HighscoreManager(Context context) {
		highscorePreferences = context.getSharedPreferences("highscores", 0);
	}
	
	/**
	 * Get the last name introduced into the highscores
	 * @return the most recent username, of "" if lack of thereof.
	 */
	public String getCurrentName(){
		return highscorePreferences.getString("current_name", "Player 1");
	}
	
	public Pair<String,Integer> getValuesForPosition(int i){
		String name = highscorePreferences.getString("name"+i, "anonymous");
		Integer value = highscorePreferences.getInt("value" + i, 0);
		Pair<String,Integer> returnValue = new Pair<String,Integer>(name,value);
		return returnValue;
	}
	
	public int addHighscore(String name, Integer value){
		int i = 1;
		while(getValuesForPosition(i).second > value){
			++i;
		}
		addScoreToPosition(i,name,value);
		return i;
	}
	
	private void addScoreToPosition(int i, String name, Integer value){
		SharedPreferences.Editor prefEditor = highscorePreferences.edit();
		Pair<String,Integer> tmp = getValuesForPosition(i);
		int a = i;
		while (tmp.first != "anonymous"){
			++a;
			tmp = getValuesForPosition(a);
			prefEditor.putString("name"+a, highscorePreferences.getString("name"+(a-1), "anonymous"));
			prefEditor.putInt("value"+a, highscorePreferences.getInt("value"+(a-1), 0));
		}
		prefEditor.putString("name"+i, name);
		prefEditor.putInt("value"+i, value);
		prefEditor.putString("current_name", name);
		prefEditor.commit();
	}
}
