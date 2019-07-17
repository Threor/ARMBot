package de.arm.bot.model;

import de.arm.bot.dca.DCAStatus;
import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.*;

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
	 * Direction to Cell
	 * @see de.arm.bot.info.Direction
	 */
	private Map<Direction,Cell> neighbours;

	/**
	 * The defaultly used constructor for the class cell
	 * Additionally to setting all attributes it links all neighbour cells
	 * @param x The current x-coordinate of the cell
	 * @param y The current y-coordinate of the cell
	 * @param status The current status of the cell
	 * @see de.arm.bot.model.Status
	 * @param neighbours A map of all neighbour cells of the current cell
	 */
	Cell(int x, int y, Status status, Map<Direction, Cell> neighbours) {
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
	int getX() {
		return x;
	}
	/** Getter for the attribute y
	 * @return The current y-coordinate of the cell
	 */
	int getY() {
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
	private boolean isDead() {
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
	private void setNeighbour(Direction direction, Cell cell) {
		neighbours.put(direction, cell);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cell))return false;
		Cell cell=(Cell) obj;
		return cell.getX()==x&&cell.getY()==y;
	}
	
	/**
	 * Gets and returns all neighbours of the cell that are not dead
	 * @see de.arm.bot.model.Status
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
	 * @param cell The to get the direction from
	 * @return The calculated direction
	 */
	public Direction getDirection(Cell cell) {
		Output.logDebug("Cell: "+this);
		if(!neighbours.containsValue(cell)) return null;
		Map.Entry<Direction,Cell> ret= neighbours.entrySet().stream()
				.filter(e-> e.getValue().equals(cell)).findFirst().orElse(null);
		if(ret==null){
			Output.logDebug("ERR Couldn't get Direction from "+cell+" to "+this+"!");
			return null;
		}
		return ret.getKey();
	}

	/** Checks whether there is a FINISH cell nearby this cell. Only used by the Level 1 algorithm
	 * @return True if one of the neighbour cells of this cell is a FINISH cell
	 */
	public boolean hasFinishNearby() {
		return neighbours.values().stream().anyMatch(c->c.getStatus()==FINISH);
	}

	boolean hasUndiscoveredNearby() {
	    return neighbours.values().stream().anyMatch(c->c.getStatus()==NOT_DISCOVERED);
    }

    public int getNotDiscoveredNeighbourCount() {
	    return (int) neighbours.values().stream().filter(c->c.getStatus()==NOT_DISCOVERED).count();
    }


	
}
