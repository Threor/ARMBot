package de.arm.bot.info;

import de.arm.bot.io.Output;
import de.arm.bot.ki.*;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Mazes;
import de.arm.bot.model.Player;

/**
 * A wrapper class that holds all information given on the start of the game.
 * These information will be used to generate the maze, the player and to initialize the KI.
 */
public class InitInfo {

    /**
     * The length of the maze
     */
    private final int mazeLength;

    private final int mazeHeight;

    private final int mazeLevel;

    private final int playerX;

    private final int playerY;

    private final int playerId;

    private int sheetCount = -1;

    public InitInfo(int mazeLength, int mazeHeight, int mazeLevel, int playerX, int playerY, int playerId) {
        this.mazeLength = mazeLength;
        this.mazeHeight = mazeHeight;
        this.mazeLevel = mazeLevel;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerId = playerId;
        Output.logDebug("Init infos: length=" + mazeLength + " height=" + mazeHeight);
    }

    public InitInfo(int mazeLength, int mazeHeight, int mazeLevel, int playerX, int playerY, int playerId, int sheetCount) {
        this(mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId);
        this.sheetCount = sheetCount;
    }

    private Player generatePlayer() {
        return sheetCount > -1 ? new Player(playerX, playerY, playerId, sheetCount) : new Player(playerX, playerY, playerId);
    }

    private Maze generateMaze() {
        return new Maze(generatePlayer(), mazeLength, mazeHeight);
    }

    public KI generateKI() {
        Output.logDebug(this.toString());
        switch (mazeLevel) {
            case 1:
                return new LevelOneKI(generateMaze());
            case 2:
                String mazeString=null;
                switch (mazeLength) {
                    case 10: case 11:return new LevelTwoExtraKI(mazeLength,generatePlayer());
                    case 13:mazeString=Mazes.MAZE6;break;
                    case 20:mazeString=Mazes.MAZE7;break;
                    case 25:mazeString=Mazes.MAZE8;break;
                }
                if(mazeString!=null) {
                    Maze maze=new Maze(mazeString,generatePlayer());
                    return new LevelTwoExtraKI(maze);
                }
                return new LevelTwoKI(generateMaze());
            case 3:
                return new LevelThreeKI(generateMaze());
            case 4:
                return new LevelFourKI(generateMaze());
            case 5:
                return new LevelFiveKI((generateMaze()));
            default:
                Output.logDebug("Running on unsupported Level!\n Using basic KI for Level 1!\n This is highly unstable and not recommended!\n Self Destruction imminent!");
                return new LevelOneKI(generateMaze());
        }
    }

    @Override
    public String toString() {
        return String.format(
                "InitInfo [mazeLength=%s, mazeHeight=%s, mazeLevel=%s, playerX=%s, playerY=%s, playerId=%s, sheetCount=%s]",
                mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId, sheetCount);
    }


}
