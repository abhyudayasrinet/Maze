package com.ggwp.maze;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class LevelsListAdapter extends ArrayAdapter<String>{

	
	Context context;
	List<String> levels;
	LevelsDB DB;
	public LevelsListAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.context=context;
		levels = objects;
		DB = new LevelsDB(context);
	}
	
	private class ViewHolder {
		TextView tv;
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		
		String level  =  getItem(position);
		
		LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        
		final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.levels_list_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.levelTV);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        
        holder.tv.setText(level);
        DB.openDataBase();
		if(DB.checkCompletion(Integer.parseInt(holder.tv.getText().toString())))
		{
			Log.d("Level completed",holder.tv.getText().toString());
			holder.tv.setBackgroundColor(Color.GREEN);
		}
		else {
			Log.d("Level not completed",holder.tv.getText().toString());
			holder.tv.setBackgroundColor(Color.TRANSPARENT);
		}
		DB.close();
			
        holder.tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((Activity)context).finish();
				Intent intent = new Intent(context, LevelActivity.class);
				intent.putExtra("level", Integer.parseInt(holder.tv.getText().toString()));
				((Activity)context).startActivity(intent);
				
			}
		});
		return convertView;
	}

}