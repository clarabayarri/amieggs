package com.amieggs.game;

import com.amieggs.settings.SettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AmieggsActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // click-handlers for buttons:
        View newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);
        View continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        View settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);
        View highscoreButton = findViewById(R.id.highscores_button);
        highscoreButton.setOnClickListener(this);
        View aboutButton = findViewById(R.id.clara);
        aboutButton.setOnClickListener(this);
        View etsyButton = findViewById(R.id.etsy);
        etsyButton.setOnClickListener(this);
        View facebookButton = findViewById(R.id.facebook);
        facebookButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.new_game_button:
			Intent newGameIntent = new Intent(this, GameActivity.class);
			Bundle b1 = new Bundle();
			b1.putBoolean("continue", false); //Your id
			newGameIntent.putExtras(b1);
			startActivity(newGameIntent);
			break;
		case R.id.continue_button:
			Intent continueGameIntent = new Intent(this, GameActivity.class);
			Bundle b2 = new Bundle();
			b2.putBoolean("continue", true);
			continueGameIntent.putExtras(b2);
			startActivity(continueGameIntent);
			break;
		case R.id.settings_button:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			break;
		case R.id.highscores_button:
			Intent highscoreIntent = new Intent(this, HighscoresActivity.class);
			startActivity(highscoreIntent);
			break;
		case R.id.clara:
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
			break;
		case R.id.etsy:
			Intent etsyBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nayami.etsy.com"));
			startActivity(etsyBrowserIntent);
			break;
		case R.id.facebook:
			Intent fbBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/amieggs"));
			startActivity(fbBrowserIntent);
			break;
		}
	}
}