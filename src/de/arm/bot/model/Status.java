package de.arm.bot.model;

import java.util.Objects;

import static de.arm.bot.model.PrimitiveStatus.*;

public class Status {

    private PrimitiveStatus status;

    private Integer additionalInfo;

    /**
     * constructor that initializes status and additionalInfo.
     * @param status
     * @param additionalInfo
     * 
     */
    public Status(PrimitiveStatus status, Integer additionalInfo) {
        this.status = status;
        this.additionalInfo = additionalInfo;
    }
    /**
     * constructor that initializes status
     * @param status
     */
    public Status(PrimitiveStatus status) {
        this.status=status;
    }
    
	/**
	 * getter for the attribute status
	 * @return status
	 */
    public PrimitiveStatus getStatus() {
        return status;
    }
    /**
     * setter for the attribute status
     * @param status
     */
    public void setStatus(PrimitiveStatus status) {
        this.status = status;
    }

    /** 
     * getter for the attribute additionalInfo
     * @returns additionalInfo if set 
     */
    public Integer getAdditionalInfo() {
        return additionalInfo==null?-1:additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof PrimitiveStatus)return status== o;
        if (o instanceof Status) return status==((Status) o).getStatus();
        return false;
    }

    public boolean equals(PrimitiveStatus primitiveStatus) {
        return status==primitiveStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    public static Status ofString(String string, int playerId) {
        if(string.equals("##"))return new Status(WALL);
        if(string.equals("  ")||string.startsWith("@"))return new Status(FLOOR);
        if(string.startsWith("!")){
            if(playerId==Integer.valueOf(string.substring(1)))return new Status(FINISH);
            return new Status(FLOOR);
        }
        int formId=string.charAt(0)-'A' + '\001';
        int formPlayerId=Integer.valueOf(string.substring(1));
        if(formPlayerId==playerId) {
            return new Status(FORM,formId);
        }else {
            return new Status(ENEMY_FORM,formId);
        }
    }

    public int getCost() {
        return status.getCost();
    }

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
