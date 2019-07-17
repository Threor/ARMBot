package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.FINISH;
import static de.arm.bot.model.Status.FLOOR;

public class LevelOneKI extends KI{

	public LevelOneKI(Maze maze) {
		super(maze);
	}

	@Override
	public Action calculateMove(TurnInfo turnInfo) {
		super.processTurnInfo(turnInfo);
		//maze.logCellsSimple();
		if(turnInfo.getCellStatus().get(null)==FINISH) {
			return new Action(Command.FINISH);
		}
		if(maze.getCurrentCell().hasFinishNearby()){
			return navigateToCell(maze.getCellsIn(Collections.singletonList(FINISH)).get(0));
		}
		return getGOAction();
	}

	protected Action getGOAction() {
		List<Cell> toSearchFor=maze.getCellsIn(Collections.singletonList(FLOOR));
		int minCost=toSearchFor.stream().map(c->estimateDistance(maze.getCurrentCell(),c)).min(Comparator.comparingInt(i->i==0?Integer.MAX_VALUE:i)).orElse(-1);
		List<Cell> bestCells=toSearchFor.stream().filter(c->estimateDistance(maze.getCurrentCell(),c)==minCost).collect(Collectors.toList());
		for(Cell c:bestCells) {
			if(pathToTake.containsKey(c))return navigateToCell(c);
		}
		//TODO MZA
		/*Cell goal=toSearchFor.stream().min(Comparator.comparingInt(c->{
			int cost=estimateDistance(maze.getCurrentCell(),c);
			Output.logDebug(c+" -> "+cost);
			return cost==0?Integer.MAX_VALUE:cost;
		})).orElse(null);*/
		//FIXME Error handling
		Output.logDebug(String.format("Found %s cells with similar cost!",bestCells.size()));
		if(bestCells.size()==0) Output.logDebug("ERROR! Couldn't find goal cell!");
		return navigateToCell(bestCells.get(ThreadLocalRandom.current().nextInt(0,bestCells.size())));
	}

}
