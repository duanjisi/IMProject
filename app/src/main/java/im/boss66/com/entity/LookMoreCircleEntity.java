package im.boss66.com.entity;

import java.util.List;

/**
 * Created by GMARUnity on 2017/3/6.
 */
public class LookMoreCircleEntity {

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
    private List<SearchCofriendEntity> result;

    public List<SearchCofriendEntity> getResult() {
        return result;
    }

    public void setResult(List<SearchCofriendEntity> result) {
        this.result = result;
    }
}
