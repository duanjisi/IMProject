package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/3/4.
 */

public class SaveSchoolRequest extends BaseDataRequest<String> {

    public SaveSchoolRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String level = (String) mParams[0];
        String edu_year = (String) mParams[1];
        String school_id = (String) mParams[2];
        String note = (String) mParams[3];

        Map<String, String> map = new HashMap<String, String>();
        map.put("level",level);
        map.put("edu_year",edu_year);
        map.put("school_id",school_id);
        map.put("note",note);

        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.SAVE_SCHOOL_INFO;
    }
}
