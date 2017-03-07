package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/7.
 */

public class ClubEntity {

    /**
     * name : Hometown for Api v1
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : yii\Response
     * result : [{"id":3,"name":"广州总商会","logo":"https://imgcdn.66boss.com/contact/emo_HometownCofc/2017/03/03/2017-03-0313194185012.jpg","pid":13,"short_desc":""}]
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
         * id : 3
         * name : 广州总商会
         * logo : https://imgcdn.66boss.com/contact/emo_HometownCofc/2017/03/03/2017-03-0313194185012.jpg
         * pid : 13
         * short_desc :
         */

        private int id;
        private String name;
        private String logo;
        private int pid;
        private String short_desc;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getShort_desc() {
            return short_desc;
        }

        public void setShort_desc(String short_desc) {
            this.short_desc = short_desc;
        }
    }
}
