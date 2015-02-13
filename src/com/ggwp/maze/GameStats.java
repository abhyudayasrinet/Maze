package com.ggwp.maze;

public class GameStats {
	
	int level;
	int totalWins;
	int totalLoses;
	int gamesPlayed;
	String fastestTime;
	
	public GameStats(int level, int totalWins, int totalLoses, int gamesPlayed,
			String fastestTime) {
	
		this.level = level;
		this.totalWins = totalWins;
		this.totalLoses = totalLoses;
		this.gamesPlayed = gamesPlayed;
		this.fastestTime = fastestTime;
	}
	
	
	@Override
	public String toString() {
	
		return level+","+gamesPlayed+","+totalWins+","+totalLoses+","+fastestTime;
	}
	

}
