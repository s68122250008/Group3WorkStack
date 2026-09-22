package algorithms;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Algorithm A: Snapshot Method.
 * Saves a full copy of the document before every edit, so Undo/Redo
 * simply swaps the current document with the top of the other stack.
 */
public class SnapshotEditor implements TextEditor {

    private StringBuilder document = new StringBuilder();
    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new ArrayDeque<>();
    private long pushCount, popCount, comparisonCount;

    private void checkBounds(int pos, int len, String message) {
        comparisonCount++;
        if (pos < 0 || len < 0 || pos + len > document.length()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void saveSnapshot() {
        undoStack.push(document.toString());
        pushCount++;
        redoStack.clear(); // a new edit always invalidates redo history
    }

    @Override
    public void insert(int position, String text) {
        checkBounds(position, 0, "Invalid insert position");
        saveSnapshot();
        document.insert(position, text);
    }

    @Override
    public void delete(int position, int length) {
        checkBounds(position, length, "Invalid delete range");
        saveSnapshot();
        document.delete(position, position + length);
    }

    @Override
    public void replace(int position, int length, String newText) {
        checkBounds(position, length, "Invalid replace range");
        saveSnapshot();
        document.replace(position, position + length, newText);
    }

    @Override
    public boolean undo() {
        comparisonCount++;
        if (undoStack.isEmpty()) return false;
        redoStack.push(document.toString());
        pushCount++;
        document = new StringBuilder(undoStack.pop());
        popCount++;
        return true;
    }

    @Override
    public boolean redo() {
        comparisonCount++;
        if (redoStack.isEmpty()) return false;
        undoStack.push(document.toString());
        pushCount++;
        document = new StringBuilder(redoStack.pop());
        popCount++;
        return true;
    }

    @Override public String getText()      { return document.toString(); }
    @Override public int undoStackSize()   { return undoStack.size(); }
    @Override public int redoStackSize()   { return redoStack.size(); }

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
        for (String s : undoStack) total += s.length();
        for (String s : redoStack) total += s.length();
        return total;
    }

    @Override
    public void resetCounters() {
        pushCount = popCount = comparisonCount = 0;
    }
}