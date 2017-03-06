package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/3/6.
 */

public class SaveSchoolEntity {

    /**
     * name : Userschool for Api v1
     * version : v1
     * message : 添加成功
     * code : 1
     * status : 200
     * type : yii\Response
     * result : []
     */

    private String name;
    private String version;
    private String message;
    private int code;
    private int status;
    private String type;
    private List<?> result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SaveSchoolEntity{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", status=" + status +
                ", type='" + type + '\'' +
                ", result=" + result +
                '}';
    }
}
