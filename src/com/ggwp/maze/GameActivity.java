package com.ggwp.maze;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.internal.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	
	private int height;
	private int width;
	int mode;
	int level;
	int givenTime;
	int timeLeft;
	boolean stopTimer;
	
	
	int levelCounter = 0;
	
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
        level = intent.getIntExtra("level",2);
        mode = intent.getIntExtra("mode",0);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        stopTimer = false;
        
        if(mode == 1) {
        	
        	if(level >= 1 && level <= 5) {
        		givenTime = 30;
        	}
        	else if(level >= 6 && level <= 10) {
        		givenTime = 45;
        	}
        	else if(level >= 11 && level <= 15) {
        		givenTime = 90;
        	}
        	else if(level >= 16 && level <= 20) {
        		givenTime = 300;
        	}
        	else if(level >= 20 && level <= 25) {
        		givenTime = 15*60;
        	}
        }
        else if(mode == 0) {
        	if(level == 1) {
        		givenTime = 30;
        	}
        	else if(level == 2) {
        		givenTime = 45;
        	}
        	else if(level == 3) {
        		givenTime = 90;
        	}
        	else if(level == 4) {
        		givenTime = 300;
        	}
        	else if(level == 5) {
        		givenTime = 15*60;
        	}
        }
        timeLeft = givenTime;
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
        
        final GameView view = new GameView(this, height, width,moves,level,mode);
        
        //timer to update the timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
	                public void run() {
	                	
	                	if(!stopTimer) {
	                		
	                		timeLeft--;
	                		
		                	timeCounterSecs = timeLeft%60;
		                	timeCounterMins = timeLeft/60;
		                	timeCounterHours = timeLeft/(60*60);
		                	
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
		                    
		                    
		                    int elapsedTime = givenTime - timeLeft;
		                    
		                    timeCounterSecs = elapsedTime%60;
		                	timeCounterMins = elapsedTime/60;
		                	timeCounterHours = elapsedTime/(60*60);
		                	
		                	ss = String.valueOf(timeCounterSecs);
		                	mm = String.valueOf(timeCounterMins);
		                	hh = String.valueOf(timeCounterHours);
		                	
		                    view.timeTaken = hh+":"+mm+":"+ss;
		                    
		                    
		                    
		                    if(timeLeft == 0) {
		                    	
		                    	view.gameOver(1);
		                    }
	                	}
	                }
	            });
				
			}
		},100,1000);
        
        mainLayout.addView(view);
        setContentView(mainLayout);
        
        
        
        final TextView tv = moves;
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewGroup.MarginLayoutParams vlp = (MarginLayoutParams) tv.getLayoutParams();
                int btnsize =tv.getMeasuredHeight()+vlp.topMargin;
                view.setDimensions(btnsize);
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if(!hasBackKey) {	
    
			mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        	
		}
	}
	
}
