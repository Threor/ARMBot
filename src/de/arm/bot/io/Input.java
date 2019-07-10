package de.arm.bot.io;

import java.util.Scanner;

import de.arm.bot.info.ActionResult;
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
		Output.logDebug(i.toString());
		return i;
	}

	public TurnInfo readTurnInfo() {
		ActionResult lastResult=ActionResult.of(scanner.nextLine());
		Status currentCellStatus=parse(scanner.nextLine());
		Status northernCellStatus=parse(scanner.nextLine());
		Status easternCellStatus=parse(scanner.nextLine());
		Status southernCellStatus=parse(scanner.nextLine());
		Status westernCellStatus=parse(scanner.nextLine());
		TurnInfo t= new TurnInfo(lastResult,currentCellStatus,northernCellStatus,westernCellStatus,southernCellStatus,easternCellStatus);
		Output.logDebug(t.toString());
		return t;
	}
	
	private Status parse(String line) {
		if(line.split(" ").length>2)return checkForFinish(line);
		return Status.valueOf(line);
	}
	
	private Status checkForFinish(String line) {
		int id=Integer.valueOf(line.split(" ")[1]);
		if(id==playerId) return Status.valueOf(line.split(" ")[0]);
		return Status.FLOOR;
	}
	
	public boolean hasData() {
		return scanner.hasNext();
	}
}

