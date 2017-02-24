package im.boss66.com.entity;

/**
 * 相册封面
 */
public class AlbumCoverEntity {

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
            return cover_pic;
        }

        public void setAvatar(String avatar) {
            this.cover_pic = avatar;
        }

        public String cover_pic;
    }

}
