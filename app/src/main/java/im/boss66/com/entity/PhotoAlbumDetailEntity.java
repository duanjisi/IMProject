package im.boss66.com.entity;

import java.util.List;

/**
 * 个人相册详情
 */
public class PhotoAlbumDetailEntity {

    private String name;
    private String version;

    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    private int code;
    private int status;
    private String type;

    public FriendCircle getResult() {
        return result;
    }

    public void setResult(FriendCircle result) {
        this.result = result;
    }

    private FriendCircle result;

}
