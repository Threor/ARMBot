package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Status;

import java.util.*;
import java.util.stream.Collectors;

import static de.arm.bot.model.PrimitiveStatus.*;

public class LevelFourKI extends LevelThreeKI {

    private final Map<Integer, Cell> formsToLookFor;

    private List<Cell> currentFlood;

    private final List<Cell> floodedCells;

    public LevelFourKI(Maze maze) {
        super(maze);
        this.formsToLookFor = new HashMap<>();
        this.currentFlood = new ArrayList<>();
        this.floodedCells = new ArrayList<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //If FORMS are in IPSA map
        if (!formsToLookFor.isEmpty()) {
            //Start of IPSA (nothing in currentFlood)
            if (currentFlood.isEmpty()) {
                //Add old position of the form to currentFlood
                currentFlood.add(formsToLookFor.values().toArray(new Cell[0])[0]);
                //Set its status to FLOOR
                currentFlood.get(0).setStatus(new Status(FLOOR));
                //Flood all neighbours
                floodAllNeighbours();
            }
            //All cells of the currentFlood have been visited
            if (allVisited()) {
                //Flood all neighbours
                floodAllNeighbours();
            }
            Output.logDebug(currentFlood.toString());
            //Calculate a normal move action that should prioritize the IPSA cells
            return getGOAction();
        }

        if (turnInfo.getCellStatus(null).getStatus() == ENEMY_FORM) {
            if (Math.random() < 0.25) {
                Direction d = turnInfo.getCellStatus()
                        .entrySet().stream()
                        .filter(entry -> entry.getValue().getStatus() == FLOOR)
                        .map(Map.Entry::getKey)
                        .findAny()
                        .orElse(null);
                if (d != null) return new Action(Command.KICK, d);
            }
        }
        //If there is no IPSA necessary then just act normal
        return super.calculateMove(turnInfo);
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if (!super.standardProcess(turnInfo)) return false;
        turnInfo.getCellStatus().forEach((key, value) -> {
            //Nearby is a FORM cell
            if (formCells.containsValue(maze.getCurrentCell().getNeighbour(key))) {
                Output.logDebug(key + " > " + value);
                //The FORM cell is no longer a FORM cell
                if (value.getStatus() != FORM) {
                    Output.logDebug(key + " > " + value);
                    //Stream
                    formCells.entrySet().stream()
                            //Filter to find the current value and check that its not an old form that is missing
                            .filter(e -> e.getValue() != null && e.getValue().equals(maze.getCurrentCell().getNeighbour(key))&&e.getKey()>foundForms)
                            //If Present (it should be present)
                            .findFirst().ifPresent(entry -> {
                        //Remove from found formCells
                        formCells.remove(entry.getKey());
                        //Add to IPSA Map
                        formsToLookFor.put(entry.getKey(), entry.getValue());
                    });
                }
            }
            //Found a FORM
            if (value.getStatus() == FORM) {
                //If FORM is in IPSA Map remove it
                formsToLookFor.remove(value.getAdditionalInfo());
                floodedCells.forEach(c -> {
                    if (c.isVisited()) c.setVisited(false);
                });
                floodedCells.clear();
                currentFlood.clear();
            }
        });
        super.processLevelTwo(turnInfo);
        return true;
    }

    /**
     * Checks if there is no cell of the current flood that does not have the status VISITED
     *
     * @return True if all cells of the current flood have been visited
     */
    private boolean allVisited() {
        return currentFlood.stream()
                .filter(c -> !c.equals(maze.getCurrentCell()))
                .allMatch(Cell::isVisited);
    }

    /**
     * Flood all neighbour cells of the current flood cells.
     * Afterwards sets these cells as the current flood cells
     */
    private void floodAllNeighbours() {
        //Streams over the current flood
        this.currentFlood = currentFlood.stream()
                //Maps to work on the neighbour cells
                .map(cell -> {
                    //For all accessible neighbours
                    cell.getNavigableNeighbours().forEach(c -> {
                        //If the cell is not in the current flood and it is visited, then set the status to FLOOR and remember it
                        if (!currentFlood.contains(c) && c.isVisited() && !maze.getCurrentCell().equals(c)) {
                            floodedCells.add(c);
                            c.setVisited(false);
                        }
                    });
                    //Maps to the neighbours
                    return cell.getNavigableNeighbours();
                })
                //Flat maps a stream of lists to a stream of cells
                .flatMap(Collection::stream)
                //Removes all currentFlood cells
                .filter(c -> !currentFlood.contains(c) && !maze.getCurrentCell().equals(c))
                //Collects only distinct cells
                .distinct().collect(Collectors.toList());
    }

    @Override
    protected List<Cell> getBestCells() {
        if (!formsToLookFor.isEmpty()) {
            return currentFlood.stream().filter(c -> !c.isVisited()).collect(Collectors.toList());
        }
        return super.getBestCells();
    }
}
