package models;

/** One edit action performed on the document (used by Algorithm B to Undo/Redo). */
public class Action {

    public enum ActionType { INSERT, DELETE, REPLACE }

    private static int counter = 0;

    private final int actionId = ++counter;
    private final ActionType actionType;
    private final int position;
    private final String oldText; // text removed/replaced (empty for INSERT)
    private final String newText; // text added (empty for DELETE)
    private final long timestamp = System.currentTimeMillis();

    public Action(ActionType actionType, int position, String oldText, String newText) {
        this.actionType = actionType;
        this.position = position;
        this.oldText = oldText == null ? "" : oldText;
        this.newText = newText == null ? "" : newText;
    }

    public int getActionId()          { return actionId; }
    public ActionType getActionType() { return actionType; }
    public int getPosition()          { return position; }
    public String getOldText()        { return oldText; }
    public String getNewText()        { return newText; }
    public long getTimestamp()        { return timestamp; }

    @Override
    public String toString() {
        return actionType + "(pos=" + position + ", old=\"" + oldText + "\", new=\"" + newText + "\")";
    }
}