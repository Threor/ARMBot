package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.FINISH;

public class LevelOneKI extends KI {

    public LevelOneKI(Maze maze) {
        super(maze);
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        super.processTurnInfo(turnInfo);
        //maze.logCellsSimple();
        if (turnInfo.getCellStatus().get(null) == FINISH) {
            return new Action(Command.FINISH);
        }
        if (maze.getCurrentCell().hasFinishNearby()) {
            return navigateToCell(maze.getCellsIn(Collections.singletonList(FINISH)).get(0));
        }
        return getGOAction();
    }

    protected Action getGOAction() {
        if (pathToTake.size() > 0) {
            Cell cell = (pathToTake.keySet().iterator().next());
            if (pathToTake.get(cell).size() > 1) return navigateToCell(cell);
        }
        Output.logDebug("Current paths: " + pathToTake.size());
        List<Cell> toSearchFor = maze.getPreferableCells();
        int minCost = toSearchFor.stream()
                .map(c -> estimateDistance(maze.getCurrentCell(), c))
                .min(Comparator.comparingInt(i -> i == 0 ? Integer.MAX_VALUE : i)).orElse(-1);
        List<Cell> bestCells = toSearchFor.stream()
                .filter(c -> estimateDistance(maze.getCurrentCell(), c) == minCost).collect(Collectors.toList());
        if (bestCells.size() > 1) {
            int min = bestCells.stream().map(Cell::getNotDiscoveredNeighbourCount).min(Comparator.comparingInt(Integer::valueOf)).orElse(-1);
            bestCells = bestCells.stream().filter(c -> c.getNotDiscoveredNeighbourCount() == min).collect(Collectors.toList());
        }
        //TODO MZA
        //FIXME Error handling
        if (bestCells.size() == 0) {
            Output.logDebug("ERROR! Couldn't find goal cell!");
            Output.logDebug("This is probably caused by all cells being already visited");
            Output.logDebug("If this happens in level 4+ a big flood should happen");
        }
        return navigateToCell(bestCells.get(ThreadLocalRandom.current().nextInt(0, bestCells.size())));
    }

}
