package com.ggwp.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import android.util.Log;


public class MazeGenerator {
	
	
	public boolean visited[][];
	public boolean walls[][][];
	public boolean marked[][];
	int distance[][];
	int id[][];
	int rows;
	int columns;
	public int dest_row;
	public int dest_col;
	int max_distance;
	List<Cell> deadEnds;
	
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
		id = new int[rows][columns];
		deadEnds = new ArrayList<Cell>();
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
	 * BACKTRACKING
	 * ITERATION
	 */
	public void createMazeBacktrack() {
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
				
			if(CheckForDeadEnd(row, column)) {
				
				this.visited[current.row][current.col] = true;
				int k = 0;
				for(int i=0;i<4;i++)
					if(walls[row][column][i])
						k++;
				if(k==3) 
					deadEnds.add(current);
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
	
	
	/*
	 * RANDOMIZED
	 * PRIM'S
	 * 0 - OUT
	 * 1 - FRONTIER
	 * 2 - IN
	 */
	void createMazePrims() {
		
		//Log.d("prims","started");
		
		List<Cell> frontier = new ArrayList<Cell>();
		
		int state[][] = new int[rows][columns];
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				state[i][j] = 0;
			}
		}
		Random rand = new Random();
		int choice,row,column;
		Cell current;
		
		//choose cell from out at random
		row = rand.nextInt(rows);
		column = rand.nextInt(columns);
		
//		Log.d("current",row+","+column);
		
		//mark it as in
		state[row][column] = 2;
		
		//add it's neighbors to frontier
		if(row+1<rows) {
			state[row+1][column] = 1;
			frontier.add(new Cell(row+1,column,0));
		}
		
		if(row-1>=0) {
			state[row-1][column] = 1;
			frontier.add(new Cell(row-1,column,0));
		}
		
		if(column+1<columns) {
			state[row][column+1] = 1;
			frontier.add(new Cell(row,column+1,0));
		}
		
		if(column-1>=0) {
			state[row][column-1] = 1;
			frontier.add(new Cell(row,column-1,0));
		}
		
		while(frontier.size() > 0) {
			
			//choose a frontier cell at random
			choice = rand.nextInt(frontier.size());
			current = frontier.get(choice);
			//remove from frontier
			frontier.remove(choice);
			
			row = current.row;
			column = current.col;
			
			//mark it as "in"
			state[row][column] = 2;
			
			//connect it to randomly chosen "in" neighbor
			List<Cell> inNeighbours = new ArrayList<Cell>();
			if(row+1 < rows && state[row+1][column] == 2) 
				inNeighbours.add(new Cell(row+1,column,0));
			if(row - 1 >=0 && state[row-1][column] == 2)
				inNeighbours.add(new Cell(row-1,column,0));
			if(column - 1 >=0 && state[row][column-1] == 2)
				inNeighbours.add(new Cell(row,column-1,0));
			if(column + 1 < columns && state[row][column+1] == 2)
				inNeighbours.add(new Cell(row,column+1,0));
			choice = rand.nextInt(inNeighbours.size());
			Cell chosen = inNeighbours.get(choice);
			
			if(chosen.row == row + 1) {
				walls[row][column][2] = false;
				walls[row+1][column][0] = false;
			}
			else if(chosen.row == row - 1) {
				walls[row][column][0] = false;
				walls[row-1][column][2] = false;
			}	
			else if(chosen.col == column + 1) {
				walls[row][column][1] = false;
				walls[row][column+1][3] = false;
			}
			else if(chosen.col == column - 1) {
				walls[row][column][3] = false;
				walls[row][column-1][1] = false;
			}
			
			//put it's "out" neighbors to "frontier"
			if(row+1 < rows && state[row+1][column] == 0) {
				state[row+1][column] = 1;
				frontier.add(new Cell(row+1,column,0));
			}
			if(row - 1 >=0 && state[row-1][column] == 0) {
				state[row-1][column] = 1;
				frontier.add(new Cell(row-1,column,0));
			}
			if(column - 1 >=0 && state[row][column-1] == 0) {
				state[row][column-1] = 1;
				frontier.add(new Cell(row,column-1,0));
			}
			if(column + 1 < columns && state[row][column+1] == 0) {
				state[row][column+1] = 1;
				frontier.add(new Cell(row,column+1,0));
			}
		}
		
		//Log.d("prims","done");
	}
	
	class Walls {
		int row;
		int col;
		int wall;
		Walls(int row,int col,int wall) {
			this.row = row;
			this.col = col;
			this.wall = wall;
		}
		
	}
	
