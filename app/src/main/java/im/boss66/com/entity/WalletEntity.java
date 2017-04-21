package im.boss66.com.entity;

/**
 * Created by GMARUnity on 2017/4/20.
 */
public class WalletEntity {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    private String message;
    private int code;
    private float data;
}
