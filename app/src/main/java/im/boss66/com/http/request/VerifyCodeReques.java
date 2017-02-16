package im.boss66.com.http.request;

import java.util.Map;

import im.boss66.com.entity.CodeEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 * 通用验证手机短信验证码
 */
public class VerifyCodeReques extends BaseDataRequest<CodeEntity> {

    public VerifyCodeReques(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        return null;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.SMS_URL;
    }
}
