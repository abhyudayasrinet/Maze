package com.ggwp.maze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;







import com.ggwp.maze.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class LevelsList extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.levels);
		
		LevelsDB DB = new LevelsDB(getApplicationContext());
		DB.openDataBase();
		int totalLevels = DB.getTotalLevels();
		DB.close();
		
		ListView levelsListView = (ListView) findViewById(R.id.levelsListView);
		List<String> levels = new ArrayList<String>();
		
		for(int i=1;i<=totalLevels;i++) {
			levels.add(""+i);
		}
		
		LevelsListAdapter adapter = new LevelsListAdapter(this,R.layout.levels_list_item, levels);
        levelsListView.setAdapter(adapter);
		
        AdView adView = (AdView) this.findViewById(R.id.adViewLevels);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
	}

}
