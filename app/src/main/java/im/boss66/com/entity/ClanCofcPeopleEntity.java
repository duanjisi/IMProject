package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/7.
 */

public class ClanCofcPeopleEntity {


    /**
     * name : GETCELEBRITY
     * version : v1
     * message : success
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\CofcController
     * result : [{"id":27,"cofc_id":6,"name":"张三","photo":"https://imgcdn.66boss.com/cofc/celebrity_photo/2017/04/12/58eded6d6bbc0.jpg","desc":"张三是个牛逼的人","user_id":100000000,"add_time":1491987821}]
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
         * id : 27
         * cofc_id : 6
         * name : 张三
         * photo : https://imgcdn.66boss.com/cofc/celebrity_photo/2017/04/12/58eded6d6bbc0.jpg
         * desc : 张三是个牛逼的人
         * user_id : 100000000
         * add_time : 1491987821
         */

        private int clan_id;

        public int getClan_id() {
            return clan_id;
        }

        public void setClan_id(int clan_id) {
            this.clan_id = clan_id;
        }

        private int id;
        private int cofc_id;
        private String name;
        private String photo;
        private String desc;
        private int user_id;
        private int add_time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCofc_id() {
            return cofc_id;
        }

        public void setCofc_id(int cofc_id) {
            this.cofc_id = cofc_id;
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

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getAdd_time() {
            return add_time;
        }

        public void setAdd_time(int add_time) {
            this.add_time = add_time;
        }
    }
}
