package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * An abstract class containing the basic fields and methods for implementing the KI for each level.
 * Every used KI should be a child of this class
 *
 * @author Team ARM
 */
public abstract class KI {

    /**
     * The maze containing all gotten information of the current maze
     */
    protected Maze maze;

    /**
     * A point containing the position, the player should reach in the next turn if his action was successful
     */
    private Point newPosition;

    /**
     * A map containing paths calculated by the A* algorithm for chosen goal cells
     */
    protected HashMap<Cell, List<Cell>> pathToTake;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    protected KI(Maze maze) {
        this.maze = maze;
        this.newPosition = new Point(maze.getCurrentPosition());
        this.pathToTake = new HashMap<>();
    }

    /**
     * Calculates the next move based on the current status and the given TurnInfo the bot should take.
     * This is basically the brain that decides, which action the bot should take next.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The calculated Action
     */
    protected abstract Action calculateMove(TurnInfo turnInfo);

    /**
     * Processes the given TurnInfo and splices the information into the maze.
     * Afterwards calculates the next action and returns it
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The generated Next action
     */
    public Action generateNextTurn(TurnInfo turnInfo) {
        processTurnInfo(turnInfo);
        return calculateMove(turnInfo);
    }

    /**
     * Processes the given TurnInfo and updates the information
     * May be overwritten in later implementations to consider future features and mechanics
     *
     * @param turnInfo The information of the current Turn as given by the game
     */
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        return standardProcess(turnInfo);
    }

    protected final boolean standardProcess(TurnInfo turnInfo) {
        if (turnInfo.getLastActionResult().isOk() && newPosition != null) {
            Cell cell = maze.updateLocation(newPosition);
            cell.setVisited(true);
            maze.getCurrentCell().updateCells(turnInfo.getCellStatus());
            return true;
        } else {
            Output.logDebug("The last Action has failed!\nThat wasn't supposed to happen!\n" + turnInfo.getLastActionResult());
            return false;
        }
    }

    /**
     * Updates the position the player should reach, if the action is successful. Also validates the new position
     *
     * @param direction The direction the player is heading to
     */
    private void updatePosition(Direction direction) {
        Point current = maze.getCurrentPosition();
        switch (direction) {
            case NORTH:
                newPosition = new Point(current.x, current.y - 1);
                break;
            case EAST:
                newPosition = new Point(current.x + 1, current.y);
                break;
            case WEST:
                newPosition = new Point(current.x - 1, current.y);
                break;
            case SOUTH:
                newPosition = new Point(current.x, current.y + 1);
                break;
            default:
                Output.logDebug("Critical error, " + direction + " is unknown");
        }
        validatePosition();
    }

    /**
     * Validates the new position the player should reach.
     * This is used so that the player can wrap around the maze, meaning exiting on one side and appearing on the opposite one.
     */
    private void validatePosition() {
        if (newPosition.x < 0) newPosition.x += maze.getLength();
        if (newPosition.y < 0) newPosition.y += maze.getHeight();
        if (newPosition.x > maze.getLength() - 1) newPosition.x -= maze.getLength();
        if (newPosition.y > maze.getHeight() - 1) newPosition.y -= maze.getHeight();
    }

    /**
     * Getter for the attribute Maze
     *
     * @return The current Maze
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Lets the player go towards a given direction. Updates the new position of the player and returns a GO Action for the given direction
     *
     * @param direction The direction the player is heading towards
     * @return The generated Action
     */
    private Action go(Direction direction) {
        updatePosition(direction);
        Output.logDebug("Going " + direction);
        return new Action(Command.GO, direction.toString());
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> path, Cell current) {
        List<Cell> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (path.containsKey(current)) {
            current = path.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }

    private List<Cell> aStar(Cell start, Cell finish) {
        Output.logDebug("Start: " + start + "\nZiel: " + finish);
        Set<Cell> openSet = new HashSet<>();
        openSet.add(start);
        Map<Cell, Cell> path = new HashMap<>();
        Map<Cell, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);
        Map<Cell, Integer> fScore = new HashMap<>();
        fScore.put(start, estimateDistance(start, finish));
        while (!openSet.isEmpty()) {
            Cell current = fScore.entrySet().stream().filter(e -> openSet.contains(e.getKey()))
                    .min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
            if (finish.equals(current)) return reconstructPath(path, current);
            openSet.remove(current);
            for (Cell neighbour : current.getNotDeadNeighbours()) {
                int tentativeGScore = gScore.get(current) + 1;
                if (tentativeGScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    path.put(neighbour, current);
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, gScore.get(neighbour) + estimateDistance(neighbour, finish));
                    openSet.add(neighbour);
                }
            }
        }
        return null;
    }

    protected int estimateDistance(Cell from, Cell to) {
        return maze.getDistance(from, to);
    }

    protected Action navigateToCell(Cell cell) {
        if (pathToTake.getOrDefault(cell, new ArrayList<>()).isEmpty()) {
            pathToTake.clear();
            pathToTake.put(cell, aStar(maze.getCurrentCell(), cell));
            Output.logDebug("Calculated path: " + pathToTake.get(cell));
            pathToTake.get(cell).remove(0);
        }
        Cell c = pathToTake.get(cell).remove(0);
        return go(maze.getCurrentCell().getDirection(c));
    }
}
