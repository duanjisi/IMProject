package im.boss66.com.entity;

import android.widget.TextView;

/**
 * Created by liw on 2017/2/23.
 */
public class MyMessage {
    private String img1;
    private String img2;
    private String tv1;
    private String tv2;
    private String tv3;

    @Override
    public String toString() {
        return "MyMessage{" +
                "img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", tv1=" + tv1 +
                ", tv2=" + tv2 +
                ", tv3=" + tv3 +
                '}';
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
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
