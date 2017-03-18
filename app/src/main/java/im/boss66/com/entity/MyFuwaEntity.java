package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/16.
 */

public class MyFuwaEntity {

    /**
     * message : Ok
     * code : 0
     * data : [{"gid":"fuwa_1","id":"8"},{"gid":"fuwa_4","id":"5"}]
     */

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public static class DataBean {
        /**
         * gid : fuwa_1
         * id : 8
         */

        private String gid;
        private String id;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
