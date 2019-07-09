package de.arm.bot.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;

public class Cell {

	private int x;
	private int y;
	
	private Status status;
	
	//TODO Maybe convert into map Direction->Cell
	private Cell northernCell;
	private Cell westernCell;
	private Cell southernCell;
	private Cell easternCell;
	
	public static final Cell VOID=new Cell();
	
	private Cell() {
		this.status=Status.WALL;
	}
	
	public Cell(int x, int y, Status status, Cell northernCell, Cell westernCell, Cell southernCell, Cell easternCell) {
		this.x = x;
		this.y = y;
		this.status = status;
		this.northernCell = northernCell;
		if(this.northernCell!=null&&this.northernCell.getSouthernCell()==null) this.northernCell.setSouthernCell(this);
		this.westernCell = westernCell;
		if(this.westernCell!=null&&this.westernCell.getEasternCell()==null) this.westernCell.setEasternCell(this);
		this.southernCell = southernCell;
		if(this.southernCell!=null&&this.southernCell.getNorthernCell()==null) this.southernCell.setNorthernCell(this);
		this.easternCell = easternCell;
		if(this.easternCell!=null&&this.easternCell.getWesternCell()==null) this.easternCell.setWesternCell(this);
	}
	/**
	 * @return x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * @return northernCell
	 */
	public Cell getNorthernCell() {
		return northernCell;
	}
	/**
	 * @return westernCell
	 */
	public Cell getWesternCell() {
		return westernCell;
	}
	/**
	 * @return southernCell
	 */
	public Cell getSouthernCell() {
		return southernCell;
	}
	/**
	 * @return easternCell
	 */
	public Cell getEasternCell() {
		return easternCell;
	}
	/**
	 * @param northernCell das zu setzende Objekt northernCell
	 */
	public void setNorthernCell(Cell northernCell) {
		this.northernCell = northernCell;
	}
	/**
	 * @param westernCell das zu setzende Objekt westernCell
	 */
	public void setWesternCell(Cell westernCell) {
		this.westernCell = westernCell;
	}
	/**
	 * @param southernCell das zu setzende Objekt southernCell
	 */
	public void setSouthernCell(Cell southernCell) {
		this.southernCell = southernCell;
	}
	/**
	 * @param easternCell das zu setzende Objekt easternCell
	 */
	public void setEasternCell(Cell easternCell) {
		this.easternCell = easternCell;
	}
	
	public void setStatus(Status status) {
		//TODO If, in future levels, cells can change their status, then revisit this method
		if(this.status.equals(Status.VISITED)) return;
		this.status = status;
	}
	
	public Direction getDirectionWithLowestCost() {
		Map<Integer,Direction> costForDirection=new HashMap<>();
		costForDirection.put(northernCell.getCost(Direction.SOUTH, 0), Direction.NORTH);
		costForDirection.put(southernCell.getCost(Direction.NORTH, 0), Direction.SOUTH);
		costForDirection.put(westernCell.getCost(Direction.WEST, 0), Direction.WEST);
		costForDirection.put(easternCell.getCost(Direction.EAST, 0), Direction.EAST);
		//TODO Consider returning multiple directions with same cost, Also consider maybe returning the map for using a more detailed cost algorithm	
		return costForDirection.get(costForDirection.keySet().stream().min(Comparator.comparing(Integer::valueOf)).get());
	}
	
	private int getCost(Direction from, int cost) {
		//FIXME Quadrate Implement caching and lock cells, which are on the current path to avoid infinite recursion
		int ret=++cost;
		//TODO Work with FINISH
		if(this.getStatus().equals(Status.FLOOR)||this.getStatus().equals(Status.FINISH))return ret;
		if(this.getStatus().equals(Status.WALL))return Integer.MAX_VALUE/2;
		List<Integer> costForNeighbours=new ArrayList<>();
		if(from!=Direction.NORTH)costForNeighbours.add(northernCell.getCost(Direction.SOUTH, ret));
		if(from!=Direction.SOUTH)costForNeighbours.add(southernCell.getCost(Direction.NORTH, ret));
		if(from!=Direction.WEST)costForNeighbours.add(westernCell.getCost(Direction.EAST, ret));
		if(from!=Direction.EAST)costForNeighbours.add(easternCell.getCost(Direction.WEST, ret));
		//TODO Wrap cells when hitting top
		int co=ret+costForNeighbours.stream().min(Comparator.comparing(Integer::valueOf)).get();
		Output.logDebug("Cost for "+x+" "+y+": "+co);
		return co;
	}
	
	public void updateCells(TurnInfo turnInfo) {
		this.setStatus(turnInfo.getCurrentCellStatus());
		northernCell.setStatus(turnInfo.getNorthernCellStatus());
		easternCell.setStatus(turnInfo.getEasternCellStatus());
		westernCell.setStatus(turnInfo.getWesternCellStatus());
		southernCell.setStatus(turnInfo.getSouthernCellStatus());
	}
	
}
