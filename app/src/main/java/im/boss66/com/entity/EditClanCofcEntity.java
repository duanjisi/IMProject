package im.boss66.com.entity;

/**
 * Created by liw on 2017/4/15.
 */

public class EditClanCofcEntity {

    /**
     * name : UPDATE
     * version : v1
     * message : 修改成功
     * code : 1
     * status : 200
     * type : app\modules\api\modules\v1\controllers\CofcController
     * result : {"banner":"https://imgcdn.66boss.com/clan/banner/2017/04/14/58f0746f18d14.jpeg","logo":"https://imgcdn.66boss.com/clan/logo/2017/04/14/58f0746f18d14.jpeg"}
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
         * banner : https://imgcdn.66boss.com/clan/banner/2017/04/14/58f0746f18d14.jpeg
         * logo : https://imgcdn.66boss.com/clan/logo/2017/04/14/58f0746f18d14.jpeg
         */

        private String banner;
        private String logo;

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}
