public class Task {
    private final String taskID;
    private final String title;
    private final int deadline;
    private String assignedMemberID;

    public Task(String taskID, String title, int deadline) {
        this.taskID = taskID;
        this.title = title;
        this.deadline = deadline;
    }

    public String getTaskID() { return taskID; }
    public String getTitle() { return title; }
    public int getDeadline() { return deadline; }
    public String getAssignedMemberID() { return assignedMemberID; }
    public void setAssignedMemberID(String id) { this.assignedMemberID = id; }
}