package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/16.
 */

public class MyFuwaEntity2 {
    private String id;
    private List<MyFuwaEntity2.DataBean> data;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * gid : fuwa_1
         * id : 8
         */

        private String gid;
        private String id;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
