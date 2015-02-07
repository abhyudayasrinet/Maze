package com.ggwp.maze;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecordsListAdapter extends ArrayAdapter<String>{

	
	Context context;
	List<String> levels;
	List<String> timeTaken;
	List<String> movesMade;
	LevelsDB DB;
	public RecordsListAdapter(Context context, int resource, List<String> objects,List<String> timeTaken,List<String> moves) {
		super(context, resource, objects);
		this.context=context;
		levels = objects;
		this.timeTaken = timeTaken;
		this.movesMade = moves;
		DB = new LevelsDB(context);
	}
	
	private class ViewHolder {
		TextView recordsLevelTV;
		TextView recordsTimeTV;
		TextView recordsMovesTV;
	}
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		
		String level  =  getItem(position);
		String time = timeTaken.get(position);
		String moves = movesMade.get(position);
		
		ViewHolder holder = null;
		
		LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        
		
        if (convertView == null) {
        	
            convertView = mInflater.inflate(R.layout.records_list_item, null);
            holder = new ViewHolder();
            holder.recordsLevelTV = (TextView) convertView.findViewById(R.id.recordsLevelTV);
            holder.recordsTimeTV = (TextView) convertView.findViewById(R.id.recordsTimeTV);
            holder.recordsMovesTV = (TextView) convertView.findViewById(R.id.recordsMovesTV);
            convertView.setTag(holder);
            
        } else
            holder = (ViewHolder) convertView.getTag();
		
		holder.recordsLevelTV.setText("Level "+level);
		holder.recordsTimeTV.setText(time);
		holder.recordsMovesTV.setText(moves);
		
		return convertView;
	}

}