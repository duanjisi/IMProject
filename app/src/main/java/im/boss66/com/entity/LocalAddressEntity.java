package im.boss66.com.entity;


import java.io.Serializable;
import java.util.List;

/**
 * Created by GMARUnity on 2017/2/18.
 */
public class LocalAddressEntity {
    private int state;
    private String message;

    public SecondChild getResult() {
        return result;
    }

    public void setResult(SecondChild result) {
        this.result = result;
    }

    private SecondChild result;

    public static class SecondChild{
        public List<ThreeChild> getList() {
            return list;
        }

        public void setList(List<ThreeChild> list) {
            this.list = list;
        }

        private List<ThreeChild> list;
    }

    public static class ThreeChild implements Serializable {
        private String region_id;
        private String region_name;
        private List<FourChild> list;

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        public List<FourChild> getList() {
            return list;
        }

        public void setList(List<FourChild> list) {
            this.list = list;
        }

    }

    public static class FourChild implements Serializable {
        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        public List<LastChild> getList() {
            return list;
        }

        public void setList(List<LastChild> list) {
            this.list = list;
        }

        private String region_id;
        private String region_name;
        private List<LastChild> list;

    }

    public static class LastChild implements Serializable{
        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        private String region_id;
        private String region_name;


    }

}
