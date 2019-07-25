package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.PrimitiveStatus;
import de.arm.bot.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.arm.bot.model.PrimitiveStatus.FINISH;
import static de.arm.bot.model.PrimitiveStatus.FORM;

public class LevelTwoKI extends LevelOneKI {

    protected int formCount;

    private int foundForms;

    protected Cell finish;

    protected final Map<Integer, Cell> formCells;

    private boolean performedTake;

    public LevelTwoKI(Maze maze) {
        super(maze);
        this.formCount = -1;
        this.formCells = new HashMap<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        Output.logDebug("Found: " + foundForms);
        Output.logDebug("Count: " + formCount);
        //Found all forms and on FINISH
        if (turnInfo.getCellStatus().get(null).getStatus() == FINISH && foundForms == formCount) return new Action(Command.FINISH);
        //Found all previous forms and on FORM
        if (turnInfo.getCellStatus().get(null).getStatus() == FORM && turnInfo.getCellStatus().get(null).getAdditionalInfo() == (foundForms) + 1) {
            performedTake = true;
            pathToTake.clear();
            return new Action(Command.TAKE);
        }
        //Found all forms
        if (onFinishWay()) return navigateToCell(finish);
        //Found all previous forms
        if (formCells.containsKey(foundForms + 1)) return navigateToCell(formCells.get(foundForms + 1));
        //When new forms were found, then go to them first
        return getGOAction();
    }

    protected boolean onFinishWay() {
        return foundForms == formCount && finish != null;
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if (!super.standardProcess(turnInfo)) return false;
        processLevelTwo(turnInfo);
        return true;
    }

    protected void processLevelTwo(TurnInfo turnInfo) {
        if (performedTake) {
            performedTake = false;
            if (turnInfo.getLastActionResult().isOk()) {
                foundForms++;
            }
        }
        if (turnInfo.hasCell(FINISH)) {
            Entry<Direction, Status> entry = turnInfo.getCellStatus().entrySet().stream().filter(e -> e.getValue().getStatus() == PrimitiveStatus.FINISH).findAny().orElse(null);
            if (entry == null) {
                Output.logDebug("After finding FINISH in TurnInfo, unable to get FINISH from TurnInfo!\n This should not happen!\n If it does, then you are cursed");
            } else {
                this.formCount = entry.getValue().getAdditionalInfo();
                this.finish = maze.getCurrentCell().getNeighbour(entry.getKey());
            }
        }
        if (turnInfo.hasCell(FORM)) {
            turnInfo.getCellStatus().entrySet().stream()
                    .filter(e -> e.getValue().getStatus() == FORM)
                    .forEach(e -> formCells.put(e.getValue().getAdditionalInfo(), maze.getCurrentCell().getNeighbour(e.getKey())));
        }
    }

    @Override
    protected void bigFlood() {
        List<Cell> toExclude = new ArrayList<>();
        for (Cell c : formCells.values()) {
            if (c == null) continue;
            toExclude.addAll(aStar(maze.getCurrentCell(), c));
        }
        if (finish != null) toExclude.addAll(aStar(maze.getCurrentCell(), finish));
        maze.performBigFlood(toExclude);
    }
}
