package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.CommunityMsgListener;

/**
 * Created by admin on 2017/2/21.
 */
public class SearchPop extends BasePopuWindow implements View.OnClickListener {
    private View view;
    private Context context;
    private EditText et_search;
    private int fragment_position;
    private SchoolmateListEntity schoolmateListEntity;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //刷新对应fragment的adaper
                    listener.refreash(fragment_position, schoolmateListEntity);
                    dismiss();
                    break;
            }
        }
    };

    public SearchPop(Context context, int fragment_position) {
        super(context);
        this.fragment_position = fragment_position;
        this.context = context;
        this.getBackground().setAlpha(150);
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
//                    initData();

                }
                return false;
            }
        });

        return view;
    }

    private void initData() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.SEARCH_PEOPLE;
        url = url + "?key=" + et_search.getText().toString() + "&page=" + 0 + "&size=" + "10";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
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
    }

    private ICallBack listener;

    public void setListener(ICallBack listener) {
        this.listener = listener;
    }


}
