package im.boss66.com.entity;

import java.util.List;

/**
 * 朋友圈评论列表
 */
public class CircleCommentListEntity {

    private String name;
    private String version;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    private int status;
    private String type;

    public List<FriendCircleCommentEntity> getResult() {
        return result;
    }

    public void setResult(List<FriendCircleCommentEntity> result) {
        this.result = result;
    }

    private List<FriendCircleCommentEntity> result;

}
