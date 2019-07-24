package de.arm.bot.model;

import com.sun.javafx.geom.Vec2d;
import de.arm.bot.info.Direction;
import de.arm.bot.io.Output;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.arm.bot.info.Direction.*;
import static de.arm.bot.model.Status.WALL;

/**
 * A class representing the Maze with its cells and its player
 *
 * @author Team ARM
 */
public class Maze {

    /**
     * The length of the maze
     */
    private int length;
    /**
     * The height of the maze
     */
    private int height;

    /**
     * The cells which the maze contains, indexed by [x][y]
     *
     * @see de.arm.bot.model.Cell
     */
    private Cell[][] cells;

    /**
     * The player used for identifying the bots current position in the maze
     *
     * @see de.arm.bot.model.Player
     */
    private Player player;

    /**
     * The defaultly used constructor for the maze
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
                Map<Direction, Cell> neighbours = new HashMap<>();
                neighbours.put(NORTH, cells[i][j == 0 ? height - 1 : j - 1]);
                neighbours.put(SOUTH, cells[i][j >= height - 1 ? 0 : j + 1]);
                neighbours.put(WEST, cells[i == 0 ? length - 1 : i - 1][j]);
                neighbours.put(EAST, cells[i >= length - 1 ? 0 : i + 1][j]);
                Cell cell = new Cell(i, j, neighbours);
                cells[i][j] = cell;
            }
        }
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
     * Logs all discovered cells of the maze in the debug channel
     */
    public void logCells() {
        getAllDiscoveredCells()
                .forEach(c -> Output.logDebug(c.getX() + " " + c.getY() + " " + c.getStatus()));
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
                switch (cells[j][i].getStatus()) {
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
                    default:
                        temp.append("5");
                }
                temp.append(" ");
            }
            Output.logDebug(temp.toString());
        }
    }

    /**
     * Returns all discovered cells of the maze
     *
     * @return A List of all discovered cells
     */
    public List<Cell> getAllDiscoveredCells() {
        return cellStream()
                .filter(c -> !c.getStatus().equals(Status.NOT_DISCOVERED))
                .collect(Collectors.toList());
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

    public List<Cell> getCellsIn(List<Status> status) {
        return cellStream()
                .filter(c -> status.contains(c.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Cell> getPreferableCells() {
        return getCellsIn(Status.getNavigableStatus()).stream()
                .filter(c -> !c.isVisited())
                .filter(Cell::hasUndiscoveredNearby)
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
        List<Integer> calculatedCost = new ArrayList<>();
        int xDifference = Math.abs(from.getX() - to.getX());
        int yDifference = Math.abs(from.getY() - to.getY());
        calculatedCost.add(xDifference + yDifference);
        if (canWrap(NORTH, from.getX())) calculatedCost.add(xDifference + Math.abs((height + from.getY() - to.getY())));
        if (canWrap(SOUTH, from.getX())) calculatedCost.add(xDifference + Math.abs((height - from.getY()) + to.getY()));
        if (canWrap(EAST, from.getY())) calculatedCost.add(Math.abs((length - from.getX() + to.getX())) + yDifference);
        if (canWrap(WEST, from.getY())) calculatedCost.add(Math.abs((length + from.getX() - to.getX())) + yDifference);
        return calculatedCost.stream()
                .min(Comparator.comparingInt(Integer::valueOf)).orElse(Integer.MAX_VALUE);
    }

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

    public Player getPlayer() {
        return player;
    }

    private Stream<Cell> cellStream() {
        return Arrays.stream(cells)
                .flatMap(Arrays::stream);
    }

    public void performBigFlood() {
        cellStream()
            .filter(c->!c.equals(getCurrentCell())&&c.isVisited())
            .forEach(c->c.setVisited(false));
    }

    public Vec2d calculateMZVector() {
        List<Vec2d> vectors= cellStream()
                .filter(cell->cell.isVisited()|| cell.getStatus()==WALL)
                .map(this::calculateCellVector)
                .collect(Collectors.toList());
        double x=0;
        double y=0;
        for(Vec2d vec:vectors) {
            x+=vec.x;
            y+=vec.y;
        }
        return new Vec2d(x/vectors.size(),y/vectors.size());
    }

    public Vec2d calculateCellVector(Cell cell) {
        return getCurrentCell().calculateDirection(cell);
    }

    public double calculateMZScore(Vec2d mzVector, Vec2d targetCellVector) {
        double scalarProduct=mzVector.x*targetCellVector.x+mzVector.y+targetCellVector.y;
        return Math.acos(scalarProduct/(calculateLengthOfVector(mzVector)*calculateLengthOfVector(targetCellVector)));
    }

    private double calculateLengthOfVector(Vec2d vec2d) {
        return Math.sqrt(Math.pow(vec2d.x,2)+Math.pow(vec2d.y,2));
    }
}
