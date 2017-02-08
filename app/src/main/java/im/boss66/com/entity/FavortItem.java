package im.boss66.com.entity;

import java.io.Serializable;

/**
 * 朋友圈点赞实体类
 */
public class FavortItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private User user;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
