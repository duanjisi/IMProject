package im.boss66.com.entity;

import java.util.List;

/**
 * Created by GMARUnity on 2017/6/2.
 */
public class FuwaClassEntity {
    private String message;
    private int code;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public class DataBean{
        public String name;
        public String classid;
    }
}
