package im.boss66.com.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GMARUnity on 2017/2/16.
 */
public class PeopleNearbyEntity {

    public String name;
    public String message;
    public int code;
    public int status;
    public String type;

    public List<NearByChildEntity> getResult() {
        return result;
    }

    public void setResult(List<NearByChildEntity> result) {
        this.result = result;
    }

    private List<NearByChildEntity> result;
}
