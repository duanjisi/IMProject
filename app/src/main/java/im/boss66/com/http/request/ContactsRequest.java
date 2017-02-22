package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseContact;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/16.
 */
public class ContactsRequest extends BaseDataRequest<BaseContact> {

    public ContactsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CONTACTS_URL;
    }
}
