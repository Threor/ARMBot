package de.arm.bot.model;

import static de.arm.bot.info.Direction.EAST;
import static de.arm.bot.info.Direction.NORTH;
import static de.arm.bot.info.Direction.SOUTH;
import static de.arm.bot.info.Direction.WEST;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;

public class Maze {
	
	private int length;
	private int height;
	
	private Cell[][] cells;
	
	private Player player;
	
	public Maze(Player player, int length, int height) {
		this.length=length;
		this.height=height;
		this.player=player;
		this.cells=new Cell[length][height];
		for(int i=0;i<length;i++) {
			for(int j=0;j<height;j++) {
				Map<Direction,Cell> neighbours=new HashMap<>();
				neighbours.put(NORTH, cells[i][j==0?height-1:j-1]);
				neighbours.put(SOUTH, cells[i][j>=height-1?0:j+1]);
				neighbours.put(WEST, cells[i==0?length-1:i-1][j]);
				neighbours.put(EAST, cells[i>=length-1?0:i+1][j]);
				Cell cell=new Cell(i, j, Status.NOT_DISCOVERED,neighbours);
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
		getAllDiscoveredCells()
			.forEach(c->Output.logDebug(c.getX()+" "+c.getY()+" "+c.getStatus()));
	}
	
	public void logCellsSimple() {
		for(int i=0;i<cells[0].length;i++) {
			String temp="";
			for(int j=0;j<cells.length;j++) {
				if(player.getX()==j&&player.getY()==i) {
					temp+="x ";
					continue;
				}
				switch(cells[j][i].getStatus()) {
				case NOT_DISCOVERED:temp+="0";break;
				case WALL:temp+="1";break;
				case FLOOR:temp+="2";break;
				case VISITED:temp+="3";break;
				case FINISH:temp+="4";break;
				default:temp+="5";
				}
				temp+=" ";
			}
			Output.logDebug(temp);
		}
	}
	
	public List<Cell> getAllDiscoveredCells() {
		return Arrays.stream(cells)
				.flatMap(Arrays::stream)
				.filter(c->!c.getStatus().equals(Status.NOT_DISCOVERED))
				.collect(Collectors.toList());
	}

	public int getLength() {
		return length;
	}

	public int getHeight() {
		return height;
	}
	
}
