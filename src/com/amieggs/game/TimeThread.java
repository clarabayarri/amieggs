package com.amieggs.game;

import android.widget.ProgressBar;

public class TimeThread extends Thread {

	private boolean _running = false;
	ProgressBar _timeBar;
	int _currentValue;
	GameActivity _main;
	
	public TimeThread(GameActivity activity, ProgressBar timeBar, int maxValue){
		_timeBar = timeBar;
		_main = activity;
		_currentValue = maxValue;
		_timeBar.setMax(maxValue);
		_timeBar.setProgress(_currentValue);
	}
    
	public void setRunning(boolean run) {
        _running = run;
    }
	
	public void setTime(int time) {
		_currentValue = time;
	}
	
	public int getTime() {
		return _currentValue;
	}
	
	public void resetTime(int maxValue){
		_timeBar.setMax(maxValue);
		_currentValue = maxValue;
	}
	@Override
    public void run() {
    	long ticksPS = 1000;
        long startTime;
        long sleepTime;
        while (_running) {
        	startTime = System.currentTimeMillis();
        	--_currentValue;
        	_timeBar.setProgress(_currentValue);
        	if(_currentValue == 0){
        		_running = false;
        		_main.onTimeOut();
        	}
        	sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                   if (sleepTime > 0)
                          sleep(sleepTime);
                   else
                          sleep(10);
            } catch (Exception e) {}
        }
	}
}
