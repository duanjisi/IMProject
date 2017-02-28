package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/27.
 * 建群生成的实体
 */
public class GroupEntity {
    private String groupid = "";
    private String creator = "";
    private String name = "";
    private String notice = "";
    private String snap = "";
    private ArrayList<String> members = new ArrayList<>();

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
