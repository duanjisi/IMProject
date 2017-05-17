package im.boss66.com.entity;

import java.util.List;

/**
 * Created by admin on 2017/2/20.
 */
public class MyInfo {


    /**
     * name : INDEX
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\ContactsController
     * result : {"school_list":[{"school_id":18,"name":"清华大学","logo":"https://imgcdn.66boss.com/contact/emo_School/2017/03/22/2017-03-22163432506173.jpeg","banner":"https://imgcdn.66boss.com/contact/emo_School/2017/03/22/2017-03-22163432850532.jpeg","brief_desc":"清华大学是名牌大学","count":1},{"school_id":6,"name":"中山大学","logo":"","banner":"https://imgcdn.66boss.com/contact/emo_School/2017/03/07/2017-03-07100303433868.png","brief_desc":"fsadf","count":0}],"hometown_list":[{"hometown_id":36,"name":"广东 广州 番禺区","logo":"https://imgcdn.66boss.com/contact/emo_Hometown/2017/04/01/2017-04-01092419910950.png","banner":"https://imgcdn.66boss.com/contact/emo_Hometown/2017/04/01/2017-04-01092419910950.png","brief_desc":"无简介","count":1}],"cofc_list":[{"cofc_id":"6","name":"测试商会","logo":"https://imgcdn.66boss.com/cofc/logo/2017/04/12/58ede15e0117f.jpg","banner":"https://imgcdn.66boss.com/cofc/banner/2017/04/12/58ede15e0128c.jpg","brief_desc":"这是测试商会","count":"2"}],"clan_list":[{"clan_id":"6","name":"测试宗亲","logo":"https://imgcdn.66boss.com/clan/logo/2017/04/12/58edfaa90eb85.jpg","banner":"https://imgcdn.66boss.com/clan/banner/2017/04/12/58edfaa90ed5c.jpg","brief_desc":"这是测试宗亲","count":"1"}]}
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
        private List<SchoolListBean> school_list;
        private List<SchoolListBean> hometown_list;
        private List<SchoolListBean> cofc_list;
        private List<SchoolListBean> clan_list;
        private List<SchoolListBean> stribe_list;

        public List<SchoolListBean> getStribe_list() {
            return stribe_list;
        }

        public void setStribe_list(List<SchoolListBean> stribe_list) {
            this.stribe_list = stribe_list;
        }

        public List<SchoolListBean> getSchool_list() {
            return school_list;
        }

        public void setSchool_list(List<SchoolListBean> school_list) {
            this.school_list = school_list;
        }

        public List<SchoolListBean> getHometown_list() {
            return hometown_list;
        }

        public void setHometown_list(List<SchoolListBean> hometown_list) {
            this.hometown_list = hometown_list;
        }

        public List<SchoolListBean> getCofc_list() {
            return cofc_list;
        }

        public void setCofc_list(List<SchoolListBean> cofc_list) {
            this.cofc_list = cofc_list;
        }

        public List<SchoolListBean> getClan_list() {
            return clan_list;
        }

        public void setClan_list(List<SchoolListBean> clan_list) {
            this.clan_list = clan_list;
        }

        public static class SchoolListBean {
            @Override
            public String toString() {
                return "SchoolListBean{" +
                        "user_id='" + user_id + '\'' +
                        ", school_id=" + school_id +
                        ", hometown_id=" + hometown_id +
                        ", cofc_id='" + cofc_id + '\'' +
                        ", clan_id='" + clan_id + '\'' +
                        ", name='" + name + '\'' +
                        ", logo='" + logo + '\'' +
                        ", banner='" + banner + '\'' +
                        ", brief_desc='" + brief_desc + '\'' +
                        ", count=" + count +
                        ", type='" + type + '\'' +
                        ", from=" + from +
                        '}';
            }

            /**
             * school_id : 18
             * name : 清华大学
             * logo : https://imgcdn.66boss.com/contact/emo_School/2017/03/22/2017-03-22163432506173.jpeg
             * banner : https://imgcdn.66boss.com/contact/emo_School/2017/03/22/2017-03-22163432850532.jpeg
             * brief_desc : 清华大学是名牌大学
             * count : 1
             */

            private String user_id="";

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            private int school_id;
            private int hometown_id;
            private String cofc_id;
            private String clan_id;
            private String name;
            private String logo;
            private String banner;
            private String brief_desc;
            private int count;
            private String type;      //我的学校 我的家乡...
            private String stribe_id;

            public String getStribe_id() {
                return stribe_id;
            }

            public void setStribe_id(String stribe_id) {
                this.stribe_id = stribe_id;
            }

            private int from;

            public int getFrom() {
                return from;
            }

            public void setFrom(int from) {
                this.from = from;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getHometown_id() {
                return hometown_id;
            }

            public void setHometown_id(int hometown_id) {
                this.hometown_id = hometown_id;
            }

            public String getCofc_id() {
                return cofc_id;
            }

            public void setCofc_id(String cofc_id) {
                this.cofc_id = cofc_id;
            }

            public String getClan_id() {
                return clan_id;
            }

            public void setClan_id(String clan_id) {
                this.clan_id = clan_id;
            }

            public int getSchool_id() {
                return school_id;
            }

            public void setSchool_id(int school_id) {
                this.school_id = school_id;
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

            public String getBanner() {
                return banner;
            }

            public void setBanner(String banner) {
                this.banner = banner;
            }

            public String getBrief_desc() {
                return brief_desc;
            }

            public void setBrief_desc(String brief_desc) {
                this.brief_desc = brief_desc;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }

    }


}
