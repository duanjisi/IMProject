package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/5/22.
 */
public class Motion {
    private int action = 0;
    private Object data;
    private static Motion motion;

    public Motion() {
    }

    public static Motion getInstance() {
        if (motion == null) {
            motion = new Motion();
        }
        return motion;
    }

//    public static Motion init(int action, Object obj) {
//        if (motion == null) {
//            motion = new Motion();
//        }
//        setAction(action);
//        setData(obj);
//        return motion;
//    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
