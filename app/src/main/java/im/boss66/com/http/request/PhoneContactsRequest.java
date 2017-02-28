package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BasePhoneContact;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/27.
 * 匹配本地手机联系人
 */
public class PhoneContactsRequest extends BaseDataRequest<BasePhoneContact> {

    public PhoneContactsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String phones = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("phones", phones);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.MATCH_PHONE_BOOK_URL;
    }
}
