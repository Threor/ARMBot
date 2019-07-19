package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.HashMap;
import java.util.Map;

import static de.arm.bot.model.Status.FORM;

public class LevelFourKI extends LevelThreeKI {

    private Map<Integer, Cell> formsToLookFor;

    public LevelFourKI(Maze maze) {
        super(maze);
        this.formsToLookFor = new HashMap<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        if (!formsToLookFor.isEmpty()) {
            //TODO IPSA (Incremental Panic Search Algorithm)
        }
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
                        .filter(e -> e.getValue().equals(maze.getCurrentCell().getNeighbour(key)))
                        //If Present (it should be present)
                        .findFirst().ifPresent(entry -> {
                            //Remove from found formCells
                            formCells.remove(entry.getKey());
                            //Add to IPSA Map
                            formsToLookFor.put(entry.getKey(),entry.getValue());
                        });
                }
            }
            //Found a FORM
            if(value==FORM) {
                //If FORM is in IPSA Map remove it
                formsToLookFor.remove(value.getAdditionalInfo());
            }
        });
        return super.processTurnInfo(turnInfo);
    }
}
