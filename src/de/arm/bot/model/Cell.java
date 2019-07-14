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

public class Cell {

	private int x;
	private int y;
	
	private Status status;
	
	private Map<Direction,Cell> neighbours;
	
	private static Stack<Cell> processing=new Stack<>();
	
	private static int stopHere;
	
	private static int minCost;
	
	public Cell(int x, int y, Status status, Map<Direction,Cell> neighbours) {
		this.x=x;
		this.y=y;
		this.status=status;
		this.neighbours=neighbours;
		neighbours.entrySet().stream()
			.filter(e->e.getValue()!=null&&e.getValue().getNeighbour(e.getKey().getOpposite())==null)
			.forEach(e->e.getValue().setNeighbour(e.getKey().getOpposite(),this));
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

	public void setStatus(Status status) {
		//TODO If, in future levels, cells can change their status, then revisit this method
		if(this.status==VISITED) return;
		this.status = status;
	}
	
	private int calcMin(Map<Integer,Direction> costForDirection) {
		int ret= costForDirection.keySet().stream().min(Comparator.comparing(Integer::valueOf)).get();
		Output.logDebug("CURRENT SH is "+ret);
		return ret;
	}
	
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
	
	/*public Direction getDirectionWithLowestCost(Cell to) {
		minCost=Math.abs(x-to.x)+Math.abs(y-to.y);
		Output.logDebug(minCost+"");
		if(neighbours.containsValue(to)) return neighbours.entrySet().stream().filter(e->e.getValue()==to).findFirst().get().getKey();
		processing.clear();
		stopHere=Integer.MAX_VALUE;
		Map<Integer,Direction> costForDirection=new HashMap<>();
		processing.push(this);
		neighbours.forEach((direction,cell)->{
			costForDirection.put(cell.getCost(to), direction);
			stopHere=calcMin(costForDirection);
		});
		processing.pop();
		costForDirection.forEach((k,v)->{
			Output.logDebug(v+" - "+k);
		});
		//TODO Consider returning multiple directions with same cost, Also consider maybe returning the map for using a more detailed cost algorithm	
		return costForDirection.get(calcMin(costForDirection));
	}
	*/
	private int getCost() {
		if(stopHere==minCost)return Integer.MAX_VALUE;
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
	
	/*private int getCost(Cell to) {
		if(processing.size()>=stopHere||processing.contains(this))return Integer.MAX_VALUE/2;
		Output.logDebug("Calculation cost for "+x+" "+y);
		//TODO Work with FINISH and form
		if(this.equals(to)) {
			if(processing.size()<stopHere)stopHere=processing.size();
			Output.logDebug("Found new SH "+stopHere);
			return 1;
		}
		if(this.getStatus()==WALL||this.getStatus()==NOT_DISCOVERED) {
			return Integer.MAX_VALUE/2;
		}
		processing.push(this);
		List<Integer> costForNeighbours=neighbours.values().stream().filter(c->!c.getStatus().isDead()).map(c->c.getCost(to)).collect(Collectors.toList());
		processing.pop();
		//TODO Wrap cells when hitting top
		int co=costForNeighbours.stream().min(Comparator.comparing(Integer::valueOf)).get();
		//Output.logDebug("Cost for "+x+" "+y+": "+(ret+co));
		return ++co;
	}*/
	
	public void updateCells(Map<Direction,Status> cellStatus) {
		this.setStatus(cellStatus.get(null));
		cellStatus.entrySet().stream()
			.filter(e->e.getKey()!=null)
			.forEach(e->neighbours.get(e.getKey()).setStatus(e.getValue()));
	}
	
	public boolean isDead() {
		return status.isDead();
	}
	
	public DCAStatus getDCAStatus() {
		int deadCellsNearby=(int) neighbours.values().stream().filter(Cell::isDead).count();
		return DCAStatus.get(deadCellsNearby);
	}
	
	public Cell getNeighbour(Direction direction) {
		return neighbours.get(direction);
	}
	
	public void setNeighbour(Direction direction, Cell cell) {
		neighbours.put(direction, cell);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cell))return false;
		Cell cell=(Cell) obj;
		return cell.getX()==x&&cell.getY()==y;
	}
	
	public List<Cell> getNeighbours() {
		return new ArrayList<>(neighbours.values());
	}
	
	public List<Cell> getNotDeadNeighbours() {
		return neighbours.values().stream().filter(c->!c.getStatus().isDead()&&!c.getStatus().equals(NOT_DISCOVERED)).collect(Collectors.toList());
	}
	@Override
	public String toString() {
		return String.format("Cell [x=%s, y=%s, status=%s]", x, y, status);
	}
	
	public Direction getDirection(Cell cell) {
		Output.logDebug("Cell: "+this);
		return neighbours.entrySet().stream()
				.filter(e->{
					Output.logDebug(e.getKey()+" -> "+e.getValue());
					return e.getValue().equals(cell);
				}
				).findFirst().orElse(null).getKey();
	}
	
	//TODO Check if it works for wrap
	public int getDistance(Cell cell) {
		return Math.abs(x-cell.x)+Math.abs(y-cell.y);
	}
	
}
