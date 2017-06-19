package im.boss66.com.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


import java.util.Timer;
import java.util.TimerTask;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.connection.AddPeopleActivity;
import im.boss66.com.entity.ConnectionAllSearch;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.CommunityMsgListener;
import im.boss66.com.widget.DialogFactory;

/**
 * Created by liw on 2017/2/21.
 */
public class SearchPop extends BasePopuWindow implements View.OnClickListener {
    private View view;
    private Context context;
    public EditText et_search;
    private int fragment_position;
    private SchoolmateListEntity schoolmateListEntity;
    private ConnectionAllSearch connectionAllSearch;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //刷新对应fragment的adaper
                    listener.refreash(fragment_position, schoolmateListEntity);
                    break;
                case 2:
                    listener.refreashAll(fragment_position, connectionAllSearch);

                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;

    public SearchPop(Context context) {
        super(context);
        this.context = context;
        this.getBackground().setAlpha(150);
    }
    /**
     * 显示加载进度条
     *
     * @return: void
     */
    protected void showLoadingDialog() {
            if (mProgressDialog == null) {
                mProgressDialog = createProgressDialog();
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = DialogFactory.createProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading));
        dialog.setIndeterminate(true);
        // dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        return dialog;
    }
    /**
     * 取消加载进度条
     *
     * @return: void
     */
    protected void cancelLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void setFragment_position(int fragment_position) {
        this.fragment_position = fragment_position;
    }

    @Override
    protected View getRootView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.pop_search, null);
        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // 搜索
                    dismiss();
                    if(fragment_position!=2){

                        initData();
                    }else{
                        initAllData();
                    }

                }
                return false;
            }
        });
//      只会第一次打开显示
//        et_search.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                InputMethodManager manager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
//                manager.showSoftInput(et_search, 0);
//            }
//        });

        return view;
    }

    private void initAllData() {
        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.SEARCH_CONNECTION_ALL;
        url = url + "?key=" + et_search.getText().toString();
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    Log.i("liwya",result);
                    connectionAllSearch = JSON.parseObject(result, ConnectionAllSearch.class);

                    if (connectionAllSearch != null) {
                        if (connectionAllSearch.getCode() == 1) {
                            handler.obtainMessage(2).sendToTarget();
                        } else {
                            ToastUtil.show(context, connectionAllSearch.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }

                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                ToastUtil.show(context, e.getMessage(), Toast.LENGTH_SHORT);
            }
        });

    }

    private void initData() {
        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.SEARCH_PEOPLE;
        url = url + "?key=" + et_search.getText().toString() + "&page=" + 0 + "&size=" + "10";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    schoolmateListEntity = JSON.parseObject(result, SchoolmateListEntity.class);
                    if (schoolmateListEntity != null) {
                        if (schoolmateListEntity.getCode() == 1) {
                            handler.obtainMessage(1).sendToTarget();
                        } else {
                            ToastUtil.show(context, schoolmateListEntity.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                ToastUtil.show(context, e.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void dismiss() {

        super.dismiss();
        listener.setVisible();

    }

    public interface ICallBack {
        void setVisible();

        void refreash(int position, SchoolmateListEntity schoolmateListEntity);
        void refreashAll(int position, ConnectionAllSearch connectionAllSearch);
    }

    private ICallBack listener;

    public void setListener(ICallBack listener) {
        this.listener = listener;
    }


}
