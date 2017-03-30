package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/8.
 */

public class NewsEntity {

    /**
     * name : MESSAGE
     * version : v1
     * message : succeed
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\HometownController
     * result : [{"id":"162","pid":"295","title":"伦洲岛","content":"清城地文景观类型多样，其中的山岳峡谷景观有\u201c青城第一山\u201d之称的大帽山，地处洲心的南山岭，东城的黄腾峡。十分独特的砂岩景观\u2014\u2014石角丹霞，此外，北江河清城段有广东省最大的内河岛\u2014\u2014伦洲岛。","pics":["https://imgcdn.66boss.com/contact/emo_HometownMessage/2017/03/29/2017-03-29034335559633.jpg"]}]
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
         * id : 162
         * pid : 295
         * title : 伦洲岛
         * content : 清城地文景观类型多样，其中的山岳峡谷景观有“青城第一山”之称的大帽山，地处洲心的南山岭，东城的黄腾峡。十分独特的砂岩景观——石角丹霞，此外，北江河清城段有广东省最大的内河岛——伦洲岛。
         * pics : ["https://imgcdn.66boss.com/contact/emo_HometownMessage/2017/03/29/2017-03-29034335559633.jpg"]
         */

        private String id;
        private String pid;
        private String title;
        private String content;
        private List<String> pics;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getPics() {
            return pics;
        }

        public void setPics(List<String> pics) {
            this.pics = pics;
        }
    }
}
