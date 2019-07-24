package de.arm.bot.ki;

import com.sun.javafx.geom.Vec2d;
import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.FINISH;

public class LevelOneKI extends KI {

    private Vec2d mzVector;

    public LevelOneKI(Maze maze) {
        super(maze);
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //super.processTurnInfo(turnInfo);
        if (turnInfo.getCellStatus().get(null) == FINISH) {
            return new Action(Command.FINISH);
        }
        if (maze.getCurrentCell().hasFinishNearby()) {
            return navigateToCell(maze.getCellsIn(Collections.singletonList(FINISH)).get(0));
        }
        return getGOAction();
    }

    protected Action getGOAction() {
        this.mzVector=maze.calculateMZVector();
        Output.logDebug("MZ Vector: "+mzVector);
        if (pathToTake.size() > 0) {
            Cell cell = (pathToTake.keySet().iterator().next());
            if (pathToTake.get(cell).size() > 1) return navigateToCell(cell);
        }
        Output.logDebug("Current paths: " + pathToTake.size());
        List<Cell> toSearchFor = getBestCells();
        if (toSearchFor.size() == 0) {
            Output.logDebug("ERROR! Couldn't find goal cell!");
            Output.logDebug("This is probably caused by all cells being already visited");
            Output.logDebug("Performing big flood");
            bigFlood();
            Output.logDebug("Performed big flood");
            Output.logDebug("New calculation engaged!");
            return getGOAction();
        }
        Map<Cell,Double> heuristicCostToCell=toSearchFor.stream()
                .collect(Collectors.toMap(cell->cell,this::calculateHeuristicCost));
        double minCost = heuristicCostToCell.values().stream().min(Comparator.comparingDouble(Double::valueOf)).orElse(0d);
        List<Cell> possibleCells=heuristicCostToCell.entrySet()
                .stream()
                .filter(entry->entry.getValue()==minCost)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        //Output.logDebug(possibleCells.size()+" cost: "+minCost);
        return navigateToCell(possibleCells.get(ThreadLocalRandom.current().nextInt(0,possibleCells.size())));
    }

    private double calculateHeuristicCost(Cell cell) {
        return (estimateDistance(maze.getCurrentCell(),cell)+0.5*cell.getNotDiscoveredNeighbourCount())*(calculateMZScore(cell)/180+0.5);
    }

    private double calculateMZScore(Cell cell) {
        return maze.calculateMZScore(mzVector,maze.calculateCellVector(cell));
    }

    private void bigFlood() {
        maze.performBigFlood();
    }

    protected List<Cell> getBestCells() {
        return maze.getPreferableCells();
    }

}
