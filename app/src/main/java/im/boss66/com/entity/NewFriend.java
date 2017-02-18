package im.boss66.com.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnny on 2017/2/17.
 */
public class NewFriend implements Parcelable {
    private String id = "";
    private String user_id = "";
    private String user_name = "";
    private String avatar = "";
    private String feedback_mark = "";//0:已拒绝，1：处理中（接受）2:已添加
    private String feedback_time = "";
    private String feedback_status = "";
    private String add_time = "";
    private String friend_note = "";

    public NewFriend() {
    }

    protected NewFriend(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        user_name = in.readString();
        avatar = in.readString();
        feedback_mark = in.readString();
        feedback_time = in.readString();
        feedback_status = in.readString();
        add_time = in.readString();
        friend_note = in.readString();
    }

    public static final Creator<NewFriend> CREATOR = new Creator<NewFriend>() {
        @Override
        public NewFriend createFromParcel(Parcel in) {
            return new NewFriend(in);
        }

        @Override
        public NewFriend[] newArray(int size) {
            return new NewFriend[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFeedback_mark() {
        return feedback_mark;
    }

    public void setFeedback_mark(String feedback_mark) {
        this.feedback_mark = feedback_mark;
    }

    public String getFeedback_time() {
        return feedback_time;
    }

    public void setFeedback_time(String feedback_time) {
        this.feedback_time = feedback_time;
    }

    public String getFeedback_status() {
        return feedback_status;
    }

    public void setFeedback_status(String feedback_status) {
        this.feedback_status = feedback_status;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getFriend_note() {
        return friend_note;
    }

    public void setFriend_note(String friend_note) {
        this.friend_note = friend_note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(user_id);
        parcel.writeString(user_name);
        parcel.writeString(avatar);
        parcel.writeString(feedback_mark);
        parcel.writeString(feedback_time);
        parcel.writeString(feedback_status);
        parcel.writeString(add_time);
        parcel.writeString(friend_note);
    }
}
