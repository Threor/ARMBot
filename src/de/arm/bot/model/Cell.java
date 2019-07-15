package de.arm.bot.model;

import static de.arm.bot.model.Status.FINISH;
import static de.arm.bot.model.Status.FLOOR;
import static de.arm.bot.model.Status.FORM;
import static de.arm.bot.model.Status.NOT_DISCOVERED;
import static de.arm.bot.model.Status.VISITED;
import static de.arm.bot.model.Status.WALL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import de.arm.bot.dca.DCAStatus;
import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;

/**
 * A class representing a cell of the maze
 * @author Team ARM
 *
 */
public class Cell {

	/**
	 * The x-coordinate of the maze
	 */
	private int x;
	/**
	 * The y-coordinate of the maze
	 */
	private int y;
	
	/**
	 * The current status of the cell
	 * @see de.arm.bot.model.Status
	 */
	private Status status;
	
	/**
	 * A map of all neighbours of the cell, mapped as
	 * Direction -> Cell
	 * @see de.arm.bot.info.Direction
	 */
	private Map<Direction,Cell> neighbours;
	
	/**
	 * A static Stack used for the getDirectionWithLowestCost algorithm to keep track of the currently traversed path
	 */
	private static Stack<Cell> processing=new Stack<>();
	
	/**
	 * A static integer representing the currently lowest calculated cost for the getDirectionWithLowestCost algorithm
	 */
	private static int stopHere;
	
	/**
	 * The defaultly used constructor for the class cell
	 * Additionally to setting all attributes it links all neighbour cells
	 * @param x The current x-coordinate of the cell
	 * @param y The current y-coordinate of the cell
	 * @param status The current status of the cell
	 * @see de.arm.bot.model.Status
	 * @param neighbours A map of all neighbour cells of the current cell
	 */
	public Cell(int x, int y, Status status, Map<Direction,Cell> neighbours) {
		this.x=x;
		this.y=y;
		this.status=status;
		this.neighbours=neighbours;
		neighbours.entrySet().stream()
			.filter(e->e.getValue()!=null&&e.getValue().getNeighbour(e.getKey().getOpposite())==null)
			.forEach(e->e.getValue().setNeighbour(e.getKey().getOpposite(),this));
	}
	/** Getter for the attribute x
	 * @return The current x-coordinate of the cell
	 */
	public int getX() {
		return x;
	}
	/** Getter for the attribute y
	 * @return The current y-coordinate of the cell
	 */
	public int getY() {
		return y;
	}
	/** Getter for the attribute status
	 * @return status The current status of the cell
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Setter for the attribute status, does not set if the cell is already visited
	 * @param status The new status of the cell
	 */
	public void setStatus(Status status) {
		//TODO If, in future levels, cells can change their status, then revisit this method
		if(this.status==VISITED) return;
		this.status = status;
	}
	
	/**
	 * Calculates the minimal key value of the map, used by the getDirectionWithLowestCost algorithm 
	 * @param costForDirection The map with the keys to be compared
	 * @return The lowest key value
	 */
	private int calcMin(Map<Integer,Direction> costForDirection) {
		int ret= costForDirection.keySet().stream().min(Comparator.comparing(Integer::valueOf)).get();
		Output.logDebug("CURRENT SH is "+ret);
		return ret;
	}
	
	/**
	 * The algorithm used for calculating the best direction for the bot to walk to
	 * @return The direction the bot should walk to
	 */
	public Direction getDirectionWithLowestCost() {
		processing.clear();
		stopHere=Integer.MAX_VALUE;
		Map<Integer,Direction> costForDirection=new HashMap<>();
		processing.push(this);
		neighbours.forEach((direction,cell)->{
			costForDirection.put(cell.getCost(), direction);
			stopHere=calcMin(costForDirection);
		});
		processing.pop();
		costForDirection.forEach((k,v)->{
			Output.logDebug(v+" - "+k);
		});
		//TODO Consider returning multiple directions with same cost, Also consider maybe returning the map for using a more detailed cost algorithm	
		return costForDirection.get(calcMin(costForDirection));
	}
	
