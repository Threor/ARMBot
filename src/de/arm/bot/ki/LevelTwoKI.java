package de.arm.bot.ki;

import static de.arm.bot.model.Status.FINISH;
import static de.arm.bot.model.Status.FORM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	//TODO Seems to fuck up, if the object to search for is right beneath the bot
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
		//When new forms where found, then go to them first
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
		return new Action(Command.GO,maze.getCurrentCell().getDirection(c).toString());
		/*List<Cell> path=aStar(maze.getCurrentCell(),cell);
		Output.logDebug("nav "+cell);
		Direction nextStep=maze.getCurrentCell().getDirectionWithLowestCost(cell);
		updatePosition(nextStep);
		Output.logDebug("Going" +nextStep);
		return new Action(Command.GO,nextStep.toString());*/
	}
	
	private List<Cell> reconstructPath(Map<Cell,Cell> cameFrom,Cell current) {
		List<Cell> totalPath = new ArrayList<>();
		totalPath.add(current);
		while(cameFrom.containsKey(current)) {
    		current=cameFrom.get(current);
    		totalPath.add(0,current);
    	}
    	return totalPath;
	}
    

	// A* finds a path from start to goal.
	// h is the heuristic function. h(n) estimates the cost to reach goal from node n.
	private List<Cell> aStar(Cell start, Cell goal) {
		Output.logDebug("Start: "+start+"\nZiel: "+goal);
		// The set of discovered nodes that need to be (re-)expanded.
	    // Initially, only the start node is known.
		List<Cell> openSet=new ArrayList<>();
	    openSet.add(start);
	    // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start to n currently known.
		Map<Cell,Cell> cameFrom =new HashMap<>();
	    // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
	    gScore =new HashMap<>();
	    gScore.put(start, 0);
	    // For node n, fScore[n] := gScore[n] + h(n).
	    fScore=new HashMap<>();
	    fScore.put(start,estimateDistance(start,goal));
	    while(!openSet.isEmpty()) {
	    	Output.logDebug(openSet+"");
	    	Cell current=fScore.entrySet().stream().filter(e->openSet.contains(e.getKey()))
	    			.min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
	    	if(goal.equals(current)) return reconstructPath(cameFrom, current);
	    	openSet.remove(current);
	    	for(Cell neighbour:current.getNotDeadNeighbours()) {
	    		// d(current,neighbor) is the weight of the edge from current to neighbor
	            // tentative_gScore is the distance from start to the neighbor through current
	            int tentativeGScore=gScore.get(current) + 1;
	            if(tentativeGScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)){
	            	  // This path to neighbor is better than any previous one. Record it!
	                cameFrom.put(neighbour, current);
	                gScore.put(neighbour, tentativeGScore);
	                fScore.put(neighbour,gScore.get(neighbour)+estimateDistance(neighbour,goal));
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
