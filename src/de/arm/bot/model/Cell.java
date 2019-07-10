package de.arm.bot.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.arm.bot.dca.DCAStatus;
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
	
	private static Stack<Cell> processing=new Stack<>();
	//TODO Maybe this could fuck up but i hope it does not
	private static HashMap<Cell,Integer> cache=new HashMap<>();
	
	private static int stopHere;
	
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
	
	private int calcMin(Map<Integer,Direction> costForDirection) {
		int ret= costForDirection.keySet().stream().min(Comparator.comparing(Integer::valueOf)).get();
		Output.logDebug("CURRENT SH is "+ret);
		return ret;
	}
	
	public Direction getDirectionWithLowestCost() {
		processing.clear();
		//cache.clear();
		stopHere=Integer.MAX_VALUE;
		Map<Integer,Direction> costForDirection=new HashMap<>();
		costForDirection.put(northernCell.getCost(0), Direction.NORTH);
		stopHere=calcMin(costForDirection);
		costForDirection.put(southernCell.getCost(0), Direction.SOUTH);
		stopHere=calcMin(costForDirection);
		costForDirection.put(westernCell.getCost(0), Direction.WEST);
		stopHere=calcMin(costForDirection);
		costForDirection.put(easternCell.getCost(0), Direction.EAST);
		costForDirection.forEach((k,v)->{
			Output.logDebug(v+" - "+k);
		});
		//TODO Consider returning multiple directions with same cost, Also consider maybe returning the map for using a more detailed cost algorithm	
		return costForDirection.get(calcMin(costForDirection));
	}
	
	private int getCost(int cost) {
		if(processing.size()>20)return Integer.MAX_VALUE/2;
		if(cost>(stopHere))return Integer.MAX_VALUE/2;
		if(processing.contains(this))return Integer.MAX_VALUE/2;
		//Output.logDebug("Calculation cost for "+x+" "+y);
		processing.push(this);
		//FIXME Quadrate Implement caching and lock cells, which are on the current path to avoid infinite recursion
		int ret=++cost;
		/*if(cache.containsKey(this)) {
			processing.pop();
			return ret+cache.get(this);
		}*/
		//TODO Work with FINISH
		if(this.getStatus().equals(Status.FLOOR)||this.getStatus().equals(Status.FINISH)) {
			//Output.logDebug(x+" "+y+" is free!"+ret);
			processing.pop();
			return ret;
		}
		if(this.getStatus().equals(Status.WALL)) {
			//Output.logDebug(x+" "+y+" is wall");
			processing.pop();
			return Integer.MAX_VALUE/2;
		}
		List<Integer> costForNeighbours=new ArrayList<>();
		costForNeighbours.add(northernCell.getCost(ret));
		costForNeighbours.add(southernCell.getCost(ret));
		costForNeighbours.add(westernCell.getCost(ret));
		costForNeighbours.add(easternCell.getCost(ret));
		//TODO Wrap cells when hitting top
		int co=costForNeighbours.stream().min(Comparator.comparing(Integer::valueOf)).get();
		//Output.logDebug("Cost for "+x+" "+y+": "+(ret+co));
		//cache.put(this,co);
		processing.pop();
		return co;
	}
	
	public void updateCells(TurnInfo turnInfo) {
		this.setStatus(turnInfo.getCurrentCellStatus());
		northernCell.setStatus(turnInfo.getNorthernCellStatus());
		easternCell.setStatus(turnInfo.getEasternCellStatus());
		westernCell.setStatus(turnInfo.getWesternCellStatus());
		southernCell.setStatus(turnInfo.getSouthernCellStatus());
	}
	
	public boolean isDead() {
		return status.isDead();
	}
	
	public DCAStatus getDCAStatus() {
		int deadCellsNearby=0;
		if(northernCell.isDead())deadCellsNearby++;
		if(southernCell.isDead())deadCellsNearby++;
		if(westernCell.isDead())deadCellsNearby++;
		if(easternCell.isDead())deadCellsNearby++;
		
		return DCAStatus.get(deadCellsNearby);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cell))return false;
		Cell cell=(Cell) obj;
		return cell.getX()==x&&cell.getY()==y;
	}
	
}
