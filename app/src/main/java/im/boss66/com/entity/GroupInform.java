package im.boss66.com.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/1.
 * 群信息实体
 */
public class GroupInform implements Parcelable {
    private String groupid = "";
    private String creator = "";
    private String name = "";
    private String notice = "";
    private String snap = "";
    private ArrayList<MemberEntity> members = new ArrayList<>();

    public GroupInform() {
    }

    protected GroupInform(Parcel in) {
        groupid = in.readString();
        creator = in.readString();
        name = in.readString();
        notice = in.readString();
        snap = in.readString();
    }

    public static final Creator<GroupInform> CREATOR = new Creator<GroupInform>() {
        @Override
        public GroupInform createFromParcel(Parcel in) {
            return new GroupInform(in);
        }

        @Override
        public GroupInform[] newArray(int size) {
            return new GroupInform[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupid);
        parcel.writeString(creator);
        parcel.writeString(name);
        parcel.writeString(notice);
        parcel.writeString(snap);
    }
}
