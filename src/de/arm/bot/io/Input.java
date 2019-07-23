package de.arm.bot.io;

import de.arm.bot.info.ActionResult;
import de.arm.bot.info.Direction;
import de.arm.bot.info.InitInfo;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static de.arm.bot.info.Direction.*;

/**
 * A class used for reading input data
 *
 * @author Team ARM
 */
public class Input {

    /**
     * A Scanner pointing towards System.in
     */
    private Scanner scanner;

    /**
     * The playerId as given by the game, used for identifying form and finish cells
     */
    private int playerId;

    /**
     * Defaultly used constructor for this class, initializes the scanner
     */
    public Input() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Read the initializing Infos gotten from the game. Used for starting the game
     *
     * @return The initializing infos given from the game
     */
    public InitInfo readInitInfo() {
        int mazeLength = scanner.nextInt();
        int mazeHeight = scanner.nextInt();
        int mazeLevel = scanner.nextInt();
        scanner.nextLine();
        int playerId = scanner.nextInt();
        this.playerId = playerId;
        int playerX = scanner.nextInt();
        int playerY = scanner.nextInt();
        if(mazeLevel==5){
            int sheetCount=scanner.nextInt();
            scanner.nextLine();
            return new InitInfo(mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId, sheetCount);
        }
        scanner.nextLine();
        return new InitInfo(mazeLength, mazeHeight, mazeLevel, playerX, playerY, playerId);
    }

    /**
     * Read the turn Infos gotten from the game. Used for calculating
     *
     * @return The turn infos given from the game
     */
    public TurnInfo readTurnInfo() {
        Output.setStart();
        String s=scanner.nextLine();
        Output.logDebug(s);
        ActionResult lastResult = ActionResult.of(s);
        Map<Direction, Status> cellStatus = new HashMap<>();
        s=scanner.nextLine();
        Output.logDebug(s);
        cellStatus.put(null, parse(s));
        s=scanner.nextLine();
        Output.logDebug(s);
        cellStatus.put(NORTH, parse(s));
        s=scanner.nextLine();
        Output.logDebug(s);
        cellStatus.put(EAST, parse(s));
        s=scanner.nextLine();
        Output.logDebug(s);
        cellStatus.put(SOUTH, parse(s));
        s=scanner.nextLine();
        Output.logDebug(s);
        cellStatus.put(WEST, parse(s));
        return new TurnInfo(lastResult, cellStatus);
    }

    /**
     * Parses a given line to a status, used for parsing the input values given by the game. May be enriched with information calculated in checkForFinish
     *
     * @param line The string to parse
     * @return The parsed status
     */
    private Status parse(String line) {
        if (line.split("\\s").length > 2) return checkForFinish(line);
        return Status.valueOf(line.split("\\s")[0]);
    }

    /**
     * Used for parsing status with multiple arguments like formId. Used by the parse function
     *
     * @param line The string to parse
     * @return The parsed status
     */
    private Status checkForFinish(String line) {
        Output.logDebug(Arrays.toString(line.split("\\s")));
        String[] args = line.split("\\s");
        int id = Integer.valueOf(args[1]);
        if (id == playerId) {
            Status ret = Status.valueOf(args[0]);
            ret.setAdditionalInfo(Integer.valueOf(args[2]));
            return ret;
        }
        return Status.FLOOR;
    }
}

