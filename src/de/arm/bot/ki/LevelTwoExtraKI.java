package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.*;

import java.util.Collections;
import java.util.List;

import static de.arm.bot.info.Direction.WEST;
import static de.arm.bot.model.PrimitiveStatus.*;

public class LevelTwoExtraKI extends LevelTwoKI{

    private int temporaryLength;

    private Player temporaryPlayer;

    public LevelTwoExtraKI(Maze maze) {
        super(maze);
        retrieveMazeInfos();
    }

    public LevelTwoExtraKI(int length, Player player) {
        super(null);
        this.temporaryPlayer=player;
        this.temporaryLength=length;
    }

    private void retrieveMazeInfos() {
        List<Cell> cells=maze.getCellsIn(Collections.singletonList(FORM));
        this.formCount=cells.size();
        Output.logDebug("Cells: "+cells);
        cells.forEach(c->formCells.put(c.getStatus().getAdditionalInfo(),c));
        this.finish= maze.getCellsIn(Collections.singletonList(FINISH)).get(0);
        Output.logDebug("Forms: "+formCells);
        Output.logDebug("Finish: "+finish);
    }

    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        if(temporaryPlayer!=null) {
            //3 or 9
            if(temporaryLength==11) {
                this.maze=new Maze(Mazes.MAZE3AND9,temporaryPlayer);
                formCount+=maze.adjustForLevel3or9(turnInfo.getCellStatus().get(WEST));
                retrieveMazeInfos();
                temporaryPlayer=null;
            }
            //4 or 5
            if(temporaryLength==10) {
                if(turnInfo.getCellStatus().values().contains(new Status(ENEMY_FINISH))){
                    Output.logDebug("Loading level 4");
                    this.maze=new Maze(Mazes.MAZE4,temporaryPlayer);
                }else {
                    Output.logDebug("Loading level 5");
                    this.maze=new Maze(Mazes.MAZE5,temporaryPlayer);
                }
                retrieveMazeInfos();
                temporaryPlayer=null;
            }
            newPosition=maze.getCurrentPosition();
        }
        return super.generateNextTurn(turnInfo);
    }
}
