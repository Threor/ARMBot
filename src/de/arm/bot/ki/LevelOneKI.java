package de.arm.bot.ki;

//import com.sun.javafx.geom.Vec2d;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.math.Vector2d;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static de.arm.bot.model.PrimitiveStatus.FINISH;

/**
 * The implementation of KI used for level one
 *
 * @author Team ARM
 */
public class LevelOneKI extends KI {

    /**
     * The calculated vector used by the MZA (Markertscher ZiehAlgorithmus)
     */
    private Vector2d mzVector;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    public LevelOneKI(Maze maze) {
        super(maze);
    }

    /**
     * Calculates the next move the bot should take.
     * If the bot stands on a finish cell he will try to finish.
     * Otherwise he will look for the finish cell
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The calculated Action
     */
    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        if (turnInfo.getCellStatus(null).getStatus() == FINISH) {
            return new Action(Command.FINISH);
        }
        if (maze.getCurrentCell().hasFinishNearby()) {
            return navigateToCell(maze.getCellsIn(Collections.singletonList(FINISH)).get(0));
        }
        return getGOAction();
    }

    /**
     * As the heart of every bot this method will be used to explore the maze in order to gather new Information.
     * This is accomplished by choosing a target cell based on heuristics and then calculating a path towards it
     *
     * @return The next Action to take
     */
    protected Action getGOAction() {
        //I have a path im currently on
        if (pathToTake.size() > 0) {
            //Get the first finish cell
            Cell cell = (pathToTake.keySet().iterator().next());
            //If there is a valid (not empty) path for this cell then use this cell
            if (pathToTake.get(cell).size() > 0) return navigateToCell(cell);
        }
        //Calculates the new mzVector
        this.mzVector = maze.calculateMZVector();
        //Finds all possible cells the bot could go towards
        List<Cell> toSearchFor = getBestCells();
        //The bot has nowhere to go
        if (toSearchFor.size() == 0) {
            Output.logDebug("ERROR! Couldn't find goal cell!");
            Output.logDebug("This is probably caused by all cells being already visited");
            Output.logDebug("Performing big flood");
            //Perform a big flood big forgetting information
            bigFlood();
            maze.logCellsSimple();
            Output.logDebug("Performed big flood");
            Output.logDebug("New calculation engaged!");
            //Let's try again
            return getGOAction();
        }
        //Map all cells with there heuristic cost
        Map<Cell, Double> heuristicCostToCell = toSearchFor.stream()
                .collect(Collectors.toMap(cell -> cell, this::calculateHeuristicCost));
        //Calculate the minimal cost
        double minCost = heuristicCostToCell.values().stream().min(Comparator.comparingDouble(Double::valueOf)).orElse(0d);
        //Only get the cells with best cost
        List<Cell> possibleCells = heuristicCostToCell.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == minCost)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        //Return one of the best cells randomly
        return navigateToCell(possibleCells.get(ThreadLocalRandom.current().nextInt(0, possibleCells.size())));
    }

    /**
     * Calculates the heuristic cost of a cell.
     * The heuristic cost is defined by: (hdist-mzscore*1,25-ndnc*0.5), with hdist beeing the estimated distance, mzscore being the calculated MZScore and ndnc being the count of not discovered neighbours of the given cell)
     *
     * @param cell The given cell
     * @return The calculated cost
     */
    private double calculateHeuristicCost(Cell cell) {
        return estimateDistance(maze.getCurrentCell(), cell) - (calculateMZScore(cell) * 1.25) - (cell.getNotDiscoveredNeighbourCount() * 0.5);
    }

    /**
     * Calculates the MZScore of a given cell as defined by the MZA
     *
     * @param cell The given cell
     * @return The calculated score
     */
    private double calculateMZScore(Cell cell) {
        return maze.calculateMZScore(mzVector, maze.calculateCellVector(cell));
    }

    /**
     * Performs a "big flood".
     * A big flood is used when the whole maze has been explored but there are still forms or finish cells to be found-.
     * The big flood forgets some information in order for the getGoAlgorithm to search for these missing cells
     */
    protected void bigFlood() {
        maze.performBigFlood();
    }

    /**
     * Finds and returns the cells the getGoAlgorithm should search for
     *
     * @return The best cells for the algorithm to visit
     */
    protected List<Cell> getBestCells() {
        return maze.getPreferableCells();
    }

}
