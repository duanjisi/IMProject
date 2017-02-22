package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseEmoWell;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/18.
 */
public class EmoWellChoseRequest extends BaseDataRequest<BaseEmoWell> {

    public EmoWellChoseRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String page = (String) mParams[0];
        String size = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page);
        map.put("size", size);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_WELL_URL;
    }
}
