package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/1.
 * 群信息实体
 */
public class GroupInform {
    private String groupid = "";
    private String creator = "";
    private String name = "";
    private String notice = "";
    private String snap = "";
    private ArrayList<MemberEntity> members = new ArrayList<>();

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

    public ArrayList<MemberEntity> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<MemberEntity> members) {
        this.members = members;
    }
}
