package im.boss66.com.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 摇一摇
 */
public class SharkIfOffEntity {

    public String name;
    public String message;
    public int code;
    public int status;
    public String type;


    public NearByChildEntity getResult() {
        return result;
    }

    public void setResult(NearByChildEntity result) {
        this.result = result;
    }

    private NearByChildEntity result;

}
