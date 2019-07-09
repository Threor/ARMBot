package de.arm.bot.ki;

import java.awt.Point;

import de.arm.bot.info.Action;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Status;

public abstract class KI {
	
	protected Maze maze;
	
	protected Point newPosition;
	
	protected KI(Maze maze) {
		this.maze=maze;
		this.newPosition=new Point(maze.getCurrentPosition());
	}
	
	public Action generateNextTurn(TurnInfo turnInfo) {
		processTurnInfo(turnInfo);
		return null;
	}
	
	private void processTurnInfo(TurnInfo turnInfo) {
		if(turnInfo.getLastActionResult().isOk()&&newPosition!=null) {
			Cell cell=maze.updateLocation(newPosition);
			cell.setStatus(Status.VISITED);
			maze.getCurrentCell().updateCells(turnInfo);
		}else {
			Output.logDebug("The last Action has failed!\nThat wasn't supposed to happen!\n"+turnInfo.getLastActionResult());
		}
	}
	
	protected void updatePosition(Direction direction) {
		Point current=maze.getCurrentPosition();
		switch(direction) {
			case NORTH:newPosition=new Point(current.x,current.y-1);break;
			case EAST:newPosition=new Point(current.x+1,current.y);break;
			case WEST:newPosition=new Point(current.x-1,current.y);break;
			case SOUTH:newPosition=new Point(current.x,current.y+1);break;
			default:Output.logDebug("Critical error, "+direction+" is unknown");
		}
	}
	
}
