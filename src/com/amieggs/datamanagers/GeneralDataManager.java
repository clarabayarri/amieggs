package com.amieggs.datamanagers;

import android.content.Context;
import android.content.SharedPreferences;

public class GeneralDataManager {

	private SharedPreferences generalPreferences;
	private SharedPreferences statePreferences;
	
	public GeneralDataManager(Context context){
		generalPreferences = context.getSharedPreferences("general", 0);
		statePreferences = context.getSharedPreferences("state", 0);
	}
	
	public boolean musicShouldBeOn() {
		return generalPreferences.getBoolean("music", true);
	}
	
	public void changeMusicTo(boolean on){
		SharedPreferences.Editor prefEditor = generalPreferences.edit();
		prefEditor.putBoolean("music", on);
		prefEditor.commit();
	}
	
	public int getDifficulty() {
		return generalPreferences.getInt("difficulty", 1);
	}
	
	public void setDifficulty(int difficulty) {
		SharedPreferences.Editor prefEditor = generalPreferences.edit();
		prefEditor.putInt("difficulty", difficulty+1);
		prefEditor.commit();
	}
	
	public String getUsername() {
		return generalPreferences.getString("name", "Player 1");
	}
	
	public void setUsername(String username) {
		SharedPreferences.Editor prefEditor = generalPreferences.edit();
		prefEditor.putString("name", username);
		prefEditor.commit();
	}
	
	public int getLevelMaxTime(int level) {
		int time = 100;
		for(int i = 1; i < level; ++i) time *= 0.8;
		time /= getDifficulty();
		return time;
	}
	
	public void saveState(int level, int score, int timeRemaining, int[][] matrix) {
		SharedPreferences.Editor prefEditor = statePreferences.edit();
		prefEditor.putBoolean("saved", true);
		prefEditor.putInt("level", level);
		prefEditor.putInt("score", score);
		prefEditor.putInt("timeRemaining", timeRemaining);
		for(int i = 0; i < 7; ++i){
			for(int j = 0; j < 6; ++j){
				prefEditor.putInt(String.valueOf(i) + String.valueOf(j), matrix[i][j]);
			}
		}
		prefEditor.commit();
	}
	
	public boolean thereIsASavedState() {
		return statePreferences.getBoolean("saved", false);
	}
	
	public int getSavedStateLevel() {
		return statePreferences.getInt("level", 1);
	}
	
	public int getSavedStateScore() {
		return statePreferences.getInt("score", 0);
	}
	
	public int getSavedStateTimeRemaining() {
		return statePreferences.getInt("timeRemaining", getLevelMaxTime(1));
	}
	
	public int[][] getSavedStateMatrix() {
		int[][] matrix = new int[7][6];
		for(int i = 0; i < 7; ++i){
			for(int j = 0; j < 6; ++j){
				matrix[i][j] = statePreferences.getInt(String.valueOf(i) + String.valueOf(j), 0);
			}
		}
		SharedPreferences.Editor prefEditor = statePreferences.edit();
		prefEditor.putBoolean("saved", false);
		prefEditor.commit();
		return matrix;
	}
}
