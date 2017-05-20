package im.boss66.com.entity;

import java.io.Serializable;

/**
 * Created by Johnny on 2017/3/15.
 */
public class ChildEntity implements Serializable {
    private String distance = "";
    private String pic = "";
    private String gid = "";
    private String geo = "";
    private String pos = "";
    private String id = "";
    private String detail = "";
    private String avatar = "";
    private String name = "";
    private String gender = "";
    private String signature = "";
    private String location = "";
    private String video = "";
    private String hider;
    private String number = "";//远处的福娃（数量）,少了gid

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getHider() {
        return hider;
    }

    public void setHider(String hider) {
        this.hider = hider;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (this.getClass() == obj.getClass()) {
                ChildEntity u = (ChildEntity) obj;
                if (this.getGid().equals(u.getGid())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}