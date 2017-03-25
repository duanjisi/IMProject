package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/3/25.
 */
public class OrderSystemNoticeRequest extends BaseModelRequest<String> {

    public OrderSystemNoticeRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String orderid = (String) mParams[0];
        String buyer = (String) mParams[1];
        String fuwagid = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("orderid", orderid);
        map.put("buyer", buyer);
        map.put("fuwagid", fuwagid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.SYSTEM_PAY_NOTICE;
    }
}
