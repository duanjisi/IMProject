package im.boss66.com.entity;

/**
 * Created by liw on 2017/3/17.
 */

public class FuwaDetailEntity {

    /**
     * message : Ok
     * code : 0
     * data : {"pos":"广州逸丰酒店","creator":"xiechc"}
     */

    private String message;
    private int code;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pos : 广州逸丰酒店
         * creator : xiechc
         */

        private String pos;
        private String creator;
        private String fuwaId;

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getFuwaId() {
            return fuwaId;
        }

        public void setFuwaId(String fuwaId) {
            this.fuwaId = fuwaId;
        }
    }
}
