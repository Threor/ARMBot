package de.arm.bot.ki;

import static de.arm.bot.model.Status.*;

import java.util.*;
import java.util.Map.Entry;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Status;

public class LevelTwoKI extends LevelOneKI{
	
	private int formCount;
	
	private int foundForms;
	
	private Cell finish;
	
	private Map<Integer,Cell> formCells;
	
	private boolean performedTake;

	public LevelTwoKI(Maze maze) {
		super(maze);
		this.formCount=-1;
		this.formCells=new HashMap<>();
	}
	
	@Override
	public Action calculateMove(TurnInfo turnInfo) {
		//Found all forms and on FINISH
		if(turnInfo.getCellStatus().get(null)==FINISH&&foundForms==formCount) return new Action(Command.FINISH);
		//Found all previous forms and on FORM
		//TODO Maybe bug in engine? formId should start with 0 but it starts with 1
		if(turnInfo.getCellStatus().get(null)==FORM&&turnInfo.getCellStatus().get(null).getAdditionalInfo()==(foundForms)+1) {
			performedTake=true;
			pathToTake.clear();
			return new Action(Command.TAKE);
		}
		//Found all forms
		if(foundForms==formCount&&finish!=null) return navigateToCell(finish);
		//Found all previous forms
		if(formCells.containsKey(foundForms+1)) return navigateToCell(formCells.get(foundForms+1));
		//When new forms were found, then go to them first
		return getGOAction();
	}
	
	@Override
	protected void processTurnInfo(TurnInfo turnInfo) {
		super.processTurnInfo(turnInfo);
		if(performedTake) {
			performedTake=false;
			if(turnInfo.getLastActionResult().isOk()) {
				foundForms++;
			}
		}
		if(turnInfo.hasCell(FINISH)) {
			Entry<Direction, Status> entry=turnInfo.getCellStatus().entrySet().stream().filter(e->e.getValue()==Status.FINISH).findAny().orElse(null);
			if(entry==null) {
				Output.logDebug("After finding FINISH in TurnInfo, unable to get FINISH from TurnInfo!\n This should not happen!\n If it does, then you are cursed");
			}else {
				this.formCount=entry.getValue().getAdditionalInfo();
				this.finish=maze.getCurrentCell().getNeighbour(entry.getKey());
			}
		}
		if(turnInfo.hasCell(FORM)) {
			turnInfo.getCellStatus().entrySet().stream()
				.filter(e->e.getValue()==FORM)
				.forEach(e->formCells.put(e.getValue().getAdditionalInfo(), maze.getCurrentCell().getNeighbour(e.getKey())));
			formCells.forEach((k,v)->Output.logDebug(k+" -> "+v));
		}
	}
}
