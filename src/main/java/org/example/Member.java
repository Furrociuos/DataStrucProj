public class Member {
    private final String memberID;
    private final String name;
    private final SimpleList<String> assignedTaskIDs = new SimpleList<>();

    public Member(String memberID, String name) {
        this.memberID = memberID;
        this.name = name;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getName() {
        return name;
    }

    public SimpleList<String> getAssignedTaskIDs() {
        return assignedTaskIDs;
    }
}
