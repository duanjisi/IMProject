package im.boss66.com.entity;

import java.util.List;

/**
 * Created by admin on 2017/2/20.
 */
public class MyInfo {


    /**
     * status : 200
     * version : v1
     * name : Contacts for Api v1
     * message : succeed
     * result : {"school_list":[{"name":"江西工程职业学院-勿删","count":0,"school_id":17,"logo":"","brief_desc":"江西工程职业学院是经江西省人民政府批准设立、国家教育部备案、省教育厅主管、在江西广播电视大学新校区基础上建立的一所培养高级应用型专业人才的省属公办普通高等院校。","banner":"https://imgcdn.66boss.com/contact/emo_School/2017/02/28/2017-02-28034256184468.jpg"},{"name":"江西工程职业学院-勿删","count":0,"school_id":17,"logo":"","brief_desc":"江西工程职业学院是经江西省人民政府批准设立、国家教育部备案、省教育厅主管、在江西广播电视大学新校区基础上建立的一所培养高级应用型专业人才的省属公办普通高等院校。","banner":"https://imgcdn.66boss.com/contact/emo_School/2017/02/28/2017-02-28034256184468.jpg"}],"hometown_list":[{"name":"闽侯县","count":0,"hometown_id":15,"logo":"https://imgcdn.66boss.com/contact/emo_Hometown/2017/03/07/2017-03-07031249557390.jpg","brief_desc":"闽侯（hòu）县，是福建省福州市下辖的一个县，1913年由闽县和侯官县合并而成，地处福建省福州市西南侧，总面积2136平方公里，常住人口67万人，人口以汉族为主，有畲族、苗族、壮族等少数民族。","banner":"https://imgcdn.66boss.com/contact/emo_Hometown/2017/03/07/2017-03-07031249557390.jpg"}]}
     * type : yii\Response
     * code : 1
     */

    private int status;
    private String version;
    private String name;
    private String message;
    private ResultBean result;
    private String type;
    private int code;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        private List<SchoolListBean> school_list;
        private List<HometownListBean> hometown_list;

        public List<SchoolListBean> getSchool_list() {
            return school_list;
        }

        public void setSchool_list(List<SchoolListBean> school_list) {
            this.school_list = school_list;
        }

        public List<HometownListBean> getHometown_list() {
            return hometown_list;
        }

        public void setHometown_list(List<HometownListBean> hometown_list) {
            this.hometown_list = hometown_list;
        }

        public static class SchoolListBean {
            /**
             * name : 江西工程职业学院-勿删
             * count : 0
             * school_id : 17
             * logo :
             * brief_desc : 江西工程职业学院是经江西省人民政府批准设立、国家教育部备案、省教育厅主管、在江西广播电视大学新校区基础上建立的一所培养高级应用型专业人才的省属公办普通高等院校。
             * banner : https://imgcdn.66boss.com/contact/emo_School/2017/02/28/2017-02-28034256184468.jpg
             */

            private String name;
            private int count;
            private int school_id;
            private String logo;
            private String brief_desc;
            private String banner;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getSchool_id() {
                return school_id;
            }

            public void setSchool_id(int school_id) {
                this.school_id = school_id;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getBrief_desc() {
                return brief_desc;
            }

            public void setBrief_desc(String brief_desc) {
                this.brief_desc = brief_desc;
            }

            public String getBanner() {
                return banner;
            }

            public void setBanner(String banner) {
                this.banner = banner;
            }
        }

        public static class HometownListBean {
            /**
             * name : 闽侯县
             * count : 0
             * hometown_id : 15
             * logo : https://imgcdn.66boss.com/contact/emo_Hometown/2017/03/07/2017-03-07031249557390.jpg
             * brief_desc : 闽侯（hòu）县，是福建省福州市下辖的一个县，1913年由闽县和侯官县合并而成，地处福建省福州市西南侧，总面积2136平方公里，常住人口67万人，人口以汉族为主，有畲族、苗族、壮族等少数民族。
             * banner : https://imgcdn.66boss.com/contact/emo_Hometown/2017/03/07/2017-03-07031249557390.jpg
             */

            private String name;
            private int count;
            private int hometown_id;
            private String logo;
            private String brief_desc;
            private String banner;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getHometown_id() {
                return hometown_id;
            }

            public void setHometown_id(int hometown_id) {
                this.hometown_id = hometown_id;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getBrief_desc() {
                return brief_desc;
            }

            public void setBrief_desc(String brief_desc) {
                this.brief_desc = brief_desc;
            }

            public String getBanner() {
                return banner;
            }

            public void setBanner(String banner) {
                this.banner = banner;
            }
        }
    }
}
