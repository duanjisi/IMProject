package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/3/6.
 */

public class SaveUserInfoRequest extends BaseDataRequest<String> {

    public SaveUserInfoRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String sex = (String) mParams[0];
        String birthday = (String) mParams[1];
        String province = (String) mParams[2];
        String city = (String) mParams[3];
        String district = (String) mParams[4];
        String ht_province = (String) mParams[5];
        String ht_city = (String) mParams[6];
        String ht_district = (String) mParams[7];
        String industry = (String) mParams[8];
        String interest = (String) mParams[9];

        HashMap<String, String> map = new HashMap<>();
        map.put("sex",sex);
        map.put("birthday",birthday);
        map.put("province",province);
        map.put("city",city);
        map.put("district",district);
        map.put("ht_province",ht_province);
        map.put("ht_city",ht_city);
        map.put("ht_district",ht_district);
        map.put("industry",industry);
        map.put("interest",interest);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.SAVE_USER_INFO;
    }
}