	/**
	 * A recursive method calculation the lowest cost for the getDirectionWithLowestCost algorithm 
	 * @return The lowest path cost from the current cell to a searched for cell
	 */
	private int getCost() {
		if(processing.size()>=stopHere||processing.contains(this))return Integer.MAX_VALUE/2;
		//Output.logDebug("Calculation cost for "+x+" "+y);
		//TODO Work with FINISH and form
		if(this.getStatus()==FLOOR||this.getStatus()==FINISH||this.getStatus()==FORM) {
			if(processing.size()<stopHere)stopHere=processing.size();
			//Output.logDebug("Found new SH "+stopHere);
			return 1;
		}
		if(this.getStatus()==WALL) {
			return Integer.MAX_VALUE/2;
		}
		processing.push(this);
		List<Integer> costForNeighbours=neighbours.values().stream().filter(c->!c.getStatus().isDead()).map(Cell::getCost).collect(Collectors.toList());
		processing.pop();
		//TODO Wrap cells when hitting top
		int co=costForNeighbours.stream().min(Comparator.comparing(Integer::valueOf)).get();
		//Output.logDebug("Cost for "+x+" "+y+": "+(ret+co));
		return ++co;
	}
	
	/**
	 * Updates the status for the current cell and for all neighbours
	 * @param cellStatus The map of the new status of all neighbour cells and the current cell (null as direction indicates the current cell)
	 */
	public void updateCells(Map<Direction,Status> cellStatus) {
		this.setStatus(cellStatus.get(null));
		cellStatus.entrySet().stream()
			.filter(e->e.getKey()!=null)
			.forEach(e->neighbours.get(e.getKey()).setStatus(e.getValue()));
	}
	
	/**
	 * Getter for the attribute dead of the current status
	 * @return Whether the cell is dead or not
	 */
	public boolean isDead() {
		return status.isDead();
	}
	
	/**
	 * Calculates and return the current DCAStatus of the cell, used by the DCA
	 * @return The current DCAStatus of the cell
	 */
	public DCAStatus getDCAStatus() {
		int deadCellsNearby=(int) neighbours.values().stream().filter(Cell::isDead).count();
		return DCAStatus.get(deadCellsNearby);
	}
	
	/**
	 * Gets and return the neighbour cell of the current cell in the given direction
	 * @param direction The direction of the neigbour cell
	 * @return The gotten neighbour
	 */
	public Cell getNeighbour(Direction direction) {
		return neighbours.get(direction);
	}
	
	/**
	 * Sets a new neighbour of the cell on the given direction
	 * @param direction The direction on which the new cell should be set
	 * @param cell The cell to be set
	 */
	public void setNeighbour(Direction direction, Cell cell) {
		neighbours.put(direction, cell);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cell))return false;
		Cell cell=(Cell) obj;
		return cell.getX()==x&&cell.getY()==y;
	}
	
	/**
	 * Gets and return all neighbours of the current cell (the values of the neghbours map)
	 * @return A list of all neghbour cells
	 */
	public List<Cell> getNeighbours() {
		return new ArrayList<>(neighbours.values());
	}
	
	/**
	 * Gets and returns all neighbours of the cell that are not dead
	 * @see de.arm.bot.Status
	 * @return A list of all not dead neighbours
	 */
	public List<Cell> getNotDeadNeighbours() {
		return neighbours.values().stream()
				.filter(c->!c.getStatus().isDead()&&!c.getStatus().equals(NOT_DISCOVERED))
				.collect(Collectors.toList());
	}
	@Override
	public String toString() {
		return String.format("Cell [x=%s, y=%s, status=%s]", x, y, status);
	}
	
	/**
	 * Gets the direction the given cell is currently on, if the cell is a neighbour cell
	 * @param cell
	 * @return The calculated direction
	 */
	public Direction getDirection(Cell cell) {
		Output.logDebug("Cell: "+this);
		if(!neighbours.containsValue(cell)) return null;
		return neighbours.entrySet().stream()
				.filter(e->{
					Output.logDebug(e.getKey()+" -> "+e.getValue());
					return e.getValue().equals(cell);
				}).findFirst().orElse(null).getKey();
	}
	
	//TODO Check if it works for wrap
	/**
	 * Calculates the estimated distance between this cell and the given cell
	 * @param cell The cell to which the distance should be calculated
	 * @return The calculated distance
	 */
	public int getDistance(Cell cell) {
		return Math.abs(x-cell.x)+Math.abs(y-cell.y);
	}
	
}
