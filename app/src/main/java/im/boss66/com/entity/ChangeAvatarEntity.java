package im.boss66.com.entity;

/**
 * 修改头像
 */
public class ChangeAvatarEntity {
    public String name;
    public String message;
    public int code;
    public int status;
    public String type;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result result;

    public class Result{
        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        String avatar;
    }

}
