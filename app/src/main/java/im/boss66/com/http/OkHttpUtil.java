package im.boss66.com.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    static {
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    /**
     * 开启同步线程访问网络
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    public static String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 把参数列表Map转换成String
     *
     * @param params         参数列表Map
     * @param paramsEncoding 编码格式
     * @return
     */
    public static String formatParams(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry != null) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * 把参数列表Map转换成String
     *
     * @param params 参数列表Map
     * @return
     */
    public static String formatParams(Map<String, String> params) {
        return formatParams(params, DEFAULT_PARAMS_ENCODING);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url            原始的新势力
     * @param params         参数列表Map
     * @param paramsEncoding 编码格式
     * @return
     */
    public static String attachHttpGetParams(String url, Map<String, String> params, String paramsEncoding) {
        return url + "?" + formatParams(params, paramsEncoding);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url    原始的新势力
     * @param params 参数列表Map
     * @return
     */
    public static String attachHttpGetParams(String url, Map<String, String> params) {
        return attachHttpGetParams(url, params, DEFAULT_PARAMS_ENCODING);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }

    /**
     * 取消请求
     * @param tag 访请求的标签
     */
    public static void cancelRequest(String tag) {
        mOkHttpClient.cancel(tag);
    }
}