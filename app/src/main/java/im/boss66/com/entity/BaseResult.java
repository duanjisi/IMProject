package im.boss66.com.entity;

import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 * Created by GMARUnity on 2017/3/7.
 */
public class BaseResult {
    private String name;
    private String version;
    private String message;
    private int code;
    private int status;
    private String type;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static BaseResult parse(String json) {
        BaseResult res = new BaseResult();
        try {
            res = JSON.parseObject(json, BaseResult.class);
        } catch (Exception e) {
            Log.e("Json Error", e.toString());
        }
        return res;
    }

}

