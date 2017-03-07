package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/3/4.
 */

public class EditSchoolRequest extends BaseDataRequest<String> {

    private String us_id;

    public EditSchoolRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String school_id = (String) mParams[0];
        String note = (String) mParams[1];
        String edu_year = (String) mParams[2];

        us_id = (String) mParams[3];


        Map<String, String> map = new HashMap<String, String>();
        map.put("school_id",school_id);
        map.put("note",note);
        map.put("edu_year",edu_year);

        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EDIT_SCHOOL_INFO+"?us_id=" + us_id ;
    }
}
