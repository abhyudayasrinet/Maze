package com.ggwp.maze;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class LevelView extends View {

	Context context;	
	int rows;
	int columns;
	int cell_size;
	int height;
	int width;
	int moveCount;
	int maxMoves;
	int level;
	Paint paint;
	MazeGenerator mazeGenerator;
	String timeTaken;
	TextView moves;
	LevelsDB DB;
	
	
	int mazeX[][];
	int mazeY[][];
	boolean mazeColor[][];
	
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
	
	//same as gameview except mazegenerator recieves object queried from database
	public LevelView(Context context,int height,int width,TextView moves,int level) {
		super(context);
		
		this.context = context;
		DB = new LevelsDB(context);
		
		DB.openDataBase();
		
		mazeGenerator = DB.getMaze(level);
		DB.close();
		rows = mazeGenerator.rows;
        columns = mazeGenerator.columns;
		this.height = height;
		this.width = width;
		cell_size = Math.min((width-20)/columns,(height-20)/rows);
		this.level = level;
		paint = new Paint();
		this.moves = moves;
		mazeX = new int[rows][columns];
		mazeY = new int[rows][columns];
		mazeColor = new boolean[rows][columns];
		moveCount = (int) Math.min(mazeGenerator.max_distance+(0.1*mazeGenerator.max_distance),(rows*columns)-1);
		maxMoves = moveCount;
		moves.setText("Moves Left : "+moveCount);
		createMazeCoordinates();
		detector = new ScaleGestureDetector(context, new ScaleListener());
		
		
		
	}

	
	
	//assign coordinates of top left corner of each cell
	private void createMazeCoordinates() {
		
		int start_x = (width - columns*cell_size)/2; 
		int x = start_x; //starting x coord top left
		int y = 0; //starting y coord top left
		
		for(int i = 0 ; i < rows ; i++) {
			
			for(int j = 0 ; j < columns ; j++) {
				mazeX[i][j] = x;
				mazeY[i][j] = y;
				mazeColor[i][j] = false;
				x+=cell_size;
			}
			y += cell_size;
			x = start_x;
		}
		
		mazeColor[0][0] = true; 
		
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
				
				if(mazeColor[i][j]) {
					paint.setColor(Color.GREEN);
					paint.setStyle(Paint.Style.FILL);
					canvas.drawRect(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cell_size, mazeY[i][j]+cell_size, paint);
				}
				
				if(i == mazeGenerator.dest_row && j == mazeGenerator.dest_col) {
					paint.setColor(Color.RED);
					canvas.drawRect(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cell_size, mazeY[i][j]+cell_size, paint);
				}

				paint.setColor(Color.BLACK);
				paint.setStrokeWidth(2);
				if(mazeGenerator.walls[i][j][0])
					canvas.drawLine(mazeX[i][j], mazeY[i][j], mazeX[i][j]+cell_size, mazeY[i][j], paint);
				if(mazeGenerator.walls[i][j][1])
					canvas.drawLine(mazeX[i][j]+cell_size, mazeY[i][j], mazeX[i][j]+cell_size, mazeY[i][j]+cell_size, paint);
				if(mazeGenerator.walls[i][j][2])
					canvas.drawLine(mazeX[i][j], mazeY[i][j]+cell_size, mazeX[i][j]+cell_size, mazeY[i][j]+cell_size, paint);
				if(mazeGenerator.walls[i][j][3])
					canvas.drawLine(mazeX[i][j], mazeY[i][j], mazeX[i][j], mazeY[i][j]+cell_size, paint);

			}
		}
		
		
		canvas.restore();
	}
	
	
	//handles panning and zooming and touch detection to mark cells
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
            //This event happens when the first finger is pressed onto the screen
			startX = event.getX() - previousTranslateX;
			startY = event.getY() - previousTranslateY;
			screenTouched = true;
			//logCell(event.getX(),event.getY());
			markCell(event.getX(),event.getY());           

            break;
		
			
		case MotionEvent.ACTION_MOVE:
			//This event fires when the finger moves across the screen, although in practice I've noticed that
            //this fires even when you're simply holding the finger on the screen.
			mode = DRAG;
			translateX = event.getX() - startX;
			translateY = event.getY() - startY;
			
			//if scaling is at min(zoomed out)
			//set translateX,Y to 0
			//infinite panning still occurs when zoomed out updating translate values
			//and messing up detecting which cells are touched
			if(scaleFactor == MIN_ZOOM)
				translateX = translateY = 0f;
			
			
			double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) + Math.pow(event.getY() - (startY + previousTranslateY), 2));
			
			//if distance of dragging is > 100 only then assume user wants to pan 
			//else leave it be
            if(distance > 100) {
               dragged = true;
            }    
            else {
            	dragged = false;
            	translateX = previousTranslateX;
            	translateY = previousTranslateY;
            }
            
			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:
			//This event fires when a second finger is pressed onto the screen
			//mode = ZOOM;
			break;
			
		case MotionEvent.ACTION_UP:
			//This event fires when all fingers are off the screen
			mode = NONE;
			dragged = false;
			screenTouched = false;
			previousTranslateX = translateX;
            previousTranslateY = translateY;
            
			break;
			
		case MotionEvent.ACTION_POINTER_UP:
			//This event fires when the second finger is off the screen, but the first finger is still on the
            //screen
			mode = DRAG;
			previousTranslateX = translateX;
            previousTranslateY = translateY;
			break;
			
		}
		
		detector.onTouchEvent(event);
		
		if ((mode == DRAG && scaleFactor != 1f && dragged) || screenTouched) { 
			
			invalidate();
			
		}
		return true;
	}
	
	//colors the cell touched if any of it's neighbour has been reached
	private void markCell(float x, float y) {

		x = x/scaleFactor + Math.abs(translateX/scaleFactor);
		y = y/scaleFactor + Math.abs(translateY/scaleFactor);
		int row_number = -1,column_number = -1;
		for(int i=0;i<rows;i++) {
			
			for(int j=0;j<columns;j++) {
				
				//check if touched location lies within current cell
				//and if any of the neighbour is visited 
				if(x > mazeX[i][j] && x < mazeX[i][j]+cell_size &&
					y > mazeY[i][j] && y < mazeY[i][j]+cell_size && neighbourVisited(i,j) &&
					!mazeColor[i][j]) {
					
						row_number = i;
						column_number = j;
						
						//update move count
						moveCount--;
						moves.setText("Moves Made : "+moveCount);
						
						//call game over if touched cell is the destination
						if(row_number == mazeGenerator.dest_row && column_number == mazeGenerator.dest_col)
							gameOver(0,timeTaken);
						else if(moveCount == 0)
							gameOver(1,timeTaken);
							
				}
			}
		}
		
		//if touch location wasn't within the maze
		//do nothing
		if(row_number == -1 || column_number == -1)
			return;
		
		//mark the cell visited
		mazeColor[row_number][column_number] = true;
		invalidate();
		
	}

	//user has reached the final cell
	//game over
	private void gameOver(int flag,final String timeTaken) {
		
		AlertDialog.Builder builder = new Builder(context);
		if(flag == 0) {
			builder.setTitle("SUCCESS").setMessage("Well Done!!\nTime Taken : "+timeTaken );
			builder.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
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
			});
		}
		else if(flag == 1) {
			
			builder.setTitle("YOU LOSE").setMessage("YOU RAN OUT OF MOVES!");
			builder.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					Activity activity = (Activity)getContext();
					activity.finish();
					Intent intent = new Intent(context, LevelsList.class);
					context.startActivity(intent);
					
				}
			});
			
		}
		
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		
	}

	//checks if any of the cell's neighbours is visited
	private boolean neighbourVisited(int row, int col) {
		
		boolean val = false;
		if( row - 1 >= 0 && !mazeGenerator.walls[row-1][col][2])
			val = val | mazeColor[row-1][col];
		if( row + 1 < rows && !mazeGenerator.walls[row+1][col][0])
			val = val | mazeColor[row+1][col];
		if( col - 1 >= 0 && !mazeGenerator.walls[row][col-1][1])
			val = val | mazeColor[row][col-1];
		if(col + 1 < columns && !mazeGenerator.walls[row][col+1][3])
			val = val | mazeColor[row][col+1];
		return val;
	}

	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            if(scaleFactor == MIN_ZOOM)
            	translateX = translateY = 0f;
            
            invalidate();
            return true;
		}
		
	}

}