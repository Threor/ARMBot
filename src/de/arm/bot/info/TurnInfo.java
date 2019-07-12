package de.arm.bot.info;

import java.util.Map;

import de.arm.bot.model.Status;

public class TurnInfo {

	private ActionResult lastActionResult;
	
	private Map<Direction,Status> cellStatus;
	
	public TurnInfo(ActionResult lastActionResult, Map<Direction,Status> cellStatus) {
		this.lastActionResult=lastActionResult;
		this.cellStatus=cellStatus;
	}
	
	public Map<Direction, Status> getCellStatus() {
		return cellStatus;
	}
	
	public ActionResult getLastActionResult() {
		return lastActionResult;
	}
	
	public boolean hasCell(Status status) {
		return cellStatus.containsValue(status);
	}

	@Override
	public String toString() {
		return String.format("TurnInfo [lastActionResult=%s, cellStatus=%s]", lastActionResult, cellStatus);
	}
	
}
