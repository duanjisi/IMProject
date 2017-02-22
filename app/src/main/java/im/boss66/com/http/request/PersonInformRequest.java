package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.PersonEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/21.
 */
public class PersonInformRequest extends BaseDataRequest<PersonEntity> {

    public PersonInformRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String user_id = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.PERSON_INFORM_URL;
    }
}
