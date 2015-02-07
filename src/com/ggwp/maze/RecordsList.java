package com.ggwp.maze;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RecordsList extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.records);
		
		LevelsDB DB = new LevelsDB(getApplicationContext());
		
		DB.openDataBase();
		int totalLevels = DB.getTotalLevels();
		DB.close();
		
		ListView levelsListView = (ListView) findViewById(R.id.recordsListView);
		List<String> levels = new ArrayList<String>();
		List<String> time = new ArrayList<String>();
		List<String> moves = new ArrayList<String>();
		DB.openDataBase();
		for(int i=1;i<=totalLevels;i++) {

				levels.add(i+"");
				time.add(DB.getTimeTaken(i));
				moves.add(DB.getMoves(i)+"");
				
		}
		
		DB.close();
		
		RecordsListAdapter adapter = new RecordsListAdapter(this,R.layout.records_list_item, levels,time,moves);
        levelsListView.setAdapter(adapter);
		
        AdView adView = (AdView) this.findViewById(R.id.adViewRecords);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
        
	}

}