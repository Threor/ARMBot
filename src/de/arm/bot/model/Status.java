package de.arm.bot.model;

import java.util.Objects;

import static de.arm.bot.model.PrimitiveStatus.*;

/**
 * A wrapper class used for combinig a PrimitiveStatus with additional info (e.g. formId)
 *
 * @see de.arm.bot.model.PrimitiveStatus
 */
public class Status {

    /**
     * The status
     */
    private PrimitiveStatus status;

    /**
     * Optional additional info
     */
    private Integer additionalInfo;

    /** Default constructor initializes all fields
     * @param status The status
     * @param additionalInfo The additional Info
     */
    public Status(PrimitiveStatus status, Integer additionalInfo) {
        this.status = status;
        this.additionalInfo = additionalInfo;
    }

    /** Constructor for this class without additional info
     * @param status The status
     */
    public Status(PrimitiveStatus status) {
        this.status = status;
    }

    /** Getter for the attribute status
     * @return The status
     */
    public PrimitiveStatus getStatus() {
        return status;
    }

    /** Setter for the attribute status
     * @param status The status to be set
     */
    public void setStatus(PrimitiveStatus status) {
        this.status = status;
    }

    /** Getter for the attribute additional info
     * @return The additional info to be set
     */
    public Integer getAdditionalInfo() {
        return additionalInfo == null ? -1 : additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof PrimitiveStatus) return status == o;
        if (o instanceof Status) return status == ((Status) o).getStatus();
        return false;
    }

    public boolean equals(PrimitiveStatus primitiveStatus) {
        return status == primitiveStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    /** Parses a given string of two chars to a status.
     * Used for known mazes in level 2
     * @param string The string representation of the status
     * @param playerId The playerId used for identifying form and finish cells
     * @return The parsed status
     */
    public static Status ofString(String string, int playerId) {
        if (string.equals("##")) return new Status(WALL);
        if (string.equals("  ") || string.startsWith("@")) return new Status(FLOOR);
        if (string.startsWith("!")) {
            if (playerId == Integer.valueOf(string.substring(1))) return new Status(FINISH);
            return new Status(FLOOR);
        }
        int formId = string.charAt(0) - 'A' + '\001';
        int formPlayerId = Integer.valueOf(string.substring(1));
        if (formPlayerId == playerId) {
            return new Status(FORM, formId);
        } else {
            return new Status(ENEMY_FORM, formId);
        }
    }

    /** Getter for the attribute cost of the status
     * @return The cost
     */
    public int getCost() {
        return status.getCost();
    }

    /** Getter for the attribute navigable of the status
     * @return True, if this status is navigable
     */
    public boolean isNavigable() {
        return status.isNavigable();
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
