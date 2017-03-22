package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaMessageAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.FuwaMsg;
import im.boss66.com.entity.FuwaMsgItem;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.slideListView.SlideListView;

/**
 * Created by GMARUnity on 2017/3/18.
 */
public class FuwaMessageActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back;
    private SlideListView lv_listview;
    private FuwaMessageAdapter adapter;
    private List<FuwaMsgItem> list;
    private DbUtils mDbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_message);
        initView();
    }

    private void initView() {
        mDbUtils = DbUtils.create(this);
        try {
            list = mDbUtils.findAll(FuwaMsgItem.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        tv_back = (TextView) findViewById(R.id.tv_back);
        lv_listview = (SlideListView) findViewById(R.id.lv_listview);

        tv_back.setOnClickListener(this);
        adapter = new FuwaMessageAdapter(this, list);
        adapter.getDb(mDbUtils);
        lv_listview.setAdapter(adapter);
        getServerData();
    }

    private void getServerData() {
        AccountEntity sAccount = App.getInstance().getAccount();
        String userId = sAccount.getUser_id();
        String url = HttpUrl.FUWA_MSG + userId;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    BaseResult data = BaseResult.parse(res);
                    if (data != null) {
                        int code = data.getCode();
                        if (code == 0) {
                            FuwaMsg fuwaMsg = JSON.parseObject(res, FuwaMsg.class);
                            if (fuwaMsg != null) {
                                list = fuwaMsg.data;
                                if (list != null && list.size() > 0) {
                                    try {
                                        mDbUtils.saveAll(list);
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                    adapter.onDataChange(list);
                                }
                            }
                        }
                    }
                } else {
                    showToast("获取消息失败TAT，请重试", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(s, false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

}