	void createMazeKruskals() {
		
		Random rand = new Random();
		List<Walls> wallsList = new ArrayList<Walls>();
		int k = 1;
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				id[i][j] = k;
				k++;
				if(i - 1 >= 0) 
					wallsList.add(new Walls(i,j,0));
				if(j + 1 < columns) 
					wallsList.add(new Walls(i,j,1));
				if(i + 1 < rows) 
					wallsList.add(new Walls(i,j,2));
				if(j - 1 >= 0) 
					wallsList.add(new Walls(i,j,3));
//				Log.d("i,j,id",i+","+j+","+id[i][j]);
			}
		}
			
		int row,column,breakWall,choice;
		
		while(wallsList.size() > 0) {
			
			choice = rand.nextInt(wallsList.size());
			Walls wall = wallsList.get(choice);
			wallsList.remove(choice);
			row = wall.row;
			column = wall.col;
			breakWall = wall.wall;

			if(walls[row][column][breakWall]) {
				
				if(breakWall == 0 && (row - 1 >= 0 ) && (id[row][column] != id[row-1][column]) ) {
//					Log.d("combining ",row+","+column+","+id[row][column]+" - "+(row-1)+","+column+","+id[row-1][column]);
					walls[row][column][0] = false;
					walls[row-1][column][2] = false;
					mergeIds(row,column,row-1,column);		
				}
				else if(breakWall == 1 && (column+1 < columns) && (id[row][column] != id[row][column+1])) {
//					Log.d("combining ",row+","+column+","+id[row][column]+" - "+(row)+","+(column+1)+","+id[row][column+1]);
					walls[row][column][1] = false;
					walls[row][column+1][3] = false;
					mergeIds(row,column,row,column+1);
		
				}
				else if(breakWall == 2 && (row+1 < rows) && (id[row][column] != id[row+1][column])) {
//					Log.d("combining ",row+","+column+","+id[row][column]+" - "+(row+1)+","+column+","+id[row+1][column]);
					walls[row][column][2] = false;
					walls[row+1][column][0] = false;
					mergeIds(row,column,row+1,column);
		
				}
				else if(breakWall == 3 && (column-1 >= 0) && (id[row][column] != id[row][column-1])) {
//					Log.d("combining ",row+","+column+","+id[row][column]+" - "+(row)+","+(column-1)+","+id[row][column-1]);
					walls[row][column][3] = false;
					walls[row][column-1][1] = false;
					mergeIds(row,column,row,column-1);
		
				}
			}
		}
		
		
		
		
		
	}
	
	
	void mergeIds(int row1,int col1,int row2,int col2) {
		
		int newId = Math.min(id[row1][col1],id[row2][col2]);
		int id1 = id[row1][col1];
		int id2 = id[row2][col2];
		
//		Log.d("merging",id[row1][col1]+","+id[row2][col2]+" -> "+newId);
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				if(id[i][j] == id1 || id[i][j] == id2)
					id[i][j] = newId;
			}
		}
		
		
	}
	
	void getDestinationPoints() {
		max_distance = 0;
		List<Cell> queue = new ArrayList<Cell>();
		Cell current = new Cell(0,0,0);
		queue.add(current);
		int row,column,distance;
		while(!queue.isEmpty()) {
			
			current = queue.get(0);
			queue.remove(0);
			
			row = current.row;
			column = current.col;
			distance = current.dist;
			marked[row][column] = true;
			
			if(distance > max_distance) {
				
				dest_row = row;
				dest_col = column;
				max_distance = distance;
				
			}
				
			
			if(row+1 < rows && !walls[row][column][2] && !marked[row+1][column]) {
				marked[row+1][column] = true;
				queue.add(new Cell(row+1,column,distance+1));
			}
			if(row-1 >=0 && !walls[row][column][0] && !marked[row-1][column]) {
				marked[row-1][column] = true;
				queue.add(new Cell(row-1,column,distance+1));
			}
			if(column-1 >=0 && !walls[row][column][3] && !marked[row][column-1]){
				marked[row][column-1] = true;	
				queue.add(new Cell(row,column-1,distance+1));
			}
			if(column+1 >=0 && !walls[row][column][1] && !marked[row][column+1]){
				marked[row][column+1] = true;
				queue.add(new Cell(row,column+1,distance+1));
			}
			
		}
		
	}
	
	
	void unblockDeadEnds() {
		
		//Log.d("DEAD ENDS","-----");
		Random rand = new Random();
		int choice = 0;
		for(int i=0;i<deadEnds.size();i++) {
			
			Cell current = deadEnds.get(i);
			int row = current.row;
			int column = current.col;
			if(row == 0 || row == rows - 1 || column == 0 || column == columns -1)
				continue;
			//Log.d("row,col",row+","+column);
			List<Integer> temp = new ArrayList<Integer>();
			for(int j=0;j<4;j++)
				if(walls[row][column][j])
					temp.add(j);
			
			if(temp.size() > 0)
			{
				choice = rand.nextInt(temp.size());
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
