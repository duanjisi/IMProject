/**
 * Summary: App网络请求数据上传基础模型
 */
package im.boss66.com.http;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.jodah.typetools.TypeResolver;

import java.io.IOException;
import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.db.dao.JsonDao;

public abstract class BaseDataModel<T> {
    private static String TAG = "BaseDataModel";
    private final Class<T> mGenericPojoClazz;
    private final String mTag;
    protected Object[] mParams;
    private JsonDao jsonDao;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    protected BaseDataModel(String tag, Object... params) {
        mGenericPojoClazz = (Class<T>) TypeResolver.resolveRawArgument(BaseDataModel.class, getClass());
        mTag = tag;
        mParams = params;
//        jsonDao = JsonDao.getInstance();
    }


    public void send(final RequestCallback callback) {
        final Request request = new Request.Builder()
                .url(getApiUrl())
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
                        case 0://正常
                            final T retT;
                            if (BaseDataModel.this instanceof PageDataMode) {
                                retT = (T) JSON.parseObject(rspContent,
                                        ((PageDataMode) BaseDataModel.this).getSubPojoType());
                            } else {
                                retT = JSON.parseObject(rspContent, mGenericPojoClazz);
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(retT);
                                }
                            });

                            break;
                        case 403:

                            break;
                        case 10011:
                            //新增错误返回码：10011，通知移动端最近登录设备改变取消用户登录状态
//                            deviceOutLine();
                            break;
                        default:
                            final PreRspPojo finalPreRspPojo = preRspPojo;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onFailure(finalPreRspPojo.message);
                                }
                            });
                            break;
                    }
                } catch (Exception e) {
                    MycsLog.i("info", "Exception:" + e.getMessage());
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
    /**
     * 转换缓存实体
     *
     * @return
     */

    protected abstract Map<String, String> getParams();


    private String getEnd(Map<String, String> params) {
        Map<String, String> encryptionParams = HttpUtil.getEncryptionParams(getParams());
        return OkHttpUtil.formatParams(encryptionParams);
    }

    protected abstract String getApiPath();

    public String getApiUrl() {
        String url = getApiPath() + getEnd(getParams());
        return url;
    }

    public interface RequestCallback<T> {
        void onSuccess(T pojo);

        void onFailure(String msg);
    }
}