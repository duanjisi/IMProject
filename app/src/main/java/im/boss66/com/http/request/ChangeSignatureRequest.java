package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 修改个性签名
 */
public class ChangeSignatureRequest extends BaseDataRequest<String>{

    public ChangeSignatureRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String signature = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("signature", signature);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANE_SIGNATURE;
    }
}
