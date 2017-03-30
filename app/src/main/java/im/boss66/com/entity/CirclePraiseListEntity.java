package im.boss66.com.entity;

import java.util.List;

/**
 * 点赞列表
 */
public class CirclePraiseListEntity {

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

    public int getStatus() {
        return status;
    }

    private int status;
    private String type;

    public List<FriendCirclePraiseEntity> getResult() {
        return result;
    }

    public void setResult(List<FriendCirclePraiseEntity> result) {
        this.result = result;
    }

    private List<FriendCirclePraiseEntity> result;

}
