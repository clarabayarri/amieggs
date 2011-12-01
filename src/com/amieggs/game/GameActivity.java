package com.amieggs.game;

//import com.amieggs.game.GameView.GameThread;

import com.amieggs.datamanagers.GeneralDataManager;
import com.amieggs.datamanagers.HighscoreManager;
import com.amieggs.datamanagers.SelectedEggsManager;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener {
	
	private TimeThread timeThread;
	private int currentLevel = 1;
	private int currentScore = 0;
	
	private TextView levelLabel;
	private TextView scoreLabel;
	BoardView boardView;
	
	HighscoreManager hm;
	GeneralDataManager gdm;
	MediaPlayer mp = null;
	
	Dialog gameOverDialog = null;
	Dialog helpDialog = null;
	
	//Coses asíncrones
	Handler handler = new Handler();
	private Runnable updateLevelLabel = new Runnable() {
		public void run() {
			levelLabel.setText(String.valueOf(currentLevel));
	    }
    };
    private Runnable updateScoreLabel = new Runnable() {
		public void run() {
			scoreLabel.setText(String.valueOf(currentScore));
	    }
    };
    private Runnable showGameOverDialog = new Runnable() {
    	public void run() {
    		showDialog(DIALOG_GAMEOVER_ID);
    	}
    };
    private Runnable showHelpDialog = new Runnable() {
    	public void run() {
    		showDialog(DIALOG_HELP_ID);
    	}
    };
	
    //Identificadors pels diversos dialogs
	static final int DIALOG_HELP_ID = 0;
	static final int DIALOG_GAMEOVER_ID = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		
		boolean continuar = false;
		Bundle b  = getIntent().getExtras();
		if (b != null) {
			continuar = b.getBoolean("continue");
        }
		
		//afegim la custom view a ma per poder seguir fent servir el visualitzador de layout xml
        FrameLayout boardContainer = (FrameLayout)findViewById(R.id.board_container);
        boardView = new BoardView(this);
        boardContainer.addView(boardView);
        
        //li donem accés al tauler per actualitzar els contadors
        levelLabel = (TextView)findViewById(R.id.level_value_label);
        scoreLabel = (TextView)findViewById(R.id.score_value_label);
        
        //li donem accés als contadors
        TextView[] contadors = new TextView[]{(TextView)findViewById(R.id.remaining0),
        		(TextView)findViewById(R.id.remaining1),
        		(TextView)findViewById(R.id.remaining2),
        		(TextView)findViewById(R.id.remaining3),
        		(TextView)findViewById(R.id.remaining4),
        		(TextView)findViewById(R.id.remaining5)};
        boardView.setCountersAccess(contadors);
        
        hm = new HighscoreManager(this);
        
        SelectedEggsManager sem = new SelectedEggsManager(this);
        int[] selectedEggs = sem.getSelectedEggs();
        int[] bottomEggImages = new int[]{R.id.boardGameEggImage1, R.id.boardGameEggImage2,
        		R.id.boardGameEggImage3, R.id.boardGameEggImage4,
        		R.id.boardGameEggImage5, R.id.boardGameEggImage6};
        ImageView bottomEgg;
        for(int i = 0; i < selectedEggs.length; ++i){
        	bottomEgg = (ImageView)findViewById(bottomEggImages[i]);
	    	bottomEgg.setMaxWidth(boardView.getWidth()/6);
	    	bottomEgg.setMaxHeight(boardView.getWidth()/6);
	    	bottomEgg.setImageResource(selectedEggs[i]);
        }
        
        gdm = new GeneralDataManager(this);
        int remainingTime = 0;
        if(continuar && gdm.thereIsASavedState()){
        	currentLevel = gdm.getSavedStateLevel();
        	currentScore = gdm.getSavedStateScore();
        	remainingTime = gdm.getSavedStateTimeRemaining();
        	int[][] matrix = gdm.getSavedStateMatrix();
        	boardView.setMatrix(matrix);
        	handler.post(updateLevelLabel);
            handler.post(updateScoreLabel);
        }
        
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.timeBar);
        timeThread = new TimeThread(this, mProgress, gdm.getLevelMaxTime(currentLevel));
        if(remainingTime > 0){
        	timeThread.setTime(remainingTime);
        }
        timeThread.setRunning(true);
        timeThread.start();
        
        boardView.startLevel(currentLevel);
        
        if(gdm.musicShouldBeOn()){
        	mp = MediaPlayer.create(this, R.raw.u900_twist_and_shout);
            mp.setLooping(true);
            mp.start();
        }
        
    }
	
	public void restart() {
		FrameLayout boardContainer = (FrameLayout)findViewById(R.id.board_container);
		boardContainer.removeView(boardView);
		boardView = new BoardView(this);
		boardContainer.addView(boardView);
		
		//li donem accés als contadors
        TextView[] contadors = new TextView[]{(TextView)findViewById(R.id.remaining0),
        		(TextView)findViewById(R.id.remaining1),
        		(TextView)findViewById(R.id.remaining2),
        		(TextView)findViewById(R.id.remaining3),
        		(TextView)findViewById(R.id.remaining4),
        		(TextView)findViewById(R.id.remaining5)};
        boardView.setCountersAccess(contadors);
        
        currentLevel = 1;
        currentScore = 0;
        handler.post(updateLevelLabel);
        handler.post(updateScoreLabel);

        //timethread
        timeThread.setRunning(false);
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.timeBar);
        timeThread = new TimeThread(this, mProgress, gdm.getLevelMaxTime(currentLevel));
        timeThread.setRunning(true);
        timeThread.start();
        
        boardView.startLevel(currentLevel);
	}
	
	public void toggleMusic() {
		if(gdm.musicShouldBeOn()){
			if(mp == null){
				mp = MediaPlayer.create(this, R.raw.u900_twist_and_shout);
	            mp.setLooping(true);
			}
			mp.start();
		}
		else {
			if(mp != null) mp.pause();
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //timeThread.setRunning(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                restart();
                return true;
            case R.id.main_menu:
            	//matar la activity actual
            	finish();
            	return true;
            case R.id.toggle_music:
            	gdm.changeMusicTo(!gdm.musicShouldBeOn());
            	toggleMusic();
            	break;
            case R.id.help:
            	onHelp();
            	return true;
        }
        return false;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) mp.pause();
        timeThread.setRunning(false);
        
        //save state
        gdm.saveState(currentLevel, currentScore, timeThread.getTime(), boardView.getMatrix());
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	timeThread.setRunning(true);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mp != null) {
    		mp.stop();
    		mp.release();
    	}
    }
    
    @Override
    protected Dialog onCreateDialog (int id){
    	Dialog dialog = new Dialog(this, R.style.Custom_Clear_Dialog);
    	switch(id) {
    	case DIALOG_HELP_ID:
    		dialog.setTitle(R.string.help_label);
    		dialog.setContentView(R.layout.help);
    		View closeButton = dialog.findViewById(R.id.close_button);
    		closeButton.setOnClickListener(this);
    		helpDialog = dialog;
    		break;
        case DIALOG_GAMEOVER_ID:
        	dialog.setContentView(R.layout.gameover);
        	//butons del game over
            View restartButton = dialog.findViewById(R.id.restart_button);
            restartButton.setOnClickListener(this);
        	View quitButton = dialog.findViewById(R.id.quit_button);
        	quitButton.setOnClickListener(this);
        	gameOverDialog = dialog;
            break;
        default:
            
        }
        return dialog;
    }
    
    public void onNoMoreMoves() {
    	showNoMoreMovesToast();
    	boardView.changeBoard();
    }
    
    public void onLevelUp() {
    	showLevelUpToast();
    	
    	currentLevel += 1;
    	
    	timeThread.setRunning(false);
    	timeThread.resetTime(gdm.getLevelMaxTime(currentLevel));
    	handler.removeCallbacks(updateLevelLabel);
		handler.post(updateLevelLabel);
		timeThread.setRunning(true);
		boardView.startLevel(currentLevel);
    }
    
    public void updateScore(int elements){
    	currentScore += (elements*10);
    	handler.removeCallbacks(updateScoreLabel);
    	handler.post(updateScoreLabel);
    }
    
    private void showLevelUpToast() {
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		Toast levelUpToast = new Toast(this);
		View toastView = inflater.inflate(R.layout.levelup, null);
		levelUpToast.setView(toastView);
		levelUpToast.setDuration(700);
		levelUpToast.setGravity(Gravity.CENTER, 0, 0);
		levelUpToast.show();
    }
    
    private void showNoMoreMovesToast() {
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		Toast noMoreMovesToast = new Toast(this);
		View toastView = inflater.inflate(R.layout.nomoremoves, null);
		noMoreMovesToast.setView(toastView);
		noMoreMovesToast.setDuration(700);
		noMoreMovesToast.setGravity(Gravity.CENTER, 0, 0);
		noMoreMovesToast.setMargin(0.1f, 0.1f);
		noMoreMovesToast.show();
    }
    
    public void onTimeOut() {
    	hm.addHighscore(gdm.getUsername(), currentScore);
    	this.runOnUiThread(showGameOverDialog);
    }
    
    public void onHelp() {
    	this.runOnUiThread(showHelpDialog);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.restart_button:
			gameOverDialog.dismiss();
			gameOverDialog = null;
			restart();
			break;
		case R.id.quit_button:
			finish();
			break;
		case R.id.close_button:
			helpDialog.dismiss();
			break;
		}
	}

}
