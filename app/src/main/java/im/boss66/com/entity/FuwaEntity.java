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

    public static  class Data {
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
        private boolean awarded;
        private String pos;
        private String creator;
        private String creatorid;

        public String getCreatorid() {
            return creatorid;
        }

        public void setCreatorid(String creatorid) {
            this.creatorid = creatorid;
        }

        public boolean isAwarded() {
            return awarded;
        }

        public void setAwarded(boolean awarded) {
            this.awarded = awarded;
        }

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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<String> getIdList() {
            return idList;
        }

        private List<FuwaDetail> fuwas = new ArrayList<>();

        public List<FuwaDetail> getFuwas() {
            return fuwas;
        }

        public void setFuwas(List<FuwaDetail> fuwas) {
            this.fuwas = fuwas;
        }

        public void setIdList(List<String> idList) {
            this.idList = idList;
        }

        private int num;
        private List<String> idList = new ArrayList<>();


        public boolean isSel() {
            return isSel;
        }

        public void setSel(boolean sel) {
            isSel = sel;
        }

        private boolean isSel = false;
    }
    public static  class FuwaDetail{
        public FuwaDetail(String gid, String id, boolean awarded, String pos, String creator,String creatorid) {
            this.gid = gid;
            this.id = id;
            this.awarded = awarded;
            this.pos = pos;
            this.creator = creator;
            this.creatorid = creatorid;
        }

        public String gid;
        public String id;
        public boolean awarded;
        public String pos;
        public String creator;
        public String creatorid;

    }

}
