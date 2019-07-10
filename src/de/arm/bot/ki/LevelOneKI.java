package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Status;

public class LevelOneKI extends KI{

	public LevelOneKI(Maze maze) {
		super(maze);
	}

	@Override
	public Action generateNextTurn(TurnInfo turnInfo) {
		//maze.logCells();
		super.generateNextTurn(turnInfo);
		if(maze.getCurrentCell().getStatus().equals(Status.FINISH)) {
			return new Action(Command.FINISH);
		}
		Direction nextStep=maze.getCurrentCell().getDirectionWithLowestCost();
		updatePosition(nextStep);
		return new Action(Command.GO,nextStep.toString());
	}

}
