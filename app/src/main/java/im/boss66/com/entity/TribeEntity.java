package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/5/20.
 */

public class TribeEntity {

    /**
     * name : CREATEBY
     * version : v1
     * message : success
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\StoretribeController
     * result : [{"stribe_id":1,"user_id":100000000,"name":"港棉纺织1","desc":"agagahahahah","logo":"https://imgcdn.66boss.com/storetribe/logo/2017/05/18/591d61911c3e5.jpeg"}]
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
         * stribe_id : 1
         * user_id : 100000000
         * name : 港棉纺织1
         * desc : agagahahahah
         * logo : https://imgcdn.66boss.com/storetribe/logo/2017/05/18/591d61911c3e5.jpeg
         */

        private int stribe_id;
        private int user_id;
        private String name;
        private String desc;
        private String logo;

        public int getStribe_id() {
            return stribe_id;
        }

        public void setStribe_id(int stribe_id) {
            this.stribe_id = stribe_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}
