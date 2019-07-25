package de.arm.bot.info;

import de.arm.bot.io.Output;
import de.arm.bot.model.Status;

import java.util.Map;

public class TurnInfo {

    private ActionResult lastActionResult;

    private Map<Direction, Status> cellStatus;

    public TurnInfo(ActionResult lastActionResult, Map<Direction, Status> cellStatus) {
        this.lastActionResult = lastActionResult;
        this.cellStatus = cellStatus;
        Output.logDebug(cellStatus.toString());
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
