package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/8.
 */

public class SearchSchoolListEntity {


    /**
     * name : LOCAL-SCHOOL
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\SearchController
     * result : [{"id":"38","name":"中南林业科技大学-勿删","level":"5","region":"湖南长沙天心区"},{"id":"58","name":"中山大学","level":"5","region":"广东广州海珠区"}]
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
         * id : 38
         * name : 中南林业科技大学-勿删
         * level : 5
         * region : 湖南长沙天心区
         */

        private String id;
        private String name;
        private String level;
        private String region;

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

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
