import java.util.ArrayList;
import java.util.List;

public class Member {
    private final String memberID;
    private final String name;
    private final List<String> assignedTaskIDs = new ArrayList<>();

    public Member(String memberID, String name) {
        this.memberID = memberID;
        this.name = name;
    }

    public String getMemberID() { return memberID; }
    public String getName() { return name; }
    public List<String> getAssignedTaskIDs() { return assignedTaskIDs; }
}