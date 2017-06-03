package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/5/31.
 */

public class VideoCategory {


    /**
     * message : Ok
     * code : 0
     * data : [{"classid":"1","name":"美食"},{"classid":"2","name":"女装"},{"classid":"3","name":"男装"},{"classid":"4","name":"鞋帽"},{"classid":"5","name":"玩乐"}]
     */

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
         * classid : 1
         * name : 美食
         */

        private String classid;
        private String name;

        public String getClassid() {
            return classid;
        }

        public void setClassid(String classid) {
            this.classid = classid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
