package im.boss66.com.entity;

/**
 * Created by liw on 2017/3/10.
 */

public class UserInfoEntity {

    /**
     * name : INDEX
     * version : v1
     * message : succ
     * code : 1
     * status : 200
     * result : {"user_id":"100000052","user_name":"哈哈","avatar":"https://imgcdn.66boss.com/imagesu/avatar_temp/default.jpg","mobile_phone":"15323339887","sex":"男","signature":"","birthday":825004800,"cover_pic":"https://imgcdn.66boss.com/imagesu/cofriend_cover/surface.png","province":6,"city":76,"district":693,"district_str":"广东 广州","ht_province":6,"ht_city":76,"ht_district":693,"ht_district_str":"广东 广州","industry":"其他","interest":"","school":"北京信息科技大学"}
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
         * user_id : 100000052
         * user_name : 哈哈
         * avatar : https://imgcdn.66boss.com/imagesu/avatar_temp/default.jpg
         * mobile_phone : 15323339887
         * sex : 男
         * signature :
         * birthday : 825004800
         * cover_pic : https://imgcdn.66boss.com/imagesu/cofriend_cover/surface.png
         * province : 6
         * city : 76
         * district : 693
         * district_str : 广东 广州
         * ht_province : 6
         * ht_city : 76
         * ht_district : 693
         * ht_district_str : 广东 广州
         * industry : 其他
         * interest :
         * school : 北京信息科技大学
         */

        private String user_id;
        private String user_name;
        private String avatar;
        private String mobile_phone;
        private String sex;
        private String signature;
        private int birthday;
        private String cover_pic;
        private int province;
        private int city;
        private int district;
        private String district_str;
        private int ht_province;
        private int ht_city;
        private int ht_district;
        private String ht_district_str;
        private String industry;
        private String interest;
        private String school;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getMobile_phone() {
            return mobile_phone;
        }

        public void setMobile_phone(String mobile_phone) {
            this.mobile_phone = mobile_phone;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public int getBirthday() {
            return birthday;
        }

        public void setBirthday(int birthday) {
            this.birthday = birthday;
        }

        public String getCover_pic() {
            return cover_pic;
        }

        public void setCover_pic(String cover_pic) {
            this.cover_pic = cover_pic;
        }

        public int getProvince() {
            return province;
        }

        public void setProvince(int province) {
            this.province = province;
        }

        public int getCity() {
            return city;
        }

        public void setCity(int city) {
            this.city = city;
        }

        public int getDistrict() {
            return district;
        }

        public void setDistrict(int district) {
            this.district = district;
        }

        public String getDistrict_str() {
            return district_str;
        }

        public void setDistrict_str(String district_str) {
            this.district_str = district_str;
        }

        public int getHt_province() {
            return ht_province;
        }

        public void setHt_province(int ht_province) {
            this.ht_province = ht_province;
        }

        public int getHt_city() {
            return ht_city;
        }

        public void setHt_city(int ht_city) {
            this.ht_city = ht_city;
        }

        public int getHt_district() {
            return ht_district;
        }

        public void setHt_district(int ht_district) {
            this.ht_district = ht_district;
        }

        public String getHt_district_str() {
            return ht_district_str;
        }

        public void setHt_district_str(String ht_district_str) {
            this.ht_district_str = ht_district_str;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }
    }
}
