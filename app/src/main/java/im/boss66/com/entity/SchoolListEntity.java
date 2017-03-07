package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/6.
 */

public class SchoolListEntity {

    /**
     * name : Userschool for Api v1
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : yii\Response
     * result : [{"us_id":1,"user_id":100000000,"school_id":6,"school_name":"广东轻工职业技术学院","level":5,"departments":"计算机多媒体技术","note":"jmt112","edu_year":"2011","add_time":1488189015}]
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

    @Override
    public String toString() {
        return "SchoolListEntity{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", status=" + status +
                ", type='" + type + '\'' +
                ", result=" + result +
                '}';
    }

    public static class ResultBean {
        /**
         * us_id : 1
         * user_id : 100000000
         * school_id : 6
         * school_name : 广东轻工职业技术学院
         * level : 5
         * departments : 计算机多媒体技术
         * note : jmt112
         * edu_year : 2011
         * add_time : 1488189015
         */

        private int us_id;
        private int user_id;
        private int school_id;
        private String school_name;
        private int level;
        private String departments;
        private String note;
        private String edu_year;
        private int add_time;

        public int getUs_id() {
            return us_id;
        }

        public void setUs_id(int us_id) {
            this.us_id = us_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getSchool_id() {
            return school_id;
        }

        public void setSchool_id(int school_id) {
            this.school_id = school_id;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getDepartments() {
            return departments;
        }

        public void setDepartments(String departments) {
            this.departments = departments;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getEdu_year() {
            return edu_year;
        }

        public void setEdu_year(String edu_year) {
            this.edu_year = edu_year;
        }

        public int getAdd_time() {
            return add_time;
        }

        public void setAdd_time(int add_time) {
            this.add_time = add_time;
        }
    }
}
