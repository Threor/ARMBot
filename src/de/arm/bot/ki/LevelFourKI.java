package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.*;
import java.util.stream.Collectors;

import static de.arm.bot.model.Status.*;

public class LevelFourKI extends LevelThreeKI {

    private Map<Integer, Cell> formsToLookFor;

    private List<Cell> currentFlood;

    public LevelFourKI(Maze maze) {
        super(maze);
        this.formsToLookFor = new HashMap<>();
        this.currentFlood = new ArrayList<>();
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
                currentFlood.get(0).setStatus(FLOOR);
                //Flood all neighbours
                floodAllNeighbours();
            }
            //All cells of the currentFlood have been visited
            if (allVisited()) {
                //Flood all neighbours
                floodAllNeighbours();
            }
            //Calculate a normal move action that should prioritize the IPSA cells
            return getGOAction();
        }
        //If there is no IPSA necessary then just act normal
        return super.calculateMove(turnInfo);
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        turnInfo.getCellStatus().forEach((key, value) -> {
            //Nearby is a FORM cell
            if (formCells.containsValue(maze.getCurrentCell().getNeighbour(key))) {
                //The FORM cell is no longer a FORM cell
                if (value != FORM) {
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
            }
        });
        return super.processTurnInfo(turnInfo);
    }

    /**
     * Checks if there is no cell of the current flood that does not have the status VISITED
     *
     * @return True if all cells of the current flood have been visited
     */
    private boolean allVisited() {
        return currentFlood.stream()
                .noneMatch(c -> c.getStatus() != VISITED);
    }

    /**
     *
     */
    private void floodAllNeighbours() {
        this.currentFlood = currentFlood.stream()
                .map(cell -> {
                    cell.getNotDeadNeighbours().forEach(c -> {
                        if (c.getStatus() == VISITED) c.setStatus(FLOOR);
                    });
                    return cell.getNotDeadNeighbours();
                })
                .flatMap(Collection::stream).collect(Collectors.toList());
    }
}
