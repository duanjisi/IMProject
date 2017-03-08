package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/8.
 */

public class SearchSchoolListEntity {

    /**
     * name : SCHOOL
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\SearchController
     * result : [{"id":"58","name":"中山大学","desc":"&nbsp; &nbsp; &nbsp;中山大学由孙中山先生创办，有着一百多年办学传统，是中国南方科学研究、文化学术与人才培养的重镇。作为中国教育部直属高校，通过部省共建，中山大学已经成为一所国内一流、国际知名的现代综合性大学...","region":"广东广州海珠区","level":"5"}]
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
         * id : 58
         * name : 中山大学
         * desc : &nbsp; &nbsp; &nbsp;中山大学由孙中山先生创办，有着一百多年办学传统，是中国南方科学研究、文化学术与人才培养的重镇。作为中国教育部直属高校，通过部省共建，中山大学已经成为一所国内一流、国际知名的现代综合性大学...
         * region : 广东广州海珠区
         * level : 5
         */

        private String id;
        private String name;
        private String desc;
        private String region;
        private String level;

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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }
}
