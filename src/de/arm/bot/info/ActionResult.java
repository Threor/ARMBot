package de.arm.bot.info;

/**
 * A class representing the result the game will send after an action was performed.
 * This is used to interpret the result of the last action and to act accordingly
 */
public class ActionResult {

    /**
     * Indicates whether the last action was successful or not.
     * In the protocol the value OK indicates an successful action and the value NOK indicates an unsuccessful action
     */
    private final boolean ok;

    /**
     * The message that was given by the game additionally to the result.
     * The message is used for handling failed action that could occur because of talking etc.
     */
    private final String message;

    /** The constructor of this class used internally to initialize all fields
     * @param ok
     * @param message
     */
    private ActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    /** Creates a new ActionResult based on the resultLine that was given by the game
     * @param resultLine The line of the information given by the game that represent the result
     * @return The created ActionResult
     */
    public static ActionResult of(String resultLine) {
        String[] args = resultLine.split(" ");
        if (args.length > 2) return null;
        String message = args.length < 2 ? "" : args[1];
        return new ActionResult(args[0].equals("OK"), message);
    }

    /** Getter for the attribute message
     * @return The message of this action
     */
    public String getMessage() {
        return message;
    }

    /** Getter for the attribute ok
     * @return The boolean ok
     */
    public boolean isOk() {
        return ok;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "ok=" + ok +
                ", message='" + message + '\'' +
                '}';
    }
}
