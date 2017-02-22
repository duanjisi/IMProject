package im.boss66.com.entity;

/**
 * Created by admin on 2017/2/20.
 */
public class MySchool {
    private String img;
    private String schoolname;
    private String schoolinfo;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getSchoolinfo() {
        return schoolinfo;
    }

    public void setSchoolinfo(String schoolinfo) {
        this.schoolinfo = schoolinfo;
    }

    @Override
    public String toString() {
        return "MySchool{" +
                "img='" + img + '\'' +
                ", schoolname='" + schoolname + '\'' +
                ", schoolinfo='" + schoolinfo + '\'' +
                '}';
    }
}
