package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.EmotionEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/23.
 * 表情编码解析请求
 */
public class EmoParseRequest extends BaseDataRequest<EmotionEntity> {

    public EmoParseRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String data = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", data);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_PARSE_URL;
    }
}
