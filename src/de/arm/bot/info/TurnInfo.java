package de.arm.bot.info;

import de.arm.bot.model.Status;

public class TurnInfo {

	private ActionResult lastActionResult;
	
	private Status currentCellStatus;
	
	private Status northernCellStatus;
	
	private Status westernCellStatus;
	
	private Status southernCellStatus;
	
	private Status easternCellStatus;
	
	public TurnInfo(ActionResult lastActionResult, Status currentCellStatus, Status northernCellStatus,
			Status westernCellStatus, Status southernCellStatus, Status easternCellStatus) {
		this.lastActionResult = lastActionResult;
		this.currentCellStatus = currentCellStatus;
		this.northernCellStatus = northernCellStatus;
		this.westernCellStatus = westernCellStatus;
		this.southernCellStatus = southernCellStatus;
		this.easternCellStatus = easternCellStatus;
	}

	/**
	 * @return lastAction
	 */
	public ActionResult getLastActionResult() {
		return lastActionResult;
	}

	/**
	 * @return currentCellStatus
	 */
	public Status getCurrentCellStatus() {
		return currentCellStatus;
	}

	/**
	 * @return northernCellStatus
	 */
	public Status getNorthernCellStatus() {
		return northernCellStatus;
	}

	/**
	 * @return westernCellStatus
	 */
	public Status getWesternCellStatus() {
		return westernCellStatus;
	}

	/**
	 * @return southernCellStatus
	 */
	public Status getSouthernCellStatus() {
		return southernCellStatus;
	}

	/**
	 * @return easternCellStatus
	 */
	public Status getEasternCellStatus() {
		return easternCellStatus;
	}

	@Override
	public String toString() {
		return String.format(
				"TurnInfo [lastActionResult=%s, currentCellStatus=%s, northernCellStatus=%s, westernCellStatus=%s, southernCellStatus=%s, easternCellStatus=%s]",
				lastActionResult, currentCellStatus, northernCellStatus, westernCellStatus, southernCellStatus,
				easternCellStatus);
	}
	
	
	
}
