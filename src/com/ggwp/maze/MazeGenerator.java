package com.ggwp.maze;

import java.util.Random;


public class MazeGenerator {
	
	
	public boolean visited[][];
	public boolean walls[][][];
	int distance[][];
	int rows;
	int columns;
	public int dest_row;
	public int dest_col;
	int max_distance;
	
	MazeGenerator() {
		
	}
	
	
	MazeGenerator(int rows,int columns)
	{
		this.rows = rows;
		this.columns = columns; 
		visited = new boolean[rows][columns];
		walls = new boolean[rows][columns][4];
		distance = new int[rows][columns];
		max_distance = 0;
		setValues();
	}
	
	void setRow(int rows) {
		this.rows = rows;
	}
	
	void setColumns(int columns) {
		this.columns = columns;
	}
	
	void setVisited() {		
		visited = new boolean[rows][columns];
	}
	
	void setWalls() {
		walls = new boolean[rows][columns][4];
	}
	
	void setDistance() {
		distance = new int[rows][columns];
	}
	
	void setMaxDistance(int max_distance) {
		this.max_distance = max_distance;
	}
	
	void setValues() {
		
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				visited[i][j] = false;
				walls[i][j][0] = true;
				walls[i][j][1] = true;
				walls[i][j][2] = true;
				walls[i][j][3] = true;
			}
		}
		
	}
	
	public void create_maze(int row,int column,int distance) {
		
		visited[row][column] = true;
		this.distance[row][column] = distance;
		
		if(this.distance[row][column] > max_distance ) {
			dest_row = row;
			dest_col = column;
			max_distance = this.distance[row][column];
		}
		
		int breakLoop = 0;
		Random rand = new Random();
		boolean generateRandom = true;
		int n = 0;
		
		if(CheckForDeadEnd(row,column)) {
			return;
		}
		
		while(true) {
			
			if(generateRandom){
				
				n = rand.nextInt(4);
				breakLoop++;
				
			}
			if(n == 0) {
				if(!Visited(row-1,column)) {
					walls[row][column][0] = false;
					walls[row-1][column][2] = false;
					create_maze(row-1,column,distance + 1);
				}
			}
			else if(n == 1) {
				if(!Visited(row,column+1)){
					walls[row][column][1] = false;
					walls[row][column+1][3] = false;
					create_maze(row,column+1,distance + 1);
				}
			}
			else if(n == 2) {
				if(!Visited(row+1,column)){
					walls[row][column][2] = false;
					walls[row+1][column][0] = false;
					create_maze(row+1,column,distance + 1);
				}
			}
			else if(n == 3){
				if(!Visited(row,column-1)){
					walls[row][column][3] = false;
					walls[row][column-1][1] = false;
					create_maze(row,column-1,distance + 1);
				}
			}
			if(breakLoop == 10) {
				
				generateRandom = false;
				if(!Visited(row-1,column))
					n = 0;
				else if(!Visited(row,column+1))
					n = 1;
				else if(!Visited(row+1,column))
					n = 2;
				else if(!Visited(row,column-1))
					n = 3;
				else 
					return;
					
			}
			
			
			if(CheckForDeadEnd(row,column))
				break;
				
		}
	}

	private boolean CheckForDeadEnd(int row, int column) {
		
		if(Visited(row-1,column) && Visited(row,column+1) && Visited(row+1,column)
				&& Visited(row,column-1))
			return true;
		return false;
	}

	private boolean Visited(int row, int column) {
		if(row < 0 || column >= columns || row >= rows || column < 0)
			return true;
		
		
		return visited[row][column];
	}
	

}
