package de.arm.bot.model;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.arm.bot.io.Output;

public class Maze {
	
	private Cell[][] cells;
	
	private Player player;
	
	public Maze(Player player, int length, int height) {
		this.player=player;
		this.cells=new Cell[length][height];
		for(int i=0;i<length;i++) {
			for(int j=0;j<height;j++) {
				Cell north=j==0?Cell.VOID:cells[i][j-1];
				Cell south=j>=height-1?Cell.VOID:cells[i][j+1];
				Cell west=i==0?Cell.VOID:cells[i-1][j];
				Cell east=i>=length-1?Cell.VOID:cells[i+1][j];
				Cell cell=new Cell(i, j, Status.NOT_DISCOVERED, north, west, south, east);
				cells[i][j]=cell;
			}
		}
	}
	
	public Cell getCurrentCell() {
		return cells[player.getX()][player.getY()];
	}
	
	public Cell updateLocation(Point newPosition) {
		Cell old=getCurrentCell();
		player.setX(newPosition.x);
		player.setY(newPosition.y);
		return old;
	}
	
	public Point getCurrentPosition() {
		return new Point(player.getX(),player.getY());
	}
	
	public void logCells() {
		for(Cell[] cell:cells) {
			for(Cell c:cell) {
				if(!c.getStatus().equals(Status.NOT_DISCOVERED)) {
					Output.logDebug(c.getX()+" "+c.getY()+" "+c.getStatus());
				}
			}
		}
	}
	
	public List<Cell> getAllDiscoveredCells() {
		return Arrays.stream(cells)
				.flatMap(Arrays::stream)
				.filter(c->!c.getStatus().equals(Status.NOT_DISCOVERED))
				.collect(Collectors.toList());
	}
}
