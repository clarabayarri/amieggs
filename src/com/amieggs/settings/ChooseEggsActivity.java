package com.amieggs.settings;

import com.amieggs.datamanagers.SelectedEggsManager;
import com.amieggs.game.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ChooseEggsActivity extends Activity  implements OnItemClickListener{
	
	int[] selectedEggs = new int[6];
	int[] allEggs;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.customeggs);

	    SelectedEggsManager sem = new SelectedEggsManager(this);
	    selectedEggs = sem.getSelectedEggs();
	    allEggs = sem.getAllEggs();
	    GridView gridview = (GridView) findViewById(R.id.customEggsGridView);
	    gridview.setAdapter(new ImageAdapter(this));

	    gridview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		boolean isSelected = false;
		for(int i = 0; i < selectedEggs.length; ++i){
			if(selectedEggs[i] == allEggs[position]){
				isSelected = true;
			}
		}
		if(!isSelected){
			setResult(position);
			finish();
		}
		else {
			showRepeatedEggToast();
		}
	}
	
	private void showRepeatedEggToast() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View toastView = inflater.inflate(R.layout.repeatedegg, null);
		Toast repeatedEggToast = new Toast(this);
		repeatedEggToast.setGravity(Gravity.CENTER, 0, 0);
		repeatedEggToast.setView(toastView);
		repeatedEggToast.setDuration(500);
		repeatedEggToast.show();
	}
}
