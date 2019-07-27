package de.arm.bot.model;

//import com.sun.javafx.geom.Vec2d;

import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;
import de.arm.bot.model.math.Vector2d;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.arm.bot.model.PrimitiveStatus.FINISH;
import static de.arm.bot.model.PrimitiveStatus.NOT_DISCOVERED;

/**
 * A class representing a cell of the maze
 *
 * @author Team ARM
 */
public class Cell {

    /**
     * The x-coordinate of the maze
     */
    private final int x;
    /**
     * The y-coordinate of the maze
     */
    private final int y;

    /**
     * The current primitiveStatus of the cell
     */
    private Status status;

    private boolean visited;

    /**
     * A map of all neighbours of the cell, mapped as
     * Direction to Cell
     *
     * @see de.arm.bot.info.Direction
     */
    private final Map<Direction, Cell> neighbours;

    /**
     * The defaultly used constructor for the class cell
     * Additionally to setting all attributes it links all neighbour cells
     *
     * @param x          The current x-coordinate of the cell
     * @param y          The current y-coordinate of the cell
     * @param neighbours A map of all neighbour cells of the current cell
     * @see PrimitiveStatus
     */
    Cell(int x, int y, Map<Direction, Cell> neighbours) {
        this(x, y, neighbours, new Status(NOT_DISCOVERED));
    }

    Cell(int x, int y, Map<Direction, Cell> neighbours, Status status) {
        this.x = x;
        this.y = y;
        this.status = status;
        this.neighbours = neighbours;
        neighbours.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().getNeighbour(e.getKey().getOpposite()) == null)
                .forEach(e -> e.getValue().setNeighbour(e.getKey().getOpposite(), this));
    }

    /**
     * Getter for the attribute x
     *
     * @return The current x-coordinate of the cell
     */
    int getX() {
        return x;
    }

    /**
     * Getter for the attribute y
     *
     * @return The current y-coordinate of the cell
     */
    int getY() {
        return y;
    }

    /**
     * Getter for the attribute status
     *
     * @return status The current primitiveStatus of the cell
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter for the attribute Status, does not set if the cell is already visited
     *
     * @param status The new Status of the cell
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Updates the primitiveStatus for the current cell and for all neighbours
     *
     * @param cellStatus The map of the new primitiveStatus of all neighbour cells and the current cell (null as direction indicates the current cell)
     */
    public void updateCells(Map<Direction, Status> cellStatus) {
        this.setStatus(cellStatus.get(null));
        cellStatus.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .forEach(e -> neighbours.get(e.getKey()).setStatus(e.getValue()));
    }

    /**
     * Gets and return the neighbour cell of the current cell in the given direction
     *
     * @param direction The direction of the neigbour cell
     * @return The gotten neighbour
     */
    public Cell getNeighbour(Direction direction) {
        return neighbours.get(direction);
    }

    /**
     * Sets a new neighbour of the cell on the given direction
     *
     * @param direction The direction on which the new cell should be set
     * @param cell      The cell to be set
     */
    private void setNeighbour(Direction direction, Cell cell) {
        neighbours.put(direction, cell);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) return false;
        Cell cell = (Cell) obj;
        return cell.getX() == x && cell.getY() == y;
    }

    /**
     * Gets and returns all neighbours of the cell that the bot can wolk on
     *
     * @return A list of all neighbour cells the bot can walk on
     * @see PrimitiveStatus
     */
    public List<Cell> getNavigableNeighbours() {
        return neighbours.values().stream()
                .filter(c -> c.getStatus().isNavigable())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("Cell [x=%s, y=%s, primitiveStatus=%s, visited=%s]", x, y, status, visited);
    }

    /**
     * Gets the direction the given cell is currently on, if the cell is a neighbour cell
     *
     * @param cell The cell to get the direction from
     * @return The calculated direction
     */
    public Direction getDirection(Cell cell) {
        Output.logDebug("Cell: " + this);
        if (!neighbours.containsValue(cell)) return null;
        Map.Entry<Direction, Cell> ret = neighbours.entrySet().stream()
                .filter(e -> e.getValue().equals(cell)).findFirst().orElse(null);
        if (ret == null) {
            Output.logDebug("ERR Couldn't get Direction from " + cell + " to " + this + "!");
            return null;
        }
        return ret.getKey();
    }

    /**
     * Checks whether there is a FINISH cell nearby this cell. Only used by the Level 1 algorithm
     *
     * @return True if one of the neighbour cells of this cell is a FINISH cell
     * @see PrimitiveStatus
     */
    public boolean hasFinishNearby() {
        return hasNearby(FINISH);
    }

    /**
     * Checks whether there is a NOT_DISCOVERED cell nearby this cell.
     * Used to find goal cells for the bot to move towards
     *
     * @return True if one of the neighbour cells of this cell is a NOT_DISOVERED cell
     */
    boolean hasUndiscoveredNearby() {
        return hasNearby(NOT_DISCOVERED);
    }

    private boolean hasNearby(PrimitiveStatus primitiveStatus) {
        return neighbours.values().stream().anyMatch(c -> c.getStatus().getStatus() == primitiveStatus);
    }

    /**
     * @return
     */
    public int getNotDiscoveredNeighbourCount() {
        return (int) neighbours.values().stream().filter(c -> c.getStatus().getStatus() == NOT_DISCOVERED).count();
    }

    public Vector2d calculateDirection(Cell towards) {
        return new Vector2d(towards.x - x, towards.y - y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
