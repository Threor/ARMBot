package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.ArrayList;
import java.util.List;

import static de.arm.bot.info.Command.PUT;
import static de.arm.bot.info.Command.TAKE;
import static de.arm.bot.model.PrimitiveStatus.ENEMY_FORM;
import static de.arm.bot.model.PrimitiveStatus.SHEET;

public class LevelFiveKI extends LevelFourKI {

    private boolean took;

    private boolean put;

    private final List<Cell> alreadyPut;

    public LevelFiveKI(Maze maze) {
        super(maze);
        this.alreadyPut = new ArrayList<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        if (alreadyPut.contains(maze.getCurrentCell()) || onFinishWay()) return super.calculateMove(turnInfo);
        if (turnInfo.getCellStatus().get(null).getStatus() == ENEMY_FORM) {
            if (maze.getPlayer().getSheetCount() > 0) {
                put = true;
                return new Action(PUT);
            }
        }
        if (turnInfo.getCellStatus().get(null).getStatus() == SHEET) {
            took = true;
            return new Action(TAKE);
        }
        return super.calculateMove(turnInfo);
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if (turnInfo.getLastActionResult().isOk()) {
            if (took) {
                took = false;
                maze.getPlayer().addSheet();
            }
            if (put) {
                put = false;
                alreadyPut.add(maze.getCurrentCell());
                maze.getPlayer().removeSheet();
            }
        }
        return super.processTurnInfo(turnInfo);
    }

    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        if (!turnInfo.getLastActionResult().isOk() && turnInfo.getLastActionResult().getMessage().equalsIgnoreCase("taking"))
            return lastAction;
        return super.generateNextTurn(turnInfo);
    }

    @Override
    protected void bigFlood() {
        alreadyPut.clear();
        super.bigFlood();
    }
}
