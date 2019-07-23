package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.*;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.FLOOR;
import static de.arm.bot.model.Status.FORM;

public class LevelFourKI extends LevelThreeKI {

    private Map<Integer, Cell> formsToLookFor;

    private List<Cell> currentFlood;

    private List<Cell> floodedCells;

    public LevelFourKI(Maze maze) {
        super(maze);
        this.formsToLookFor = new HashMap<>();
        this.currentFlood = new ArrayList<>();
        this.floodedCells = new ArrayList<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //If FORMS are in IPSA map
        Output.logDebug(formsToLookFor.toString());
        if (!formsToLookFor.isEmpty()) {
            Output.logDebug(currentFlood.toString());
            //Start of IPSA (nothing in currentFlood)
            if (currentFlood.isEmpty()) {
                //Add old position of the form to currentFlood
                currentFlood.add(formsToLookFor.values().toArray(new Cell[0])[0]);
                Output.logDebug(currentFlood.toString());
                //Set its status to FLOOR
                currentFlood.get(0).setStatus(FLOOR);
                Output.logDebug(currentFlood.toString());
                //Flood all neighbours
                floodAllNeighbours();
                Output.logDebug(currentFlood.toString());
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
        //If there is no IPSA necessary then just act normal
        return super.calculateMove(turnInfo);
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        //FIXME This shit is fucking ugly! FIX!
        if (!super.standardProcess(turnInfo)) return false;
        turnInfo.getCellStatus().forEach((key, value) -> {
            //Nearby is a FORM cell
            if (formCells.containsValue(maze.getCurrentCell().getNeighbour(key))) {
                Output.logDebug(key + " > " + value);
                //The FORM cell is no longer a FORM cell
                if (value != FORM) {
                    Output.logDebug(key + " > " + value);
                    //Stream
                    formCells.entrySet().stream()
                            //Filter to find the current value
                            .filter(e -> e.getValue() != null && e.getValue().equals(maze.getCurrentCell().getNeighbour(key)))
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
            if (value == FORM) {
                //If FORM is in IPSA Map remove it
                formsToLookFor.remove(value.getAdditionalInfo());
                floodedCells.forEach(c -> {
                    if (c.isVisited()) c.setVisited(false);
                });
                floodedCells.clear();
                currentFlood.clear();
            }
        });
        return super.processLevelTwo(turnInfo);
    }

    /**
     * Checks if there is no cell of the current flood that does not have the status VISITED
     *
     * @return True if all cells of the current flood have been visited
     */
    private boolean allVisited() {
        return currentFlood.stream()
                .filter(c->!c.equals(maze.getCurrentCell()))
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
                    //FIXME Floods all but should only flood outer neighbours
                    //Fixed, probably
                    //For all accessible neighbours
                    cell.getNotDeadNeighbours().forEach(c -> {
                        //If the cell is not in the current flood and it is visited, then set the status to FLOOR and remember it
                        if (!currentFlood.contains(c) && c.isVisited() &&!maze.getCurrentCell().equals(c)) {
                            floodedCells.add(c);
                            c.setVisited(false);
                        }
                    });
                    //Maps to the neighbours
                    return cell.getNotDeadNeighbours();
                })
                //Flat maps a stream of lists to a stream of cells
                .flatMap(Collection::stream)
                //Removes all currentFlood cells
                .filter(c -> !currentFlood.contains(c)&&!maze.getCurrentCell().equals(c))
                //Collects only distinct cells
                .distinct().collect(Collectors.toList());
    }

    @Override
    protected List<Cell> getBestCells() {
        if(!formsToLookFor.isEmpty()){
            return currentFlood.stream().filter(c->!c.isVisited()).collect(Collectors.toList());
        }
        return super.getBestCells();
    }
}
