package de.arm.bot.info;

import de.arm.bot.ki.*;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Player;

public class InitInfo {
	
	private int mazeLength;
	
	private int mazeHeight;
	
	private int mazeLevel;
	
	private int playerX;
	
	private int playerY;
	
	private int playerId;

	public InitInfo(int mazeLength, int mazeHeight, int mazeLevel, int playerX, int playerY, int playerId) {
		this.mazeLength = mazeLength;
		this.mazeHeight = mazeHeight;
		this.mazeLevel = mazeLevel;
		this.playerX = playerX;
		this.playerY = playerY;
		this.playerId = playerId;
	}
	
	public Player generatePlayer() {
		return new Player(playerX, playerY, playerId);
	}
	
	public Maze generateMaze() {
		return new Maze(generatePlayer(),mazeLength, mazeHeight);
	}
	
	public KI generateKI() {
		switch(mazeLevel) {
		case 1: return new LevelOneKI(generateMaze());
		case 2: return new LevelTwoKI(generateMaze());
		default: return new LevelOneKI(generateMaze());
		}
	}

	@Override
	public String toString() {
		return String.format(
				"InitInfo [mazeLength=%s, mazeHeight=%s, mazeLevel=%s, playerX=%s, playerY=%s, playerId=%s]",
				mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId);
	}
	
	
}
