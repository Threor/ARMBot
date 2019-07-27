package de.arm.bot.info;

import de.arm.bot.io.Output;
import de.arm.bot.ki.*;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Mazes;
import de.arm.bot.model.Player;

/**
 * A wrapper class that holds all information given on the start of the game.
 * These information will be used to generate the maze, the player and to initialize the KI.
 *
 * @author Team ARM
 */
public class InitInfo {

    /**
     * The length of the maze
     */
    private final int mazeLength;

    /**
     * The height of the maze
     */
    private final int mazeHeight;

    /**
     * The level of the maze
     * Should be between 1 and 5
     */
    private final int mazeLevel;

    /**
     * The starting X-coordinate of the player
     */
    private final int playerX;

    /**
     * The starting Y-coordinate of the player
     */
    private final int playerY;

    /**
     * The given id of the player
     */
    private final int playerId;

    /**
     * The starting sheet count of the player.
     * Only used in level 5, therefore it is set to -1 by default
     */
    private int sheetCount = -1;

    /**
     * Constructor used by level 4 and below.
     * Initializes all fields
     *
     * @param mazeLength The length of the maze
     * @param mazeHeight The height of the maze
     * @param mazeLevel  The level of the maze
     * @param playerX    The starting X-coordinate of the player
     * @param playerY    The starting Y-coordinate of the player
     * @param playerId   The given id of the player
     */
    public InitInfo(int mazeLength, int mazeHeight, int mazeLevel, int playerX, int playerY, int playerId) {
        this.mazeLength = mazeLength;
        this.mazeHeight = mazeHeight;
        this.mazeLevel = mazeLevel;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerId = playerId;
    }

    /**
     * Constructor used by level 5
     * Initializes all fields
     *
     * @param mazeLength The length of the maze
     * @param mazeHeight The height of the maze
     * @param mazeLevel  The level of the maze
     * @param playerX    The starting X-coordinate of the player
     * @param playerY    The starting Y-coordinate of the player
     * @param playerId   The given id of the player
     * @param sheetCount The count of sheets the player holds at the beginning
     */
    public InitInfo(int mazeLength, int mazeHeight, int mazeLevel, int playerX, int playerY, int playerId, int sheetCount) {
        this(mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId);
        this.sheetCount = sheetCount;
    }

    /**
     * Generates a new Player object based on the given player infos
     *
     * @return the generated Player
     */
    private Player generatePlayer() {
        return sheetCount > -1 ? new Player(playerX, playerY, playerId, sheetCount) : new Player(playerX, playerY, playerId);
    }

    /**
     * Generates a new Maze object based on the given Maze infos and a generated Player
     *
     * @return The generated Maze
     */
    private Maze generateMaze() {
        return new Maze(generatePlayer(), mazeLength, mazeHeight);
    }

    /**
     * Generates a new KI based on the maze Level and the generated Maze and Player.
     * Only supports level 1-5. For unsupported level a LevelOneKI will be generated
     * For level two the LevelTwoExtraKI will be loaded for known mazes. This is accomplished by analyzing the infos given about the maze.
     * If the maze is unknown, then a normal LevelTwoKI will be generated
     *
     * @return The generated KI
     */
    public KI generateKI() {
        switch (mazeLevel) {
            case 1:
                return new LevelOneKI(generateMaze());
            case 2:
                String mazeString = null;
                switch (mazeLength) {
                    //Mazes on level 2 with a length or 10 or 11 are not unique. Therefore the KI will decide on turn one which one it has to load
                    case 10:
                    case 11:
                        return new LevelTwoExtraKI(mazeLength, generatePlayer());
                    case 13:
                        mazeString = Mazes.MAZE6;
                        break;
                    case 20:
                        mazeString = Mazes.MAZE7;
                        break;
                    case 25:
                        mazeString = Mazes.MAZE8;
                        break;
                }
                //Not running on a known maze
                if (mazeString != null) {
                    Maze maze = new Maze(mazeString, generatePlayer());
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
