package im.boss66.com.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GMARUnity on 2017/3/16.
 */
public class FuwaEntity {
    private String message;
    private String code;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    private List<Data> data;

    public class Data {
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

        private String gid;
        private String id;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<String> getIdList() {
            return idList;
        }

        public void setIdList(List<String> idList) {
            this.idList = idList;
        }

        private int num;
        private List<String> idList = new ArrayList<>();
    }
}
