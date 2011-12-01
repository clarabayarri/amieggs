package com.amieggs.game;

import com.amieggs.datamanagers.HighscoreManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HighscoresActivity extends Activity implements OnClickListener {

	private TextView[] name_labels;
	private TextView[] value_labels = new TextView[5];
	private String[] names = new String[5];
	private String[] values = new String[5];
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
        
        name_labels = new TextView[]{(TextView)findViewById(R.id.position1name),
        		(TextView)findViewById(R.id.position2name),
        		(TextView)findViewById(R.id.position3name),
        		(TextView)findViewById(R.id.position4name),
        		(TextView)findViewById(R.id.position5name)};
        
        value_labels = new TextView[]{(TextView)findViewById(R.id.position1value),
        		(TextView)findViewById(R.id.position2value),
        		(TextView)findViewById(R.id.position3value),
        		(TextView)findViewById(R.id.position4value),
        		(TextView)findViewById(R.id.position5value)};
        
        HighscoreManager manager = new HighscoreManager(this);
        Pair<String,Integer> result;
        for(int i = 1; i <= 5; ++i){
        	result = manager.getValuesForPosition(i);
        	names[i-1] = result.first;
        	values[i-1] = result.second.toString();
        }
        
        Runnable displayHighscores = new Runnable() {
    		public void run() {
    			for(int i = 0; i < 5; ++i){
    				name_labels[i].setText(names[i]);
    				value_labels[i].setText(values[i]);
    			}
    	    }
        };
        
        View backArrow = findViewById(R.id.highscores_back_arrow);
	    backArrow.setOnClickListener(this);
	    
        Handler handler = new Handler();
        handler.post(displayHighscores);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.highscores_back_arrow:
			finish();
			break;
		}
	}
}
