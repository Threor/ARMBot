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

public class Input {
	
	private Scanner scanner;
	
	private int playerId;
	
	public Input() {
		this.scanner=new Scanner(System.in);
	}
	
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
		InitInfo i= new InitInfo(mazeLength,mazeHeight,mazeLevel,playerX,playerY,playerId);
		return i;
	}

	public TurnInfo readTurnInfo() {
		Output.setStart();
		ActionResult lastResult=ActionResult.of(scanner.nextLine());
		Map<Direction,Status> cellStatus=new HashMap<>();
		cellStatus.put(null, parse(scanner.nextLine()));
		cellStatus.put(NORTH, parse(scanner.nextLine()));
		cellStatus.put(EAST, parse(scanner.nextLine()));
		cellStatus.put(SOUTH, parse(scanner.nextLine()));
		cellStatus.put(WEST, parse(scanner.nextLine()));
		TurnInfo t= new TurnInfo(lastResult,cellStatus);
		Output.logDebug("Read Turn Info");
		return t;
	}
	
	private Status parse(String line) {
		if(line.split(" ").length>1)return checkForFinish(line);
		return Status.valueOf(line);
	}
	
	private Status checkForFinish(String line) {
		String[] args=line.split(" ");
		int id=Integer.valueOf(args[1]);
		if(id==playerId) {
			Status ret=Status.valueOf(args[0]);
			ret.setAdditionalInfo(Integer.valueOf(args[2]));
			return ret;
		}
		return Status.FLOOR;
	}
	
	public boolean hasData() {
		return scanner.hasNext();
	}
}

