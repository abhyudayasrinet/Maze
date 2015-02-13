package com.ggwp.maze;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RecordsLevelsFragment extends Fragment  {

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

	        View view = inflater.inflate(R.layout.records_levels_tab, container, false);

	        LevelsDB DB = new LevelsDB(getActivity());
			
			DB.openDataBase();
			int totalLevels = DB.getTotalLevels();
			DB.close();
			
			ListView levelsListView = (ListView) view.findViewById(R.id.recordsLevelsTab);
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
			
			RecordsLevelsListAdapter adapter = new RecordsLevelsListAdapter(getActivity(),R.layout.records_list_item, levels,time,moves);
	        levelsListView.setAdapter(adapter);
	        
	        return view;
	    }
	 
	 
	 	
}