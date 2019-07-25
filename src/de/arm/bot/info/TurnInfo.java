package de.arm.bot.info;

import de.arm.bot.io.Output;
import de.arm.bot.model.PrimitiveStatus;
import de.arm.bot.model.Status;

import java.util.Map;

public class TurnInfo {

    private final ActionResult lastActionResult;

    private final Map<Direction, Status> cellStatus;

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

    public boolean hasCell(Status primitiveStatus) {
        return cellStatus.containsValue(primitiveStatus);
    }

    public boolean hasCell(PrimitiveStatus status) {
        return cellStatus.containsValue(new Status(status));
    }

    @Override
    public String toString() {
        return String.format("TurnInfo [lastActionResult=%s, cellStatus=%s]", lastActionResult, cellStatus);
    }

}
