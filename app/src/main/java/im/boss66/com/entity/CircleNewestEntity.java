package im.boss66.com.entity;

/**
 * Created by GMARUnity on 2017/3/6.
 */
public class CircleNewestEntity {

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

    public CircleNewest getResult() {
        return result;
    }

    public void setResult(CircleNewest result) {
        this.result = result;
    }

    private CircleNewest result;
}
