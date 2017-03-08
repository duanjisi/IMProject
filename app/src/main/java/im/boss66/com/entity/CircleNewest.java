package im.boss66.com.entity;

/**
 * Created by GMARUnity on 2017/3/6.
 */
public class CircleNewest {

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public CircleNewestUser getFrist_user() {
        return frist_user;
    }

    public void setFrist_user(CircleNewestUser frist_user) {
        this.frist_user = frist_user;
    }

    String count;
    CircleNewestUser frist_user;

    public class CircleNewestUser {
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

        String user_id;
        String user_name;
        String avatar;
    }
}
