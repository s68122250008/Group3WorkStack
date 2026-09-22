import algorithms.CommandEditor;
import algorithms.SnapshotEditor;
import algorithms.TextEditor;

import java.util.Scanner;

/**
 * Menu-driven demo of Group 3's Text Editor Undo/Redo system.
 * Lets the user pick an algorithm, edit text, Undo/Redo, and inspect stacks.
 */
public class Main {

    private static TextEditor editor = new CommandEditor();
    private static String algorithmName = "Algorithm B (Command/Delta)";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Group 3: Text Editor - Snapshot vs Command ===");
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> useAlgorithm("A");
                    case "2" -> useAlgorithm("B");
                    case "3" -> insert();
                    case "4" -> delete();
                    case "5" -> replace();
                    case "6" -> undo();
                    case "7" -> redo();
                    case "8" -> editor.displayState();
                    case "9" -> showCounters();
                    case "10" -> System.out.println("Current text: \"" + editor.getText() + "\"");
                    case "0" -> { System.out.println("Goodbye."); return; }
                    default -> System.out.println("Invalid choice. Please enter a number from the menu.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Active algorithm: " + algorithmName);
        System.out.println("Text: \"" + editor.getText() + "\"  (undo=" + editor.undoStackSize() +
                ", redo=" + editor.redoStackSize() + ")");
        System.out.println("1) Use Algorithm A - Snapshot      6) UNDO");
        System.out.println("2) Use Algorithm B - Command/Delta 7) REDO");
        System.out.println("3) INSERT                          8) Display Stack State");
        System.out.println("4) DELETE                          9) Show Operation Counters");
        System.out.println("5) REPLACE                         10) Show Current Text");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private static void useAlgorithm(String which) {
        String text = editor.getText();
        editor = which.equals("A") ? new SnapshotEditor() : new CommandEditor();
        algorithmName = which.equals("A") ? "Algorithm A (Snapshot)" : "Algorithm B (Command/Delta)";
        if (!text.isEmpty()) editor.insert(0, text); // carry the text over into a fresh history
        System.out.println("Switched to " + algorithmName);
    }

    private static void insert() {
        int pos = readInt("Position: ");
        System.out.print("Text to insert: ");
        String text = sc.nextLine();
        if (text.isEmpty()) {
            System.out.println("Warning: inserted text is empty, no change made.");
            return;
        }
        editor.insert(pos, text);
        System.out.println("Inserted. Text is now: \"" + editor.getText() + "\"");
    }

    private static void delete() {
        if (editor.getText().isEmpty()) {
            System.out.println("Warning: document is empty, nothing to delete.");
            return;
        }
        int pos = readInt("Position: ");
        int len = readInt("Length: ");
        editor.delete(pos, len);
        System.out.println("Deleted. Text is now: \"" + editor.getText() + "\"");
    }

    private static void replace() {
        if (editor.getText().isEmpty()) {
            System.out.println("Warning: document is empty, nothing to replace.");
            return;
        }
        int pos = readInt("Position: ");
        int len = readInt("Length to replace: ");
        System.out.print("New text: ");
        String newText = sc.nextLine();
        editor.replace(pos, len, newText);
        System.out.println("Replaced. Text is now: \"" + editor.getText() + "\"");
    }

    private static void undo() {
        System.out.println(editor.undo()
                ? "Undo done. Text is now: \"" + editor.getText() + "\""
                : "Warning: undo stack is empty, nothing to undo.");
    }

    private static void redo() {
        System.out.println(editor.redo()
                ? "Redo done. Text is now: \"" + editor.getText() + "\""
                : "Warning: redo stack is empty, nothing to redo.");
    }

    private static void showCounters() {
        System.out.println("Push count: " + editor.getPushCount());
        System.out.println("Pop count: " + editor.getPopCount());
        System.out.println("Comparison count: " + editor.getComparisonCount());
        System.out.println("Approx. auxiliary characters stored: " + editor.getAuxiliaryCharCount());
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number, try again: ");
            }
        }
    }
}