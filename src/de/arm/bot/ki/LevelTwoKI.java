package de.arm.bot.ki;

import static de.arm.bot.model.Status.FINISH;
import static de.arm.bot.model.Status.FORM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	
	Map<Integer,Cell> formCells;
	
	private boolean performedTake;
	
	private Map<Cell,Integer> fScore;
	
	private Map<Cell,Integer> gScore;
	
	List<Cell> pathToTake;
	
	public LevelTwoKI(Maze maze) {
		super(maze);
		this.formCount=-1;
		this.formCells=new HashMap<>();
		this.pathToTake=new ArrayList<>();
	}
	
	@Override
	public Action calculateMove(TurnInfo turnInfo) {
		//Found all forms and on FINISH
		Output.logDebug("Current: "+turnInfo.getCellStatus().get(null));
		Output.logDebug("found forms: "+foundForms+" formCount: "+formCount);
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
			Entry<Direction, Status> entry=turnInfo.getCellStatus().entrySet().stream().filter(e->e.getValue()==Status.FINISH).findAny().get();
			this.formCount=entry.getValue().getAdditionalInfo();
			this.finish=maze.getCurrentCell().getNeighbour(entry.getKey());
		}
		if(turnInfo.hasCell(FORM)) {
			turnInfo.getCellStatus().entrySet().stream()
				.filter(e->e.getValue()==FORM)
				.forEach(e->formCells.put(e.getValue().getAdditionalInfo(), maze.getCurrentCell().getNeighbour(e.getKey())));
			formCells.forEach((k,v)->Output.logDebug(k+" -> "+v));
		}
	}
	
	//TODO Better implementation
	protected Action navigateToCell(Cell cell) {
		if(pathToTake.isEmpty()) {
			pathToTake=aStar(maze.getCurrentCell(),cell);
			pathToTake.remove(0);
		}
		Output.logDebug("Path to Take. "+pathToTake);
		Cell c=pathToTake.remove(0);
		Output.logDebug(c.toString());
		return go(maze.getCurrentCell().getDirection(c));
	}
	
	private List<Cell> reconstructPath(Map<Cell,Cell> path,Cell current) {
		List<Cell> totalPath = new ArrayList<>();
		totalPath.add(current);
		while(path.containsKey(current)) {
    		current=path.get(current);
    		totalPath.add(0,current);
    	}
    	return totalPath;
	}
    
	private List<Cell> aStar(Cell start, Cell finish) {
		Output.logDebug("Start: "+start+"\nZiel: "+finish);
		Set<Cell> openSet=new HashSet<>();
	    openSet.add(start);
		Map<Cell,Cell> path =new HashMap<>();
	    gScore =new HashMap<>();
	    gScore.put(start, 0);
	    fScore=new HashMap<>();
	    fScore.put(start,estimateDistance(start,finish));
	    while(!openSet.isEmpty()) {
	    	Cell current=fScore.entrySet().stream().filter(e->openSet.contains(e.getKey()))
	    			.min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
	    	if(finish.equals(current)) return reconstructPath(path, current);
	    	openSet.remove(current);
	    	for(Cell neighbour:current.getNotDeadNeighbours()) {
	            int tentativeGScore=gScore.get(current) + 1;
	            if(tentativeGScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
	                path.put(neighbour, current);
	                gScore.put(neighbour, tentativeGScore);
	                fScore.put(neighbour,gScore.get(neighbour)+estimateDistance(neighbour,finish));
	                if(!openSet.contains(neighbour)) openSet.add(neighbour);
	            }
	    	}	    	            
	    }	        
	    return null;
	}
	
	private int estimateDistance(Cell from,Cell to) {
		return Math.abs(from.getX()-to.getX())+Math.abs(from.getY()-to.getY());
	}
}
