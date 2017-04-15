package im.boss66.com.entity;

/**
 * Created by liw on 2017/4/14.
 */

public class ClanCofcEntity {

    /**
     * name : INDEX
     * version : v1
     * message : success
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\CofcController
     * result : {"id":"9","name":"无敌商会","desc":null,"logo":"","province":"6","city":"76","district":"693","address":"","banner":"","user_id":"100000052","add_time":"1492075245","count":"1","contact":"","is_follow":1}
     */

    private String name;
    private String version;
    private String message;
    private int code;
    private int status;
    private String type;
    private ResultBean result;

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 9
         * name : 无敌商会
         * desc : null
         * logo :
         * province : 6
         * city : 76
         * district : 693
         * address :
         * banner :
         * user_id : 100000052
         * add_time : 1492075245
         * count : 1
         * contact :
         * is_follow : 1
         */

        private String id;
        private String name;
        private Object desc;
        private String logo;
        private String province;
        private String city;
        private String district;
        private String address;
        private String banner;
        private String user_id;
        private String add_time;
        private String count;
        private String contact;
        private int is_follow;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getDesc() {
            return desc;
        }

        public void setDesc(Object desc) {
            this.desc = desc;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public int getIs_follow() {
            return is_follow;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }
    }
}
