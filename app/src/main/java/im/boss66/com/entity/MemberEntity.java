package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/3/1.
 * 群成员
 */
public class MemberEntity {
    private String nickname = "";
    private String snap = "";
    private String userid = "";
    private int type = 0;//0;普通, 1;添加按钮, 2,删除按钮
    protected boolean checked = false;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
