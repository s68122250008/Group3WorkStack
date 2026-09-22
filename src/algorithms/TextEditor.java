package algorithms;

/** Common contract for both Undo/Redo algorithms (Snapshot and Command/Delta). */
public interface TextEditor {

    void insert(int position, String text);
    void delete(int position, int length);
    void replace(int position, int length, String newText);

    boolean undo(); // false if there is nothing to undo
    boolean redo(); // false if there is nothing to redo

    String getText();
    int undoStackSize();
    int redoStackSize();
    void displayState();

    long getPushCount();
    long getPopCount();
    long getComparisonCount();
    long getAuxiliaryCharCount(); // approx. memory used by the undo/redo history

    void resetCounters();
}