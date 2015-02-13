package com.ggwp.maze;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecordsQuickGameFragment extends Fragment  {

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	        
	        View view = inflater.inflate(R.layout.records_quickgame_tab, container, false);	       
	        
	        LevelsDB DB = new LevelsDB(getActivity());
			
			DB.openDataBase();
			
			ListView quickGameStatsLV = (ListView) view.findViewById(R.id.recordsQGListView);
			
			List<GameStats> stats = new ArrayList<GameStats>();
			
			DB.openDataBase();
			
			for(int i = 1 ; i <= 5 ; i++) {

				GameStats stat = DB.getQuickGameStats(i);
//				Log.d("stats level "+i,stat.toString());
				stats.add(stat);
					
			}
			
			DB.close();
			
			RecordsQuickGameListAdapter adapter = new RecordsQuickGameListAdapter(getActivity(),R.layout.records_quickgame_listitem, stats);
	        quickGameStatsLV.setAdapter(adapter);
	        
	        
	        return view;
	    }
}