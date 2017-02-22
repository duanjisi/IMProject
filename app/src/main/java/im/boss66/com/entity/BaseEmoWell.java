package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/18.
 * 精选表情实体基类
 */
public class BaseEmoWell {

    private ArrayList<EmoAdEntity> hot = new ArrayList<>();

    private ArrayList<EmoBagEntity> groups = new ArrayList<>();

    public ArrayList<EmoAdEntity> getHot() {
        return hot;
    }

    public void setHot(ArrayList<EmoAdEntity> hot) {
        this.hot = hot;
    }

    public ArrayList<EmoBagEntity> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<EmoBagEntity> groups) {
        this.groups = groups;
    }
}
