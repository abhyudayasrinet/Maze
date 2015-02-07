package com.ggwp.maze;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class LevelActivity extends Activity {
	
	private int height;
	private int width;
	private int level;
	int cell_size;
	LinearLayout mainLayout,topMenu;
	Timer timer;
	TextView timerTV,moves;
	private int timeCounterSecs = 0;
	private int timeCounterMins = 0;
	private int timeCounterHours = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
       
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 1);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        
        //Parent layout
        mainLayout = new LinearLayout(this);
        mainLayout.setPadding(5, 5, 5, 5);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        
        //Menu bar with timer
        topMenu = new LinearLayout(this);
        topMenu.setOrientation(LinearLayout.HORIZONTAL);
        topMenu.setPadding(5, 5, 5, 5);
        mainLayout.addView(topMenu);
        
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, (float) 1.0);
        
        //Timer Ticker
        timerTV = new TextView(getApplicationContext());
        timerTV.setLayoutParams(param);
        timerTV.setTextColor(Color.BLACK);
        timerTV.setGravity(Gravity.CENTER);
        timerTV.setTextSize(20);
        timerTV.setText("00:00:00");
        timerTV.setPadding(10, 0, 10, 0);
        topMenu.addView(timerTV);
        
        
        //Moves Counter
        moves = new TextView(getApplicationContext());
        moves.setLayoutParams(param);
        moves.setTextColor(Color.BLACK);
        moves.setTextSize(20);
        moves.setGravity(Gravity.CENTER);
        moves.setPadding(10, 0, 10, 0);
        moves.setText("Moves Made : 0");
        topMenu.addView(moves);
        
        final LevelView view = new LevelView(this, height, width,moves,level);
        
        //timer to update the timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
	                public void run() {
	                	if(timeCounterSecs >= 60) {
	                		timeCounterSecs = 0;
	                		timeCounterMins++;
	                	}
	                	if(timeCounterMins >= 60) {
	                		timeCounterMins = 0;
	                		timeCounterHours++;
	                	}
	                	String ss = String.valueOf(timeCounterSecs);
	                	String mm = String.valueOf(timeCounterMins);
	                	String hh = String.valueOf(timeCounterHours);
	                	
	                	if(timeCounterSecs < 10)
	                		ss= "0"+ss;
	                	if(timeCounterMins < 10)
	                		mm= "0"+mm;
	                	if(timeCounterHours < 10)
	                		hh= "0"+hh;
	                	
	                    timerTV.setText(hh+":"+mm+":"+ss); 
	                    view.timeTaken = hh+":"+mm+":"+ss;
	                    timeCounterSecs++;
	                }
	            });
				
			}
		},100,1000);
        
        mainLayout.addView(view);
        setContentView(mainLayout);
        
	}
	

}