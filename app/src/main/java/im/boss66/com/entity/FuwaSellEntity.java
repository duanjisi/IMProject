package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/15.
 */
public class FuwaSellEntity {

    /**
     * message : OK
     * code : 0
     * data : [{"orderid":11,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66},{"orderid":12,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66},{"orderid":13,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66},{"orderid":14,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66},{"orderid":15,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66},{"orderid":16,"owner":"xiechc","amount":100.1,"fuwagid":"fuwa_33","fuwaid":66}]
     */

    private String message;
    private int code;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * orderid : 11
         * owner : xiechc
         * amount : 100.1
         * fuwagid : fuwa_33
         * fuwaid : 66
         */

        private int orderid;
        private String owner;
        private Double amount;
        private String fuwagid;
        private int fuwaid;

        public int getOrderid() {
            return orderid;
        }

        public void setOrderid(int orderid) {
            this.orderid = orderid;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getFuwagid() {
            return fuwagid;
        }

        public void setFuwagid(String fuwagid) {
            this.fuwagid = fuwagid;
        }

        public int getFuwaid() {
            return fuwaid;
        }

        public void setFuwaid(int fuwaid) {
            this.fuwaid = fuwaid;
        }
    }

    @Override
    public String toString() {
        return "FuwaSellEntity{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
