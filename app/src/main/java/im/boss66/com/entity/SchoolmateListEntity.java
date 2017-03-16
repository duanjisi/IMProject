package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/13.
 */

public class SchoolmateListEntity {

    /**
     * name : SAME-SCHOOL
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\SearchController
     * result : [{"user_id":"100000037","user_name":"18202093754","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170228030528822344.jpg","sex":"0","birthday":"0","province":"28","city":"346","district":"2948","industry":"","interest":"","ht_province":"0","ht_city":"0","ht_district":"0","school":[{"name":"中山大学","user_id":"100000037","school_id":"58","level":"5","departments":null,"edu_year":"2017"},{"name":"中南林业科技大学-勿删","user_id":"100000037","school_id":"38","level":"5","departments":null,"edu_year":"2017"},{"name":"北京信息科技大学","user_id":"100000037","school_id":"62","level":"5","departments":null,"edu_year":"1993"}],"similar":52},{"user_id":"100000041","user_name":"Z18202093756","avatar":"https://imgcdn.66boss.com/imagesu/avatar_temp/default.jpg","sex":"1","birthday":"0","province":"2","city":"52","district":"500","industry":"互联网/电子商务","interest":"餐饮美食|体育运动","ht_province":"2","ht_city":"52","ht_district":"500","school":[{"name":"江西工程职业学院-勿删","user_id":"100000041","school_id":"17","level":"5","departments":null,"edu_year":"2012"},{"name":"中山大学","user_id":"100000041","school_id":"58","level":"5","departments":null,"edu_year":"0"}],"similar":19}]
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
         * user_id : 100000037
         * user_name : 18202093754
         * avatar : https://imgcdn.66boss.com/imagesu/avatar/20170228030528822344.jpg
         * sex : 0
         * birthday : 0
         * province : 28
         * city : 346
         * district : 2948
         * industry :
         * interest :
         * ht_province : 0
         * ht_city : 0
         * ht_district : 0
         * school : [{"name":"中山大学","user_id":"100000037","school_id":"58","level":"5","departments":null,"edu_year":"2017"},{"name":"中南林业科技大学-勿删","user_id":"100000037","school_id":"38","level":"5","departments":null,"edu_year":"2017"},{"name":"北京信息科技大学","user_id":"100000037","school_id":"62","level":"5","departments":null,"edu_year":"1993"}]
         * similar : 52
         */

        private String user_id;
        private String user_name;
        private String avatar;
        private String sex;
        private String birthday;
        private String province;
        private String city;
        private String district;
        private String industry;
        private String interest;
        private String ht_province;
        private String ht_city;
        private String ht_district;
        private int similar;
        private List<SchoolBean> school;

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

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
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

        public String getHt_province() {
            return ht_province;
        }

        public void setHt_province(String ht_province) {
            this.ht_province = ht_province;
        }

        public String getHt_city() {
            return ht_city;
        }

        public void setHt_city(String ht_city) {
            this.ht_city = ht_city;
        }

        public String getHt_district() {
            return ht_district;
        }

        public void setHt_district(String ht_district) {
            this.ht_district = ht_district;
        }

        public int getSimilar() {
            return similar;
        }

        public void setSimilar(int similar) {
            this.similar = similar;
        }

        public List<SchoolBean> getSchool() {
            return school;
        }

        public void setSchool(List<SchoolBean> school) {
            this.school = school;
        }

        public static class SchoolBean {
            /**
             * name : 中山大学
             * user_id : 100000037
             * school_id : 58
             * level : 5
             * departments : null
             * edu_year : 2017
             */

            private String name;
            private String user_id;
            private String school_id;
            private String level;
            private String departments;
            private String edu_year;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getSchool_id() {
                return school_id;
            }

            public void setSchool_id(String school_id) {
                this.school_id = school_id;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getDepartments() {
                return departments;
            }

            public void setDepartments(String departments) {
                this.departments = departments;
            }

            public String getEdu_year() {
                return edu_year;
            }

            public void setEdu_year(String edu_year) {
                this.edu_year = edu_year;
            }
        }
    }
}
