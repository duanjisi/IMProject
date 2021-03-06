/**
 * Summary: App网络请求数据上传基础模型
 */
package im.boss66.com.http;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.jodah.typetools.TypeResolver;

import java.io.IOException;
import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.db.dao.JsonDao;
import im.boss66.com.entity.JsonEntity;

public abstract class BaseDataRequest<T> {
    private static String TAG = "BaseDataModel";
    private final Class<T> mGenericPojoClazz;
    private final String mTag;
    protected Object[] mParams;
    private JsonDao jsonDao;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    protected BaseDataRequest(String tag, Object... params) {
        mGenericPojoClazz = (Class<T>) TypeResolver.resolveRawArgument(BaseDataRequest.class, getClass());
        mTag = tag;
        mParams = params;
//        jsonDao = JsonDao.getInstance();
    }


    public void send(final RequestCallback callback) {
        final Request request = new Request.Builder()
                .url(getApiUrl())
                .post(getBody())
                .tag(mTag)
                .build();
//        String dbJson = dbRequest();
//        MycsLog.v("存在缓存数据:" + dbJson);
//        if (dbJson != null && !dbJson.equals("")) {//数据库存在数据
//            updateRequest(request, dbJson, callback);//网络请求更新数据库里数据
//        } else {
//            netRequest(request, callback);//网络请求
//        }
        netRequest(request, callback);//网络请求
    }

    private void netRequest(Request request, final RequestCallback callback) {//网络请求
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final App app = App.getInstance();
                if (NetworkUtil.networkAvailable(app)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(app.getString(R.string.error_generic_error));
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(app.getString(R.string.error_generic_server_down));
                        }
                    });
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String rspContent = response.body().string();
                MycsLog.d("Response\nAPI:" + getApiUrl() + "\nData:" + rspContent);
                PreRspPojo preRspPojo = null;
                try {
                    preRspPojo = JSON.parseObject(rspContent, PreRspPojo.class);
                    MycsLog.d("返回的code为" + preRspPojo.code);
                    switch (preRspPojo.code) {
                        case 1://正常
                            final T retT;
//                            jsonDao.save(switchJsonEntity(rspContent));
                            if (BaseDataRequest.this instanceof PageDataRequest) {
                                retT = (T) JSON.parseObject(preRspPojo.result,
                                        ((PageDataRequest) BaseDataRequest.this).getSubPojoType());
                            } else {
                                if (isParse()) {
                                    retT = JSON.parseObject(preRspPojo.result, mGenericPojoClazz);
                                } else {
                                    retT = JSON.parseObject(rspContent, mGenericPojoClazz);
                                }
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(retT);
                                }
                            });
                            break;
//                        case 401://token无效，注销当前登录
//                            deviceOutLine(preRspPojo.message);
//                            break;
                        case 10011:
                            //新增错误返回码：10011，通知移动端最近登录设备改变取消用户登录状态
                            MycsLog.d("收到了退出消息");
//                            deviceOutLine();
                            break;
                        default:
                            final PreRspPojo finalPreRspPojo = preRspPojo;
                            if (finalPreRspPojo.status == 401) {
                                deviceOutLine(finalPreRspPojo.message);
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFailure(finalPreRspPojo.message);
                                    }
                                });
                            }
                            break;
                    }
                } catch (Exception e) {
                    Log.i("info", "===============Exception:" + e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MycsLog.i("info", "callback:" + callback);
                            callback.onFailure(App.getInstance().getString(R.string.error_server_down));
                        }
                    });
//                    MycsLog.e(e);
                }
            }
        });
    }

    /**
     * 本地数据库请求（获取JSON文本）
     */
    private void deviceOutLine(String msg) {
//        logoutRequest();
//        ToastUtil.showLong(App.getInstance().getApplicationContext(), msg);
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }

    /**
     * 本地数据库请求（获取JSON文本）
     */
    private void showToast(String msg) {
        ToastUtil.showLong(App.getInstance().getApplicationContext(), msg);
    }

    private String dbRequest() {
        Map<String, String> map = getParams();
        String userId = "";
//        if (map.containsKey(K.Request.USER_ID)) {
//            userId = map.get(K.Request.USER_ID);
//        }
        Map<String, String> authMap = HttpUtil.getEncryptionParams(map);
        String authKey = authMap.get("authKey");
        JsonEntity entity = jsonDao.QureJson(authKey, userId);
        String json = null;
        if (entity != null) {
            json = entity.getJson();
        }
        return json;
    }

    /**
     * 转换缓存实体
     *
     * @param jsonStr
     * @return
     */
    private JsonEntity switchJsonEntity(String jsonStr) {
        JsonEntity entity = new JsonEntity();
        Map<String, String> map = getParams();
        String userId = "";
//        if (map.containsKey(K.Request.USER_ID)) {
//            userId = map.get(K.Request.USER_ID);
//        }
        Map<String, String> authMap = HttpUtil.getEncryptionParams(map);
        String authKey = authMap.get("authKey");
        entity.setUserId(userId);
        entity.setAuthKey(authKey);
        entity.setJson(jsonStr);
        return entity;
    }

    protected abstract boolean isParse();

    protected abstract Map<String, String> getParams();

    private RequestBody getBody() {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        Map<String, String> encryptionParams = HttpUtil.getEncryptionParams(getParams());
        MycsLog.v("获取到的encryptionParams" + encryptionParams);
        for (Map.Entry<String, String> entry : encryptionParams.entrySet()) {
            if (entry != null) {
                formEncodingBuilder.add(entry.getKey(), entry.getValue());
//                formEncodingBuilder.add()
            }
        }

        MycsLog.d("Request\nAPI:" + getApiUrl() + "?" + OkHttpUtil.formatParams(encryptionParams));

        RequestBody body = formEncodingBuilder.build();

        return body;
    }

    private String getEnd(Map<String, String> params) {
        Map<String, String> encryptionParams = HttpUtil.getEncryptionParams(getParams());
        return OkHttpUtil.formatParams(encryptionParams);
    }

    protected abstract String getApiPath();

    public String getApiUrl() {
        return getApiPath();
    }

    public interface RequestCallback<T> {
        void onSuccess(T pojo);

        void onFailure(String msg);
    }

}