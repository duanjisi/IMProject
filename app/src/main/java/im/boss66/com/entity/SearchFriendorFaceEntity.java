package im.boss66.com.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GMARUnity on 2017/3/2.
 */
public class SearchFriendorFaceEntity {

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

    public SearchDataEntity getResult() {
        return result;
    }

    public void setResult(SearchDataEntity result) {
        this.result = result;
    }

    private SearchDataEntity result;
}
