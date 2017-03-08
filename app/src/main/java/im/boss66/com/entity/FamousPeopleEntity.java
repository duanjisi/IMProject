package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/7.
 */

public class FamousPeopleEntity {

    /**
     * name : Hometown for Api v1
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : yii\Response
     * result : [{"id":16,"pid":13,"name":"伍秉鉴","photo":"https://imgcdn.66boss.com/contact/emo_HometownCelebrity/2017/03/03/2017-03-03131820661934.jpg","desc":"（1769\u20141843年），又名伍敦元，祖籍福建"}]
     */

    private String name;
    private String version;
    private String message;
    private int code;
    private int status;
    private String type;
    private List<ResultBean> result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 16
         * pid : 13
         * name : 伍秉鉴
         * photo : https://imgcdn.66boss.com/contact/emo_HometownCelebrity/2017/03/03/2017-03-03131820661934.jpg
         * desc : （1769—1843年），又名伍敦元，祖籍福建
         */

        private int id;
        private int pid;
        private String name;
        private String photo;
        private String desc;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
