package com.ggwp.maze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class LevelsDB extends SQLiteOpenHelper{

	
	
	//destination path (location) of our database on device
	private static String DB_PATH = ""; 
	private static String DB_NAME ="mazeDB";// Database name
	private SQLiteDatabase mDataBase; 
	private Context mContext;
	
	//LEVELS Table
	String levelsTable = "LEVELS";
	String level = "LEVEL";
	String completed = "COMPLETED";
	String timeTaken = "TIME_TAKEN";
	String movesTaken = "MOVES_TAKEN";
	String rows = "ROWS";
	String columns = "COLUMNS";
	String destRow = "DEST_ROW";
	String destCol = "DEST_COLUMN";
	String maxDistance = "MAX_DISTANCE";
	
	//COORDINATES TABLE
	String coordinatesTable = "COORDINATES";
//	String level = "LEVEL";
	String row = "ROW";
	String column = "COLUMN";
	String wall0 = "WALL0";
	String wall1 = "WALL1";
	String wall2 = "WALL2";
	String wall3 = "WALL3";
	
	
	
	public LevelsDB(Context context) {
		super(context, DB_NAME, null, 1);
		
		if(android.os.Build.VERSION.SDK_INT >= 17) {
	    
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
	    }
	    else {
	    	
	       DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	    }
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		String createTable = "CREATE TABLE "+levelsTable+"( "+level+" integer,"+completed+" integer ,"+
									timeTaken+" TEXT ,"+movesTaken+" integer ,"+rows+" integer ,"+
									columns+" integer,"+destRow+" integer,"+
									destCol+" integer,"+maxDistance+" integer)";
		db.execSQL(createTable);
		
		createTable = "CREATE TABLE "+coordinatesTable+"( "+level+" integer,"+row+" integer ,"+
				column+" integer,"+wall0+" integer,"+wall1+" integer,"+wall2+" integer,"
				+wall3+" integer)";
		
		db.execSQL(createTable);
		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	//	db.execSQL("DROP TABLE IF EXISTS "+levelsTable);
		
	}

	//creates the database if it doesn't already exists by copying from assets folder
	public void createDataBase() throws IOException {

		//If database not exists copy it from the assets
	    boolean mDataBaseExist = checkDataBase();
	    if(!mDataBaseExist) {
	    	
	    	//creates a file at the destination
	        this.getReadableDatabase();
	        this.close();
	        try {
	            //Copy the database from assests
	            copyDataBase();
	            
	        } 
	        catch (IOException mIOException) {
	            throw new Error("ErrorCopyingDataBase");
	        }
	    }
	}
	
	//check if file exists
	private boolean checkDataBase() {
        
		File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }
	
	
	//copy file from assets
	private void copyDataBase() throws IOException {
		
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);

        byte[] mBuffer = new byte[1024];
        int mLength;
        
        while ((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
	
	//open writable object
	public void openDataBase() throws SQLException{
		
        String myPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
	
	
	//close database writable object opened above
	@Override
    public synchronized void close() 
    {
        if(mDataBase != null) {
        	mDataBase.close();
        }
        	
            
        super.close();
        
    }
	
	//adds a level to the maze
	//no use for user
	void addLevel(MazeGenerator maze,int level) {
		
		ContentValues values = new ContentValues();
		
		values.put(this.level, level);
		values.put(completed, 0);
		values.put(timeTaken, "");
		values.put(movesTaken, 0);
		values.put(rows, maze.rows);
		values.put(columns,maze.columns);
		values.put(destRow, maze.dest_row);
		values.put(destCol, maze.dest_col);
		values.put(maxDistance, maze.max_distance);
		mDataBase.insert(levelsTable, null, values);
		
		
		
		for(int i = 0 ; i < maze.rows ; i++) {
			
			for(int j = 0 ; j < maze.columns ; j++) {
				
				ContentValues values2 = new ContentValues();
				values2.put(this.level, level);
				values2.put(row, i);
				values2.put(column, j);
				
				if(maze.walls[i][j][0])
					values2.put(wall0, 1);
				else
					values2.put(wall0, 0);
				
				if(maze.walls[i][j][1])
					values2.put(wall1, 1);
				else
					values2.put(wall1, 0);
				
				if(maze.walls[i][j][2])
					values2.put(wall2, 1);
				else
					values2.put(wall2, 0);
				
				if(maze.walls[i][j][3])
					values2.put(wall3, 1);
				else
					values2.put(wall3, 0);
				
				mDataBase.insert(coordinatesTable, null, values2);
				
			}
		}
		

	}
	
	//marks the level as completed
	void updateCompletedMaze(int level,String time,int moves) {
		
		ContentValues values=new ContentValues();
	    values.put(completed, 1);
	    values.put(timeTaken, time);
	    values.put(movesTaken,moves);
	    mDataBase.update(levelsTable, values, this.level+" = ?", new String []{String.valueOf(level)});	    

	}
	
	//checks if the level has been completed
	boolean checkCompletion(int level) {
		
		String query = "SELECT * FROM "+levelsTable+" where "+this.level+"="+String.valueOf(level);
		Cursor cursor = mDataBase.rawQuery(query, null);
		
		if(cursor.moveToFirst()) 
			return (cursor.getInt(1) == 1);
		else
			return false;
	
	}
	
	//returns the time taken to complete a level if completed
	String getTimeTaken(int level) {
		
		String query = "SELECT * FROM "+levelsTable+" where "+this.level+"="+String.valueOf(level);
		Cursor cursor = mDataBase.rawQuery(query, null);
		
		if(cursor.moveToFirst()) {
			return cursor.getString(2);
		}
		else
			return "00:00:00";
	}
	
	//returns the number of moves taken to complete a level
	int getMoves(int level) {
	
		String query = "SELECT * FROM "+levelsTable+" where "+this.level+"="+String.valueOf(level);
		Cursor cursor = mDataBase.rawQuery(query, null);
		
		if(cursor.moveToFirst()) 
			return cursor.getInt(3);
		else
			return 0;
	
	}
	
	//returns a mazeGenerator object which contains the maze for a given level
	MazeGenerator getMaze(int level) { 
		
		String query = "SELECT * FROM "+levelsTable+" where "+this.level+"="+String.valueOf(level);
	    Cursor cursor = mDataBase.rawQuery(query, null);
	    MazeGenerator maze = new MazeGenerator();
	    
	    if (cursor.moveToFirst()) {
	        do {
	        		
		            maze.rows = cursor.getInt(4);
		            maze.columns = cursor.getInt(5);
		            maze.dest_row = cursor.getInt(6);
		            maze.dest_col = cursor.getInt(7);
		            maze.max_distance = cursor.getInt(8);	            
		            maze.setDistance();
		            maze.setVisited();
		            maze.setWalls();
		            maze.setValues();
	        } while (cursor.moveToNext());
	    }
	    
	    query = "SELECT * FROM "+coordinatesTable+" where "+this.level+"="+String.valueOf(level);
	    cursor = mDataBase.rawQuery(query, null);
	    
	    //set the wall values
	    if (cursor.moveToFirst()) {
	    	
	        do {
	        	
	        		if(cursor.getInt(3) == 1)
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][0] = true;
	        		else
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][0] = false;
	        		
	        		if(cursor.getInt(4) == 1)
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][1] = true;
	        		else
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][1] = false;
	        		
	        		if(cursor.getInt(5) == 1)
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][2] = true;
	        		else
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][2] = false;
	        		
	        		if(cursor.getInt(6) == 1)
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][3] = true;
	        		else
	        			maze.walls[cursor.getInt(1)][cursor.getInt(2)][3] = false;
		            
	        } while (cursor.moveToNext());
	    }
	    
	    return maze;
	}
	
	//returns the total number of levels in the database
	public int getTotalLevels() {
		
	
		String query = "SELECT * FROM "+levelsTable;
		
	    Cursor cursor = mDataBase.rawQuery(query, null);
	    
	    return cursor.getCount();
	}
}
