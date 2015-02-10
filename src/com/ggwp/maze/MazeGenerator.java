package com.ggwp.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import android.util.Log;


public class MazeGenerator {
	
	
	public boolean visited[][];
	public boolean walls[][][];
	public boolean marked[][];
	int distance[][];
	int rows;
	int columns;
	public int dest_row;
	public int dest_col;
	int max_distance;
	
	private class Cell {
		int row;
		int col;
		int dist;
		Cell(int x,int y,int d) {
			this.row = x;
			this.col = y;
			this.dist = d;
		}
		
	}
	
	MazeGenerator() {
		
	}
	
	
	MazeGenerator(int rows,int columns)
	{
		this.rows = rows;
		this.columns = columns; 
		visited = new boolean[rows][columns];
		walls = new boolean[rows][columns][4];
		distance = new int[rows][columns];
		marked = new boolean[rows][columns];
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
	
	/*
	 * RECURSION 
	 */
	/*
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
	*/
	
	/*
	 * ITERATION
	 */
	public void creatMaze() {
		//Log.d("mazeCreation","start");
		
		Stack<Cell> stack = new Stack<Cell>();
		stack.push(new Cell(0,0,0));
		int cells = 0;
		int distance = 0;
		int n = 0;
		int blockLength = 3;
		boolean generateRandom = true;
		int breakLoop = 0,row,column;
		Random rand = new Random();
		int direction[] = new int[4];
		direction[0] = direction[1] = direction[2] = direction[3] = 0;
		
		//Log.d("rowsxcolumns",rows+"x"+columns);
		
		while(cells != rows*columns) {
			
			Cell current = stack.peek();
			row = current.row;
			column = current.col;
			distance = current.dist;
			
			//Log.d("row,col distance",row+","+column+" "+distance);
			
			if(distance > max_distance) {
				dest_row = row;
				dest_col = column;
				max_distance = distance;
			}
				
			
			
			/*if(CheckForDeadEnd(row, column) && !visited[row][column]) {
				
				Log.d("marked dead end",row+","+column);
				if(!marked[row][column]) {
					
					
					if(row == 0) {
						walls[row][column][2] = false;
						walls[row+1][column][0] = false;
					}
					else if(column == 0) {
						walls[row][column][1] = false;
						walls[row][column+1][3] = false;
					}
					else if(row == rows - 1) {
						walls[row][column][0] = false;
						walls[row-1][column][2] = false;
					}
					else if(column == columns - 1) {
						walls[row][column][3] = false;
						walls[row][column-1][1] = false;
					}
					else {
						Log.d("row,column",row+","+column);
						List<Integer> temp = new ArrayList<Integer>();
						for(int i=0;i<4;i++)
							if(walls[row][column][i])
							{
								temp.add(i);
								Log.d("i",i+"");
							}
						Log.d("temp.size",temp.size()+"");
						if(temp.size() > 0)
						{
							int choice = rand.nextInt(temp.size());
							if(temp.get(choice) == 0) {
								walls[row][column][0] = false;
								walls[row-1][column][2] = false;
							}
							else if(temp.get(choice) == 1) {
								walls[row][column][1] = false;
								walls[row][column+1][3] = false;
							}
							else if(temp.get(choice) == 2) {
								walls[row][column][2] = false;
								walls[row+1][column][0] = false;
							}
							else if(temp.get(choice) == 3) {
								walls[row][column][3] = false;
								walls[row][column-1][1] = false;
							}
						}
					}
					
					marked[row][column]  = true;
				}
				this.visited[current.row][current.col] = true;
				stack.pop();
				cells++;
				continue;
				
			}
			else */if(CheckForDeadEnd(row, column)) {
				
				this.visited[current.row][current.col] = true;
				Log.d("dead end",row+","+column);
				stack.pop();
				cells++;
				continue;
				
			}
			
			this.visited[current.row][current.col] = true;
			generateRandom = true;
			breakLoop = 0;
			while(true) {
				
				if(generateRandom){
					
						n = rand.nextInt(4);
						breakLoop++;
						
					}
					if(n == 0 && direction[0] < blockLength) {
						
						if(!Visited(row-1,column)) {
							walls[row][column][0] = false;
							walls[row-1][column][2] = false;
							direction[0]++;
							direction[1] = direction[2] = direction[3] = 0;
							stack.push(new Cell(row-1,column,distance+1));
							break;
						}
						
					}
					else if(n == 1 && direction[1] < blockLength) {
						
						if(!Visited(row,column+1)){	
							walls[row][column][1] = false;
							walls[row][column+1][3] = false;
							direction[1]++;
							direction[0] = direction[2] = direction[3] = 0;
							stack.push(new Cell(row,column+1,distance+1));
							break;
						}
						
					}
					else if(n == 2 && direction[2] < blockLength) {
						
						if(!Visited(row+1,column)){
							walls[row][column][2] = false;
							walls[row+1][column][0] = false;
							direction[2]++;
							direction[1] = direction[0] = direction[3] = 0;
							stack.push(new Cell(row+1,column,distance+1));
							break;
						}
						
					}
					else if(n == 3 && direction[3] < blockLength){
						
						if(!Visited(row,column-1)){
							walls[row][column][3] = false;
							walls[row][column-1][1] = false;
							direction[3]++;
							direction[1] = direction[2] = direction[0] = 0;
							stack.push(new Cell(row,column-1,distance+1));
							break;
						}
						
					}
					
					if(breakLoop == 10) {
						
						generateRandom = false;
						direction[0] = direction[1] = direction[2] = direction[3] = 0;
						if(!Visited(row-1,column))
							n = 0;
						else if(!Visited(row,column+1))
							n = 1;
						else if(!Visited(row+1,column))
							n = 2;
						else if(!Visited(row,column-1))
							n = 3;
						else 
							break;
					}
			}
				
			
			
		}
		//Log.d("mazeCreation","done");
		
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
