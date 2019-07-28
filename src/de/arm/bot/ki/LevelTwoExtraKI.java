package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.*;

import java.util.Collections;
import java.util.List;

import static de.arm.bot.info.Direction.WEST;
import static de.arm.bot.model.PrimitiveStatus.*;

/**
 * A special implementation of the KI used for Level 2 that operates on known mazes.
 * We, as Team ARM are sorry for using this kind of technology. We know, that we will hurt many other teams with this step.
 * But in order to remain competitive we are forced to do this. Again, we are sorry.
 * We know, that with this step we will sell our souls to the devil.
 * But at least it is warm down there.
 */
public class LevelTwoExtraKI extends LevelTwoKI {

    /**
     * The length of the maze that will be used if it is not possible to recognize the maze before the first turn.
     */
    private int temporaryLength;

    /**
     * The player instance that will be used if the maze could not be identified before the first turn
     */
    private Player temporaryPlayer;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze.
     * Also retrieves known information from the game
     *
     * @param maze The maze the KI should work on
     */
    public LevelTwoExtraKI(Maze maze) {
        super(maze);
        retrieveMazeInfos();
    }

    /**
     * Constructor used if the maze can only be loaded during the first turn
     *
     * @param length The known length of the maze
     * @param player The known player instance for this maze
     */
    public LevelTwoExtraKI(int length, Player player) {
        super(null);
        this.temporaryPlayer = player;
        this.temporaryLength = length;
    }

    /**
     * Retries information about forms and finish cells from the given maze.
     * Used if the maze is known and could be loaded
     */
    private void retrieveMazeInfos() {
        List<Cell> cells = maze.getCellsIn(Collections.singletonList(FORM));
        this.formCount = cells.size();
        cells.forEach(c -> formCells.put(c.getStatus().getAdditionalInfo(), c));
        this.finish = maze.getCellsIn(Collections.singletonList(FINISH)).get(0);
    }

    /**
     * Processes the given TurnInfo and splices the information into the maze.
     * Afterwards calculates the next action and returns it.
     * If the maze the bot is working on could not be identified before the first turn then during the first turn it will be identified here.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The generated Next action
     */
    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        if (temporaryPlayer != null) {
            //3 or 9
            if (temporaryLength == 11) {
                this.maze = new Maze(Mazes.MAZE3AND9, temporaryPlayer);
                formCount += maze.adjustForLevel3or9(turnInfo.getCellStatus().get(WEST));
                retrieveMazeInfos();
                temporaryPlayer = null;
            }
            //4 or 5
            if (temporaryLength == 10) {
                //The maze 4 has a finish cell of an enemy nearby, the maze 5 does not
                if (turnInfo.getCellStatus().values().contains(new Status(ENEMY_FINISH))) {
                    this.maze = new Maze(Mazes.MAZE4, temporaryPlayer);
                } else {
                    this.maze = new Maze(Mazes.MAZE5, temporaryPlayer);
                }
                retrieveMazeInfos();
                temporaryPlayer = null;
            }
            newPosition = maze.getCurrentPosition();
        }
        return super.generateNextTurn(turnInfo);
    }
}
