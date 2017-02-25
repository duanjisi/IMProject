package im.boss66.com.entity;

/**
 * Created by liw on 2017/2/23.
 */
public class MyFollow {
    private String img;
    private String tv1;
    private String tv2;
    private String tv3;

    @Override
    public String toString() {
        return "MyFollow{" +
                "img='" + img + '\'' +
                ", tv1='" + tv1 + '\'' +
                ", tv2='" + tv2 + '\'' +
                ", tv3='" + tv3 + '\'' +
                '}';
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTv1() {
        return tv1;
    }

    public void setTv1(String tv1) {
        this.tv1 = tv1;
    }

    public String getTv2() {
        return tv2;
    }

    public void setTv2(String tv2) {
        this.tv2 = tv2;
    }

    public String getTv3() {
        return tv3;
    }

    public void setTv3(String tv3) {
        this.tv3 = tv3;
    }
}
