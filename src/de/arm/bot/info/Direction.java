package de.arm.bot.info;

/**
 * An enumeration containing each of the available directions, used for navigating to the maze and modelling neighbour cells
 *
 * @author Team ARM
 **/
public enum Direction {

    /**
     * The north
     */
    NORTH,
    /**
     * The east
     */
    EAST,
    /**
     * The south
     */
    SOUTH,
    /**
     * The west
     */
    WEST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * Calculates and returns the opposite direction, meaning the direction on the opposing side on the compass, to this direction
     *
     * @return The opposite Direction
     */
    public Direction getOpposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            default:
                return null;//should really not happen, but the compiler needs it
        }
    }
}
