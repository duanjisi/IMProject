package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.AlipayOrder;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/3/24.
 */
public class OrderAlipayRequest extends BaseDataRequest<AlipayOrder> {

    public OrderAlipayRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String orderid = (String) mParams[0];
        String amount = (String) mParams[1];
        String fuwagid = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("orderid", orderid);
        map.put("amount", amount);
        map.put("fuwagid", fuwagid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FUWA_PAY_ALIPAY;
    }
}
