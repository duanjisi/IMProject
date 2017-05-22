package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/5/19.
 */
public class BaseBaby {

    private ArrayList<ChildEntity> far = new ArrayList<>();
    private ArrayList<ChildEntity> near = new ArrayList<>();

    public ArrayList<ChildEntity> getFar() {
        return far;
    }

    public void setFar(ArrayList<ChildEntity> far) {
        this.far = far;
    }

    public ArrayList<ChildEntity> getNear() {
        return near;
    }

    public void setNear(ArrayList<ChildEntity> near) {
        this.near = near;
    }
}
