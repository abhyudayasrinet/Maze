package com.ggwp.maze;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecordsQuickGameListAdapter extends ArrayAdapter<GameStats>{

	
	Context context;
	List<GameStats> stats;
	LevelsDB DB;
	
	public RecordsQuickGameListAdapter(Context context, int resource, List<GameStats> objects) {
		super(context, resource, objects);
		this.context=context;
		this.stats = objects;
		DB = new LevelsDB(context);
	}
	
	private class ViewHolder {
		TextView levelTV;
		TextView gamesPlayedTV;
		TextView winsTV;
		TextView losesTV;
		TextView fastestWinTV;
	}
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		
		GameStats stat  =  getItem(position);
		
		
				
		ViewHolder holder = null;
		
		LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        
		
        if (convertView == null) {
        	
            convertView = mInflater.inflate(R.layout.records_quickgame_listitem, null);
            holder = new ViewHolder();
            holder.levelTV = (TextView) convertView.findViewById(R.id.recordsQGLevelTV);
            holder.gamesPlayedTV = (TextView) convertView.findViewById(R.id.recordsQGgamesPlayedTV);
            holder.winsTV = (TextView) convertView.findViewById(R.id.recordsQGwinsTV);
            holder.losesTV = (TextView) convertView.findViewById(R.id.recordsQGlosesTV);
            holder.fastestWinTV = (TextView) convertView.findViewById(R.id.recordsQGfastestTimeTV);
            convertView.setTag(holder);
            
        } else
            holder = (ViewHolder) convertView.getTag();
		
        if(position == 0)
        	holder.levelTV.setText("Sandbox");
        else if(position == 1)
        	holder.levelTV.setText("Normal");
        else if(position == 2)
        	holder.levelTV.setText("Hard");
        else if(position == 3)
        	holder.levelTV.setText("Insane");
        else if(position == 4)
        	holder.levelTV.setText("Hell");
        
        holder.gamesPlayedTV.setText(stats.get(position).gamesPlayed+"");
        holder.winsTV.setText(stats.get(position).totalWins+"");
        holder.losesTV.setText(stats.get(position).totalLoses+"");
        holder.fastestWinTV.setText(stats.get(position).fastestTime+"");
		
		
		return convertView;
	}

}