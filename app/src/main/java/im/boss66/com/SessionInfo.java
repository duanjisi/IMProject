package im.boss66.com;

/**
 * Created by Johnny on 2016/7/5.
 */
public class SessionInfo {
    private int Action;
    private Object data;
    private int tag;
    private boolean flag;

    public int getAction() {
        return Action;
    }

    public void setAction(int action) {
        Action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
