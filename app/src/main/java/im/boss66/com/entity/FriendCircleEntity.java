package im.boss66.com.entity;

import java.util.List;

/**
 * 朋友圈实体类
 */
public class FriendCircleEntity {

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

    public int getStatus() {
        return status;
    }

    private int status;
    private String type;

    public List<FriendCircle> getResult() {
        return result;
    }

    public void setResult(List<FriendCircle> result) {
        this.result = result;
    }

    private List<FriendCircle> result;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }
}
