package org.example;

import java.time.LocalDate;

public class Task {
    private final String taskID;
    private final String title;
    private final LocalDate dueDate;
    private final int deadline; // epoch day of dueDate: the integer key used by the heap and BST
    private String assignedMemberID;

    public Task(String taskID, String title, LocalDate dueDate) {
        this.taskID = taskID;
        this.title = title;
        this.dueDate = dueDate;
        this.deadline = (int) dueDate.toEpochDay();
    }

    /** Kept for compatibility: the int is interpreted as an epoch day. */
    public Task(String taskID, String title, int deadline) {
        this(taskID, title, LocalDate.ofEpochDay(deadline));
    }

    public String getTaskID() { return taskID; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public int getDeadline() { return deadline; }
    public String getAssignedMemberID() { return assignedMemberID; }
    public void setAssignedMemberID(String id) { this.assignedMemberID = id; }
}
