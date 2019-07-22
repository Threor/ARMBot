package de.arm.bot.info;

public class ActionResult {

    private boolean ok;

    private String message;

    private ActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public static ActionResult of(String resultLine) {
        String[] args = resultLine.split(" ");
        //FIXME Illegal case (neither defined nor implemented)
        if (args.length > 2) return null;
        String message = args.length < 2 ? "" : args[1];
        return new ActionResult(args[0].equals("OK"), message);
    }

    public String getMessage() {
        return message;
    }

    public boolean isOk() {
        return ok;
    }
}
