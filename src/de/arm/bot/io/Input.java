package de.arm.bot.io;

import static de.arm.bot.info.Direction.EAST;
import static de.arm.bot.info.Direction.NORTH;
import static de.arm.bot.info.Direction.SOUTH;
import static de.arm.bot.info.Direction.WEST;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.arm.bot.info.ActionResult;
import de.arm.bot.info.Direction;
import de.arm.bot.info.InitInfo;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Status;

/**
 * A class used for reading input data
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
		this.scanner=new Scanner(System.in);
	}

	/**
	 * Read the initializing Infos gotten from the game. Used for starting the game
	 * @return The initializing infos given from the game
	 */
	public InitInfo readInitInfo() {
		int mazeLength = scanner.nextInt();
		int mazeHeight = scanner.nextInt(); 
		int mazeLevel = scanner.nextInt();
		scanner.nextLine();
		int playerId = scanner.nextInt();
		this.playerId=playerId;
		int playerX = scanner.nextInt(); 
		int playerY = scanner.nextInt();
		scanner.nextLine();
		return new InitInfo(mazeLength,mazeHeight,mazeLevel,playerX,playerY,playerId);
	}

	/**
	 * Read the turn Infos gotten from the game. Used for calculating
	 * @return The turn infos given from the game
	 */
	public TurnInfo readTurnInfo() {
		Output.setStart();
		ActionResult lastResult=ActionResult.of(scanner.nextLine());
		Map<Direction,Status> cellStatus=new HashMap<>();
		cellStatus.put(null, parse(scanner.nextLine()));
		cellStatus.put(NORTH, parse(scanner.nextLine()));
		cellStatus.put(EAST, parse(scanner.nextLine()));
		cellStatus.put(SOUTH, parse(scanner.nextLine()));
		cellStatus.put(WEST, parse(scanner.nextLine()));
		return new TurnInfo(lastResult,cellStatus);
	}

	/** Parses a given line to a status, used for parsing the input values given by the game. May be enriched with information calculated in checkForFinish
	 * @param line The string to parse
	 * @return The parsed status
	 */
	private Status parse(String line) {
		if(line.split("\\W").length>1)return checkForFinish(line);
		return Status.valueOf(line);
	}

	/** Used for parsing status with multiple arguments like formId. Used by the parse function
	 * @param line The string to parse
	 * @return The parsed status
	 */
	private Status checkForFinish(String line) {
		String[] args=line.split("\\W");
		int id=Integer.valueOf(args[1]);
		if(id==playerId) {
			Status ret=Status.valueOf(args[0]);
			ret.setAdditionalInfo(Integer.valueOf(args[2]));
			return ret;
		}
		return Status.FLOOR;
	}
}

