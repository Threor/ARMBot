package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import static de.arm.bot.model.Status.ENEMY_FORM;
import static de.arm.bot.model.Status.SHEET;

public class LevelFiveKI extends LevelFourKI{

    private boolean took;

    private boolean put;

    private List<Cell> alreadyPut;

    public LevelFiveKI(Maze maze) {
        super(maze);
        this.alreadyPut=new ArrayList<>();
    }

    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //TODO Possible bugs: form got kicked to alreadyPut, but it's unlikely that it will happen
        if(alreadyPut.contains(maze.getCurrentCell()))return super.calculateMove(turnInfo);
        if(turnInfo.getCellStatus().get(null)==ENEMY_FORM) {
            if(maze.getPlayer().getSheetCount()>0){
                put=true;
                return new Action(Command.PUT);
            }
        }
        if(turnInfo.getCellStatus().get(null)==SHEET) {
            took=true;
            return new Action(Command.TAKE);
        }
        return super.calculateMove(turnInfo);
    }

    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if(turnInfo.getLastActionResult().isOk()) {
            if(took) {
                took=false;
                maze.getPlayer().addSheet();
            }
            if(put) {
                put=false;
                alreadyPut.add(maze.getCurrentCell());
                maze.getPlayer().removeSheet();
            }

        }
        return super.processTurnInfo(turnInfo);
    }
}
