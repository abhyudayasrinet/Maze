package com.ggwp.maze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends View {

	Context context;	
	int rows;
	int columns;
	int cellSize;
	int level;
	int height;
	int width;
	int moveCount;
	int maxMoves;
	Paint paint;
	MazeGenerator mazeGenerator;
	String timeTaken;
	TextView moves;
	LevelsDB DB;
	int gameMode;
	int currentX;
	int currentY;
	
	/*
	 * store x and y coordinates of top left corner
	 * and if the maze has been touched or not
	 */
	int mazeX[][];
	int mazeY[][];
	boolean mazeColor[][];
	
	/*
	 * Variables related to zooming and panning on the canvas
	 * link : http://vivin.net/2011/12/04/implementing-pinch-zoom-and-pandrag-in-an-android-view-on-the-canvas/
	 */
	private static float MIN_ZOOM = 1f;
	private static float MAX_ZOOM = 5f;
	private float scaleFactor = 1.f;
	private ScaleGestureDetector detector;
	
	private static int NONE = 0;
	private static int DRAG = 1;
	private static int ZOOM = 2;
	private int mode;
	
	private float startX = 0f;
	private float startY = 0f;
	
	private float translateX = 0f;
	private float translateY = 0f;
	
	private float previousTranslateX = 0f;
	private float previousTranslateY = 0f;
	
	private boolean dragged = true;
	
	private boolean screenTouched = false;
	
	final Handler longPressHandler;
	Runnable longPressRunnable;
	boolean longPressed;
	boolean GameOver;
	
	public GameView(Context context,int height,int width,TextView moves,
			int level,int mode) {
		super(context);
		
		this.context = context;
		this.height = height;
		this.width = width;
		this.moves = moves;
		this.level = level;
		this.gameMode = mode;
		longPressHandler = new Handler();
		
		longPressRunnable = new Runnable() {
			@Override
			public void run() {
				longPressed = true;
			}
		};
		
		GameOver = false;
		DB = new LevelsDB(context);	
		
		paint = new Paint();
		
	}
	
	public void setDimensions(int TextViewHeight) {
		
		TextViewHeight += 20;
		height -= TextViewHeight;
	//	Log.d("Height, Width", height + "," + width);
		intializeValues();
		
	}
	
	public void intializeValues() {
		
		if(gameMode == 0) {
			
			if(level == 1) {
				rows = 10;
				columns= 5;
			}
			else if(level == 2) {
				rows = 20;
				columns= 10;
			}
			else if(level == 3) {
				rows = 40;
				columns= 20;
			}
			else if(level == 4) {
				rows = 80;
				columns= 40;
			}
			else if(level == 5) {
				rows = 100;
				columns= 50;
			}
			
			this.cellSize = Math.min( height/rows, width/columns);
			
			mazeGenerator = new MazeGenerator(rows,columns);
			mazeGenerator.createMazeKruskals();
			mazeGenerator.getDestinationPoints();
			
			
		}
		else if(gameMode == 1) {
			
			DB.openDataBase();
			mazeGenerator = DB.getMaze(level);
			DB.close();
			
			rows = mazeGenerator.rows;
	        columns = mazeGenerator.columns;
			
			cellSize = Math.min((width-20)/columns,(height-20)/rows);
			
			
		}
//		
//		Log.d("cellSize",this.cellSize+"");
//		Log.d("heightxwidth",height+"x"+width);
//		Log.d("rowsxcolumns",rows+"x"+columns);
		
		mazeX = new int[rows][columns];
		mazeY = new int[rows][columns];
		mazeColor = new boolean[rows][columns];
		
		createMazeCoordinates();
		
		moveCount = (int) Math.min(Math.ceil(mazeGenerator.max_distance+(0.25*mazeGenerator.max_distance)),(rows*columns)-1);
		maxMoves = moveCount;
		moves.setText("Moves Left : "+moveCount);
	}
	
	//assign coordinates of top left corner of each cell
	public void createMazeCoordinates() {
		
		int start_x = (width - columns*cellSize)/2; 
		int x = start_x; //starting x coord top left
		int y = 0; //starting y coord top left
		
		for(int i = 0 ; i < rows ; i++) {
			
			for(int j = 0 ; j < columns ; j++) {
				mazeX[i][j] = x;
				mazeY[i][j] = y;
				mazeColor[i][j] = false;
				x+=cellSize;
				//Log.d(i+","+j,x+","+y);
			}
			y += cellSize;
			x = start_x;
		}
		
		mazeColor[0][0] = true; 
		currentX = 0;
		currentY = 0;
	}

	
	//Where all the drawing happens
	@Override
	protected void onDraw(Canvas canvas) {
		
		
		super.onDraw(canvas);
		
		
		
		canvas.save();
		canvas.scale(scaleFactor, scaleFactor);//,this.detector.getFocusX(),this.detector.getFocusY());
		
		if((translateX * -1) < 0) { //left bound
			translateX = 0;       
		}
		else if((translateX * -1) > (scaleFactor - 1) * width) { //right bound
		    translateX = (1 - scaleFactor) * width;
		}
		
		if(translateY * -1 < 0) {//top bound
			translateY = 0;
		}		
		else if((translateY * -1) > (scaleFactor - 1) * height) { //bottom bound
			translateY = (1 - scaleFactor) * height;
		}

		
		canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
		
		paint.setColor(Color.BLACK);

		for(int i=0;i<rows;i++) {
			
			for(int j=0;j<columns;j++) {
				
				//if cell has been touched color it green
				if(mazeColor[i][j]) {
					paint.setColor(Color.GREEN);
					paint.setStyle(Paint.Style.FILL);
					canvas.drawRect(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cellSize, mazeY[i][j]+cellSize, paint);
				}
				
				//if cell is destination color it red
				if(i == mazeGenerator.dest_row && j == mazeGenerator.dest_col) {
					paint.setColor(Color.RED);
					canvas.drawRect(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cellSize, mazeY[i][j]+cellSize, paint);
				}

				paint.setColor(Color.BLACK);
				paint.setStrokeWidth(3);
				
				if(mazeGenerator.walls[i][j][0])
					canvas.drawLine(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cellSize, mazeY[i][j], paint);
				if(mazeGenerator.walls[i][j][1])
					canvas.drawLine(mazeX[i][j]+cellSize, mazeY[i][j], mazeX[i][j]+cellSize, mazeY[i][j]+cellSize, paint);
				if(mazeGenerator.walls[i][j][2])
					canvas.drawLine(mazeX[i][j], mazeY[i][j]+cellSize, mazeX[i][j]+cellSize, mazeY[i][j]+cellSize, paint);
				if(mazeGenerator.walls[i][j][3])
					canvas.drawLine(mazeX[i][j], mazeY[i][j], mazeX[i][j], mazeY[i][j]+cellSize, paint);

			}
		}		
		canvas.restore();
	}
	
	
	//handles panning and zooming and touch detection to mark cells
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(GameOver)
			return true;
		
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
			float distFromTop = event.getY();
			float distFromBot = height - event.getY();
			float distFromLeft = event.getX();
			float distFromRight = width - event.getX();
			
//			Log.d("distFromTop" , distFromTop+"");
//			Log.d("distFromLeft" , distFromLeft+"");
//			Log.d("distFromRight" , distFromRight+"");
//			Log.d("distFromBot" , distFromBot+"");
			
			if(distFromTop ==  Math.min(distFromTop, Math.min(distFromBot, Math.min(distFromLeft, distFromRight))) )
				moveCell(0);
			
			if(distFromBot ==  Math.min(distFromTop, Math.min(distFromBot, Math.min(distFromLeft, distFromRight))) )
				moveCell(2);
			
			if(distFromLeft ==  Math.min(distFromTop, Math.min(distFromBot, Math.min(distFromLeft, distFromRight))) )
				moveCell(3);
			
			if(distFromRight ==  Math.min(distFromTop, Math.min(distFromBot, Math.min(distFromLeft, distFromRight))) )
				moveCell(1);
			break;
		}
		
		return true;
		
	}
	
	private void moveCell(int direction)
	{
		//Log.d("direction",direction+"");
		if(direction == 3 && currentY - 1 >= 0 && !mazeGenerator.walls[currentX][currentY][3]) {
			//Log.d("3","true");
			mazeColor[currentX][currentY] = false;
			currentY = currentY - 1;
			mazeColor[currentX][currentY] = true;
			moveCount--;
		}
		else if(direction == 1 && currentY + 1 < columns && !mazeGenerator.walls[currentX][currentY][1]) {
		//	Log.d("1","true");
			mazeColor[currentX][currentY] = false;
			currentY = currentY + 1;
			mazeColor[currentX][currentY] = true;
			moveCount--;
		}
		else if(direction == 0 && currentX - 1 >=0 && !mazeGenerator.walls[currentX][currentY][0]) {
			//Log.d("0","true");
			mazeColor[currentX][currentY] = false;
			currentX = currentX - 1;
			mazeColor[currentX][currentY] = true;
			moveCount--;
		}
		else if(direction == 2 && currentX + 1 < rows && !mazeGenerator.walls[currentX][currentY][2]) {
			//Log.d("2","true");
			mazeColor[currentX][currentY] = false;
			currentX = currentX + 1;
			mazeColor[currentX][currentY] = true;
			moveCount--;
		}
		moves.setText("Moves Left : "+moveCount);
		if(currentX == mazeGenerator.dest_row && currentY == mazeGenerator.dest_col)
			gameOver(0);
		else if(moveCount == 0)
			gameOver(1);
		
		invalidate();
	}

	//user has reached the final cell
	//game over
	public void gameOver(int flag) {
		
		AlertDialog.Builder builder = new Builder(context);
		
		((GameActivity) getContext()).stopTimer = true;
		GameOver = true;
		
		if(flag == 0) {
			builder.setTitle("SUCCESS").setMessage("Well Done!!\nTime Taken : "+timeTaken );
			builder.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					if(gameMode == 1) { //Normal Game
						DB.openDataBase();
						String highScoreTime = DB.getTimeTaken(level);
						int highScoreMoves = DB.getMoves(level);
						if(maxMoves - moveCount < highScoreMoves || highScoreMoves == 0) {
							DB.updateCompletedMaze(level, timeTaken, maxMoves - moveCount);
						}
						else if(maxMoves - moveCount == highScoreMoves && timeTaken.compareTo(highScoreTime) < 0  ) {
							DB.updateCompletedMaze(level, timeTaken, maxMoves - moveCount);
						}
						DB.close();
						Activity activity = (Activity)getContext();
						activity.finish();
						Intent intent = new Intent(context, LevelsList.class);
						context.startActivity(intent);
					}
					else if(gameMode == 0) { //Quick Game
						
						DB.openDataBase();
						DB.updateQuickGameStats(level, 1, timeTaken);
						DB.close();
						Activity activity = (Activity)getContext();
						activity.finish();
						
					}
					
				}
			});
		}
		else if(flag == 1) {
			builder.setTitle("FAIL").setMessage("You Lose!\nTime Taken : "+timeTaken );
			builder.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					if(gameMode == 1) {
						
						Activity activity = (Activity)getContext();
						activity.finish();
						Intent intent = new Intent(context, LevelsList.class);
						context.startActivity(intent);
						
					}
					else if(gameMode == 0) { 
						
						DB.openDataBase();
						DB.updateQuickGameStats(level, 0, timeTaken);
						DB.close();
						
						Activity activity = (Activity)getContext();
						activity.finish();
					}
					
				}
			});
			
		}
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		
		
		
	}	
	

}