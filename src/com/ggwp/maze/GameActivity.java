package com.ggwp.maze;

import java.util.Timer;

import java.util.TimerTask;

//import com.google.android.gms.internal.db;










import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.PorterDuff;

public class GameActivity extends Activity {
	
	private int height;
	private int width;
	int mode;
	int level;
	int givenTime;
	int timeLeft;
	boolean stopTimer;
	
	int levelCounter = 0;
	
	LinearLayout topMenu,botMenu;
	RelativeLayout mainLayout;
	Button moveLeft, moveRight, moveUp, moveDown;
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
        		givenTime = 2*60;
        	}
        	else if(level >= 16 && level <= 20) {
        		givenTime = 5*60;
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
        		givenTime = 2*60;
        	}
        	else if(level == 4) {
        		givenTime = 5*60;
        	}
        	else if(level == 5) {
        		givenTime = 15*60;
        	}
        }
        else if(mode == -1)
        {
        	givenTime = -1;
        	
        }
        timeLeft = givenTime;
        
        //Parent layout
        mainLayout = new RelativeLayout(this);
        mainLayout.setPadding(5, 5, 5, 5);
        
        
        //Menu bar with timer
        topMenu = new LinearLayout(this);
        topMenu.setId(1);
        topMenu.setOrientation(LinearLayout.HORIZONTAL);
        topMenu.setPadding(5, 5, 5, 5);
        RelativeLayout.LayoutParams topMenuParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        topMenuParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        topMenuParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        topMenuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        topMenu.setLayoutParams(topMenuParams);
        
        if(mode!=-1)
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
        
        //Bottom Menu to hold controls
        botMenu = new LinearLayout(this);
        botMenu.setId(2);
        botMenu.setOrientation(LinearLayout.HORIZONTAL);
        botMenu.setPadding(5, 5, 5, 5);
        RelativeLayout.LayoutParams botMenuParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        botMenuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        botMenuParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        botMenuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        botMenu.setLayoutParams(botMenuParams);
        mainLayout.addView(botMenu);
        
        //create middle game view
        final GameView gameView = new GameView(this, height, width,moves,level,mode);
        RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if(mode!=-1)
        	gameViewParams.addRule(RelativeLayout.BELOW, topMenu.getId());
        else
        	gameViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gameViewParams.addRule(RelativeLayout.ABOVE, botMenu.getId());
        gameView.setLayoutParams(gameViewParams);
        
        
        //Create control buttons
        LinearLayout.LayoutParams controlParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        controlParams.weight = 1.0f;
        
        
        moveLeft = new Button(this);
        //moveLeft.setText("Left");
        moveLeft.setBackgroundResource(R.drawable.control_left);
        moveLeft.setLayoutParams(controlParams);
        moveLeft.setOnTouchListener(controlButtonTouch);
        moveLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.moveCell(3);
			}
		});
        botMenu.addView(moveLeft);
        
        moveUp = new Button(this);
        moveUp.setBackgroundResource(R.drawable.control_up);
        moveUp.setOnTouchListener(controlButtonTouch);
        moveUp.setLayoutParams(controlParams);
        moveUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.moveCell(0);
			}
		});
        botMenu.addView(moveUp);
        
        moveDown = new Button(this);
        moveDown.setBackgroundResource(R.drawable.control_down);
        moveDown.setOnTouchListener(controlButtonTouch);
        moveDown.setLayoutParams(controlParams);
        moveDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.moveCell(2);
			}
		});
        botMenu.addView(moveDown);
        
        moveRight = new Button(this);
        moveRight.setBackgroundResource(R.drawable.control_right);
        moveRight.setOnTouchListener(controlButtonTouch);
        moveRight.setLayoutParams(controlParams);
        moveRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.moveCell(1);
			}
		});
        botMenu.addView(moveRight);
                
       
        
        //timer to update the timer
        
        timer = new Timer();
        if(mode!=-1)
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
		                	
		                    gameView.timeTaken = hh+":"+mm+":"+ss;
		                    
		                    
		                    
		                    if(timeLeft == 0) {
		                    	
		                    	gameView.gameOver(1);
		                    }
	                	}
	                }
	            });
				
			}
		},100,1000);
       
        
        
        mainLayout.addView(gameView);
        setContentView(mainLayout);
        
        
        
        final TextView tv = moves;
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewGroup.MarginLayoutParams vlp = (MarginLayoutParams) tv.getLayoutParams();
                int btnsize = tv.getMeasuredHeight()+vlp.topMargin;
                gameView.setDimensions(btnsize);
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

	
	OnTouchListener controlButtonTouch = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                v.getBackground().setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                v.getBackground().clearColorFilter();
                v.invalidate();
                break;
            }
        }
			return false;
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer.purge();
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		timer.purge();
	}
}
