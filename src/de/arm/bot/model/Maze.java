package de.arm.bot.model;

//import com.sun.javafx.geom.Vec2d;

import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;
import de.arm.bot.model.math.Vector2d;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.arm.bot.info.Direction.*;
import static de.arm.bot.model.PrimitiveStatus.*;

/**
 * A class representing the Maze with its cells and its player
 *
 * @author Team ARM
 */
public class Maze {

    /**
     * The length of the maze
     */
    private final int length;
    /**
     * The height of the maze
     */
    private final int height;

    /**
     * The cells which the maze contains, indexed by [x][y]
     *
     * @see de.arm.bot.model.Cell
     */
    private final Cell[][] cells;

    /**
     * The player used for identifying the bots current position in the maze
     *
     * @see de.arm.bot.model.Player
     */
    private final Player player;

    /**
     * The default constructor for the maze
     *
     * @param player The Player
     * @param length The length of the maze
     * @param height The height of the maze
     */
    public Maze(Player player, int length, int height) {
        this.length = length;
        this.height = height;
        this.player = player;
        this.cells = new Cell[length][height];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell(i, j, createNeighboursMap(i,j));
                cells[i][j] = cell;
            }
        }
    }

    /**
     * Constructor for Level2 when the maze is known
     *
     * @param maze   The string representation of the maze
     * @param player The current player
     */
    public Maze(String maze, Player player) {
        String[] mazeLines = maze.split("\n");
        String[][] mazeString = Arrays.stream(mazeLines)
                .map(line -> {
                    List<String> lines = new ArrayList<>();
                    while (line.length() > 1) {
                        lines.add(line.substring(0, 2));
                        line = line.substring(2);
                    }
                    return lines.toArray(new String[0]);
                }).collect(Collectors.toList()).toArray(new String[0][0]);
        this.length = mazeString[0].length;
        this.height = mazeString.length;
        this.player = player;
        cells = new Cell[mazeString[0].length][mazeString.length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell(i, j, createNeighboursMap(i,j), Status.ofString(mazeString[j][i], player.getId()));
                cells[i][j] = cell;
            }
        }
    }

    /** Creates a map of neighbour for the cell with the specified indexes
     * @param i The i-index
     * @param j The j-index
     * @return The created map
     */
    private Map<Direction,Cell> createNeighboursMap(int i, int j) {
        Map<Direction, Cell> neighbours = new HashMap<>();
        neighbours.put(NORTH, cells[i][j == 0 ? height - 1 : j - 1]);
        neighbours.put(SOUTH, cells[i][j >= height - 1 ? 0 : j + 1]);
        neighbours.put(WEST, cells[i == 0 ? length - 1 : i - 1][j]);
        neighbours.put(EAST, cells[i >= length - 1 ? 0 : i + 1][j]);
        return neighbours;
    }

    /**
     * Method used for retrieving the cell, the player currently stands on
     *
     * @return The cell at the position playerX and playerY
     */
    public Cell getCurrentCell() {
        return cells[player.getX()][player.getY()];
    }

    /**
     * Updates the current position of the player to the newPosition
     *
     * @param newPosition The position to set
     * @return The cell the player stood on before the location was updated
     * @see java.awt.Point
     */
    public Cell updateLocation(Point newPosition) {
        Cell old = getCurrentCell();
        player.setX(newPosition.x);
        player.setY(newPosition.y);
        return old;
    }

    /**
     * Return the Point the player currently stands on
     *
     * @return The current position of the player
     * @see java.awt.Point
     */
    public Point getCurrentPosition() {
        return new Point(player.getX(), player.getY());
    }

    /**
     * Logs all cells of the maze simply, used for debugging
     */
    public void logCellsSimple() {
        for (int i = 0; i < cells[0].length; i++) {
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < cells.length; j++) {
                if (player.getX() == j && player.getY() == i) {
                    temp.append("x ");
                    continue;
                }
                switch (cells[j][i].getStatus().getStatus()) {
                    case NOT_DISCOVERED:
                        temp.append("0");
                        break;
                    case WALL:
                        temp.append("1");
                        break;
                    case FLOOR:
                        if (cells[j][i].isVisited()) {
                            temp.append("3");
                        } else temp.append("2");
                        break;
                    case FINISH:
                        temp.append("4");
                        break;
                    case FORM:
                        temp.append("5");
                        break;
                    default:
                        temp.append("6");
                }
                temp.append(" ");
            }
            Output.logDebug(temp.toString());
        }
    }

    /**
     * Getter for the attribute length
     *
     * @return The length of the maze
     */
    public int getLength() {
        return length;
    }

    /**
     * Getter for the attribute height
     *
     * @return The height of the maze
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets and returns all cells that have on of the the given status
     *
     * @param primitiveStatuses The given status
     * @return The specified list
     */
    public List<Cell> getCellsIn(List<PrimitiveStatus> primitiveStatuses) {
        return cellStream()
                .filter(c -> primitiveStatuses.contains(c.getStatus().getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets and returns all navigable cells that have not been visited and have undiscovered cells nearby
     *
     * @return The specified list
     */
    public List<Cell> getPreferableCells() {
        return getCellsIn(PrimitiveStatus.getNavigableStatus()).stream()
                .filter(c -> !getCurrentCell().equals(c) && !c.isVisited() && c.hasUndiscoveredNearby())
                .collect(Collectors.toList());
    }

    /**
     * Calculates the estimated distance between the two given cells.
     * Also calculates the distance using the wrap functionality if it is possible to wrap.
     * If it is possible to wrap on all for directions, five different distances will be calculated
     * Returned will be the lowest calculated distance
     *
     * @return The calculated lowest distance
     */
    public int getDistance(Cell from, Cell to) {
        int xCost = from.getX() == to.getX() ? 0 : Integer.MAX_VALUE / 2;
        int yCost = from.getY() == to.getY() ? 0 : Integer.MAX_VALUE / 2;
        if (from.getX() > to.getX() || canWrap(WEST, from.getX())) {
            int cost = calculateDistance(from, to, WEST);
            if (cost < xCost) xCost = cost;
        }
        if (from.getX() < to.getX() || canWrap(EAST, from.getX())) {
            int cost = calculateDistance(from, to, EAST);
            if (cost < xCost) xCost = cost;
        }
        if (from.getY() > to.getY() || canWrap(NORTH, from.getY())) {
            int cost = calculateDistance(from, to, NORTH);
            if (cost < yCost) yCost = cost;
        }
        if (from.getY() < to.getY() || canWrap(SOUTH, from.getY())) {
            int cost = calculateDistance(from, to, SOUTH);
            if (cost < yCost) yCost = cost;
        }
        return xCost + yCost;
    }

    /**
     * Calculates the distance (x or y) between the given cells using the given direction
     *
     * @param from      The first cell
     * @param to        The second cell
     * @param direction The given direction
     * @return The calculated distance
     */
    private int calculateDistance(Cell from, Cell to, Direction direction) {
        int ret = 0;
        if (direction == NORTH || direction == SOUTH) {
            while (from.getY() != to.getY()) {
                from = from.getNeighbour(direction);
                ret += from.getStatus().getCost();
            }
        } else {
            while (from.getX() != to.getX()) {
                from = from.getNeighbour(direction);
                ret += from.getStatus().getCost();
            }
        }
        return ret;
    }

    /**
     * Checks whether it is possible to wrap (the last cell of the specified row/column is navigable)
     *
     * @param direction The given Direction
     * @param on        The index of the row/column to check
     * @return True, if it is possible to wrap
     */
    private boolean canWrap(Direction direction, int on) {
        switch (direction) {
            case NORTH:
                return cells[on][0].getStatus().isNavigable();
            case EAST:
                return cells[length - 1][on].getStatus().isNavigable();
            case SOUTH:
                return cells[on][height - 1].getStatus().isNavigable();
            case WEST:
                return cells[0][on].getStatus().isNavigable();
            default:
                return false;
        }
    }

    /**
     * Getter for the attribute Player
     *
     * @return The Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Creates a stream of all cells of this maze
     *
     * @return The created stream
     */
    private Stream<Cell> cellStream() {
        return Arrays.stream(cells)
                .flatMap(Arrays::stream);
    }

    /**
     * Performs a big flood (forgets the status of a good amount of cells)
     */
    public void performBigFlood() {
        performBigFlood(new ArrayList<>());
    }

    /**
     * Performs a big flood but excludes the given cells
     *
     * @param toExclude The cells to exclude
     */
    public void performBigFlood(List<Cell> toExclude) {
        cellStream()
                .filter(c -> !c.equals(getCurrentCell()) && c.isVisited())
                .forEach(c -> {
                    c.setVisited(false);
                    if (c.getStatus().getStatus() == FLOOR && !toExclude.contains(c) && getDistance(getCurrentCell(), c) > 1) {
                        c.setStatus(new Status(NOT_DISCOVERED));
                    }
                });
    }

    /**
     * Calculates the current MZVector as defined in the MZA
     *
     * @return The calculated MZVector
     */
    public Vector2d calculateMZVector() {
        return cellStream()
                .filter(cell -> !(cell.isVisited() || cell.getStatus().getStatus() == WALL))
                .map(this::calculateCellVector)
                .collect(Collector.of(Vector2d::new, (Vector2d::add), Vector2d::add));
    }

    /**
     * Calculates the vector between the current cell and the given cell
     *
     * @param cell The given cell
     * @return The calculated vector
     */
    public Vector2d calculateCellVector(Cell cell) {
        return getCurrentCell().calculateDirection(cell).norm();
    }

    /**
     * Calculates and return the MZScore as defined by the MZA with the given MZVector and the given TCVector
     *
     * @param mzVector         The given MZVector
     * @param targetCellVector The given TCVector
     * @return The calculated score
     */
    public double calculateMZScore(Vector2d mzVector, Vector2d targetCellVector) {
        return Math.acos(mzVector.dotProduct(targetCellVector) / (mzVector.getLength() * targetCellVector.getLength()));
    }

    /**
     * Adjusts the form data for this maze if it is performing on level 2 and maze 3 or 9
     *
     * @param westStatus The status that indicates on which maze is currently played
     * @return The number of added forms
     */
    public int adjustForLevel3or9(Status westStatus) {
        if (player.getId() == 3) {
            if (westStatus.equals(FORM)) {
                cells[4][2].setStatus(new Status(FORM, 2));
                return 1;
            } else {
                cells[1][2].setStatus(new Status(FORM, 1));
                cells[4][8].setStatus(new Status(FORM, 2));
                return 2;
            }
        }
        if (player.getId() == 4) {
            if (westStatus.equals(FORM)) {
                cells[4][8].setStatus(new Status(FORM, 2));
                return 1;
            } else {
                cells[1][8].setStatus(new Status(FORM, 1));
                cells[4][2].setStatus(new Status(FORM, 2));
                return 2;
            }
        }
        return 0;
    }
}
