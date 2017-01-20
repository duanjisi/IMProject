package im.boss66.com.entity;


/**
 * Created by Administrator on 2015/7/23.
 */
public class JsonEntity {

    private int id;
    private String userId = "";

    private String json;

    private String authKey = "";

    private String deveiceId;

    public String getDeveiceId() {
        return deveiceId;
    }

    public void setDeveiceId(String deveiceId) {
        this.deveiceId = deveiceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public String toString() {
        return "JsonEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", json='" + json + '\'' +
                ", authKey='" + authKey + '\'' +
                ", deveiceId='" + deveiceId + '\'' +
                '}';
    }
}
