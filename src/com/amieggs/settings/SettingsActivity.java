package com.amieggs.settings;

import com.amieggs.datamanagers.GeneralDataManager;
import com.amieggs.datamanagers.SelectedEggsManager;
import com.amieggs.game.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingsActivity extends Activity implements OnClickListener {

	int[] buttons = {R.id.chosenEggButton1, R.id.chosenEggButton2, R.id.chosenEggButton3, R.id.chosenEggButton4, R.id.chosenEggButton5, R.id.chosenEggButton6};
	
	private int[] mThumbIds;
	
	SelectedEggsManager sem;
	GeneralDataManager gdm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings);

	    Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.difficulties_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    
	    View backArrow = findViewById(R.id.backArrow);
	    backArrow.setOnClickListener(this);
	    
	    gdm = new GeneralDataManager(this);
	    
	    EditText usernameBox = (EditText)findViewById(R.id.username_box);
	    usernameBox.setText(gdm.getUsername());
	    usernameBox.setOnEditorActionListener(new MyEditorActionListener());
	    
	    sem = new SelectedEggsManager(this);
	    mThumbIds = sem.getAllEggs();
	    int[] selectedEggs = sem.getSelectedEggs();
	    ImageButton chosenEggsButton;
	    for (int i = 0; i < selectedEggs.length; ++i){
	    	chosenEggsButton = (ImageButton)findViewById(buttons[i]);
	    	chosenEggsButton.setImageResource(selectedEggs[i]);
	        chosenEggsButton.setOnClickListener(this);
	    }
	    
	    RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
	    if(gdm.musicShouldBeOn()){
	    	rg.check(R.id.music_on);
	    }
	    else rg.check(R.id.music_off);
	    rg.setOnCheckedChangeListener(new MyOnCheckChangeListener());
	    
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backArrow:
			finish();
			break;
		}
		
		for(int i = 0; i < buttons.length; ++i){
			if(buttons[i] == v.getId()){
				Intent eggSelectionIntent = new Intent(this, ChooseEggsActivity.class);
				startActivityForResult(eggSelectionIntent,i);
				break;
			}
		}
		
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		ImageButton chosenEggsButton = (ImageButton)findViewById(buttons[requestCode]);
		chosenEggsButton.setImageDrawable(getResources().getDrawable(mThumbIds[resultCode]));
		sem.setDrawableIdForIndex(requestCode, mThumbIds[resultCode]);
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			gdm.setDifficulty(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class MyOnCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			gdm.changeMusicTo(checkedId == R.id.music_on);
		}
		
	}
	
	public class MyEditorActionListener implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			gdm.setUsername(v.getText().toString());
			return false;
		}
		
	}
}
