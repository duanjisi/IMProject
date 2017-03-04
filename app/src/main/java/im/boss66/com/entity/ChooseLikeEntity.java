package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/3.
 */

public class ChooseLikeEntity {


    /**
     * name : ucenter api
     * message : succeed
     * code : 1
     * status : 200
     * type : yii\Response
     * result : [{"tag_id":1,"tag_name":"教育"},{"tag_id":2,"tag_name":"旅游"},{"tag_id":3,"tag_name":"金融"},{"tag_id":4,"tag_name":"汽车"},{"tag_id":5,"tag_name":"房产"},{"tag_id":6,"tag_name":"家居"},{"tag_id":7,"tag_name":"服饰鞋帽箱包"},{"tag_id":8,"tag_name":"餐饮美食"},{"tag_id":9,"tag_name":"生活服务"},{"tag_id":10,"tag_name":"商务服务"},{"tag_id":11,"tag_name":"美容"},{"tag_id":12,"tag_name":"互联网"},{"tag_id":13,"tag_name":"电子产品"},{"tag_id":14,"tag_name":"体育运动"},{"tag_id":15,"tag_name":"医疗健康"},{"tag_id":16,"tag_name":"孕产育儿"},{"tag_id":17,"tag_name":"游戏"},{"tag_id":18,"tag_name":"政法"}]
     */

    private String name;
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
         * tag_id : 1
         * tag_name : 教育
         */

        private int tag_id;
        private String tag_name;

        public int getTag_id() {
            return tag_id;
        }

        public void setTag_id(int tag_id) {
            this.tag_id = tag_id;
        }

        public String getTag_name() {
            return tag_name;
        }

        public void setTag_name(String tag_name) {
            this.tag_name = tag_name;
        }
    }
}
