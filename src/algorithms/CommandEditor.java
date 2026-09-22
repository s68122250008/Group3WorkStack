package algorithms;

import java.util.ArrayDeque;
import java.util.Deque;
import models.Action;
import models.Action.ActionType;

/**
 * Algorithm B: Command (Delta) Method.
 * Stores only what changed in each edit (an Action), and computes the
 * inverse operation to Undo — instead of keeping a full copy of the document.
 */
public class CommandEditor implements TextEditor {

    private StringBuilder document = new StringBuilder();
    private final Deque<Action> undoStack = new ArrayDeque<>();
    private final Deque<Action> redoStack = new ArrayDeque<>();
    private long pushCount, popCount, comparisonCount;

    private void checkBounds(int pos, int len, String message) {
        comparisonCount++;
        if (pos < 0 || len < 0 || pos + len > document.length()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void record(Action action) {
        undoStack.push(action);
        pushCount++;
        redoStack.clear(); // a new edit always invalidates redo history
    }

    @Override
    public void insert(int position, String text) {
        checkBounds(position, 0, "Invalid insert position");
        document.insert(position, text);
        record(new Action(ActionType.INSERT, position, "", text));
    }

    @Override
    public void delete(int position, int length) {
        checkBounds(position, length, "Invalid delete range");
        String removed = document.substring(position, position + length);
        document.delete(position, position + length);
        record(new Action(ActionType.DELETE, position, removed, ""));
    }

    @Override
    public void replace(int position, int length, String newText) {
        checkBounds(position, length, "Invalid replace range");
        String old = document.substring(position, position + length);
        document.replace(position, position + length, newText);
        record(new Action(ActionType.REPLACE, position, old, newText));
    }

    /** Undoes one action directly on the document. */
    private void applyInverse(Action a) {
        switch (a.getActionType()) {
            case INSERT  -> document.delete(a.getPosition(), a.getPosition() + a.getNewText().length());
            case DELETE  -> document.insert(a.getPosition(), a.getOldText());
            case REPLACE -> document.replace(a.getPosition(), a.getPosition() + a.getNewText().length(), a.getOldText());
        }
    }

    /** Re-applies one action directly on the document (used by Redo). */
    private void applyForward(Action a) {
        switch (a.getActionType()) {
            case INSERT  -> document.insert(a.getPosition(), a.getNewText());
            case DELETE  -> document.delete(a.getPosition(), a.getPosition() + a.getOldText().length());
            case REPLACE -> document.replace(a.getPosition(), a.getPosition() + a.getOldText().length(), a.getNewText());
        }
    }

    @Override
    public boolean undo() {
        comparisonCount++;
        if (undoStack.isEmpty()) return false;
        Action a = undoStack.pop();
        popCount++;
        applyInverse(a);
        redoStack.push(a);
        pushCount++;
        return true;
    }

    @Override
    public boolean redo() {
        comparisonCount++;
        if (redoStack.isEmpty()) return false;
        Action a = redoStack.pop();
        popCount++;
        applyForward(a);
        undoStack.push(a);
        pushCount++;
        return true;
    }

    @Override public String getText()    { return document.toString(); }
    @Override public int undoStackSize() { return undoStack.size(); }
    @Override public int redoStackSize() { return redoStack.size(); }

    @Override
    public void displayState() {
        System.out.println("Document: \"" + document + "\"");
        System.out.println("Undo stack size=" + undoStack.size());
        System.out.println("Redo stack size=" + redoStack.size());
    }

    @Override public long getPushCount()       { return pushCount; }
    @Override public long getPopCount()        { return popCount; }
    @Override public long getComparisonCount() { return comparisonCount; }

    @Override
    public long getAuxiliaryCharCount() {
        long total = 0;
        for (Action a : undoStack) total += a.getOldText().length() + a.getNewText().length();
        for (Action a : redoStack) total += a.getOldText().length() + a.getNewText().length();
        return total;
    }

    @Override
    public void resetCounters() {
        pushCount = popCount = comparisonCount = 0;
    }
}