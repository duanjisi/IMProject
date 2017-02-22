package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/2/20.
 */
public class CellEntity {
    private int resId = 0;
    private String name = "";

    public CellEntity(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
