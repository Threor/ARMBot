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
     * A map containing paths calculated by the A* algorithm for chosen goal cells
     */
    protected final HashMap<Cell, List<Cell>> pathToTake;
    /**
     * The maze containing all gotten information of the current maze
     */
    protected Maze maze;
    /**
     * A point containing the position, the player should reach in the next turn if his action was successful
     */
    protected Point newPosition;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    protected KI(Maze maze) {
        this.maze = maze;
        this.newPosition = maze == null ? null : new Point(maze.getCurrentPosition());
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
     * @return True if the TurnInfo could be processed successfully
     */
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        return standardProcess(turnInfo);
    }

    /**
     * The standard procedure executed every turn.
     * Updates the position if the last action was successful.
     * Also updates the status of all nearby cells
     *
     * @param turnInfo The TurnInfo of the last turn
     * @return True if the position could be updated
     */
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
     * Lets the player go towards a given direction. Updates the new position of the player and returns a GO Action for the given direction
     *
     * @param direction The direction the player is heading towards
     * @return The generated Action
     */
    private Action go(Direction direction) {
        updatePosition(direction);
        return new Action(Command.GO, direction);
    }

    /**
     * An internal function used by the A * algorithm to construct a List of Cells to visit by a map that combines neighbour cells
     *
     * @param path    The map (cell -> neighbour Cell) that describes the path
     * @param current The starting cell of the path
     * @return The constructed path as list
     */
    private List<Cell> reconstructPath(Map<Cell, Cell> path, Cell current) {
        List<Cell> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (path.containsKey(current)) {
            current = path.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }

    /**
     * An implementation of the A * algorithm that constructs a good path between the given starting cell and the given finish cell
     *
     * @param start  The starting cell of the path
     * @param finish The last cell of the path
     * @return The calculated path
     * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* algorithm</a>
     */
    protected List<Cell> aStar(Cell start, Cell finish) {
        //The set of discovered nodes that will be expanded
        Set<Cell> openSet = new HashSet<>();
        //Starting with the start cell
        openSet.add(start);
        //A map that represents the path to take with (cell -> neighbour cell)
        Map<Cell, Cell> path = new HashMap<>();
        //Maps a cell to the cost of the cheapest path from start to the cell
        Map<Cell, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);
        //Maps a cell to the expected cost of the path over this cell (gScore + estimateDistance)
        Map<Cell, Integer> fScore = new HashMap<>();
        fScore.put(start, estimateDistance(start, finish));
        //Discovered nodes are left
        while (!openSet.isEmpty()) {
            //Gets the cell in openSet with the lowest fScore value to work on
            Cell current = fScore.entrySet().stream()
                    .filter(e -> openSet.contains(e.getKey()))
                    .min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
            //When the finish cell has been reached then the path is constructed
            if (finish.equals(current)) return reconstructPath(path, current);
            //Working on this cell so it does not need to be expanded again
            openSet.remove(current);
            //Iterates over all neighbour cells of the current cell that the bot can walk on
            for (Cell neighbour : current.getNavigableNeighbours()) {
                //The neighbour is one step away, so the cost is increased by one
                int tentativeGScore = gScore.get(current) + 1;
                //A better way has been found
                if (tentativeGScore < gScore.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    path.put(neighbour, current);
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, gScore.get(neighbour) + estimateDistance(neighbour, finish));
                    openSet.add(neighbour);
                }
            }
        }
        //No path has been found
        return new ArrayList<>();
    }

    /**
     * Estimates the distance between the two given cells
     *
     * @param from The fist cell
     * @param to   The second cell
     * @return The estimated distance
     */
    protected int estimateDistance(Cell from, Cell to) {
        return maze.getDistance(from, to);
    }

    /**
     * Generates and returns the Action the bot should perform to get towards the given cell.
     * Uses the A* algorithm to navigate
     *
     * @param cell The cell to navigate to
     * @return The generated Action
     */
    protected Action navigateToCell(Cell cell) {
        //No path for this cell has been calculated
        if (pathToTake.getOrDefault(cell, new ArrayList<>()).isEmpty()) {
            //Forgets previous paths
            pathToTake.clear();
            //Calculates the path and puts it in the map
            pathToTake.put(cell, aStar(maze.getCurrentCell(), cell));
            //The first cell has to be removed because it is the cell the bot is currently standing on
            pathToTake.get(cell).remove(0);
        }
        //Removes the next cell from the path
        Cell c = pathToTake.get(cell).remove(0);
        //Return a GO action towards the direction of the next cell
        return go(maze.getCurrentCell().getDirection(c));
    }
}
