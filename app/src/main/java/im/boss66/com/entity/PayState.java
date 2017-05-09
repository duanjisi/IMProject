package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/5/9.
 */
public class PayState {
    private String state = "";
    private String type = "";

    public PayState(String type, String state) {
        this.type = type;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
