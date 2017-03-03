package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.JobEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by admin on 2017/3/1.
 */

public class ChooseJobRequest extends BaseDataRequest<String> {
    public ChooseJobRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        return new HashMap<String,String>();
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHOOSE_JOB_LIST;
    }
}
