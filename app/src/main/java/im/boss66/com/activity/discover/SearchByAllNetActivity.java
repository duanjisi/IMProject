package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.EmoStore;
import im.boss66.com.entity.SearchCofriendEntity;
import im.boss66.com.entity.SearchDataEntity;
import im.boss66.com.entity.SearchFriendorFaceEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.MyListView;

/**
 * Created by GMARUnity on 2017/3/2.
 */
public class SearchByAllNetActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {

    private MyListView lv_friend, lv_face;
    private TextView tv_friend_title, tv_face_title;
    private ScrollView sv_content;
    private View v_line_friend_top, v_line_face_top;
    private RelativeLayout rl_friend_bottom, rl_face_bottom, rl_default;
    private EditText et_search;
    private TextView tv_close;
    private String access_token, keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_all_net);
        initView();
    }

    private void initView() {
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();

        rl_default = (RelativeLayout) findViewById(R.id.rl_default);
        lv_face = (MyListView) findViewById(R.id.lv_face);
        lv_friend = (MyListView) findViewById(R.id.lv_friend);
        sv_content = (ScrollView) findViewById(R.id.sv_content);
        tv_friend_title = (TextView) findViewById(R.id.tv_friend_title);
        tv_face_title = (TextView) findViewById(R.id.tv_face_title);
        v_line_friend_top = findViewById(R.id.v_line_friend_top);
        v_line_face_top = findViewById(R.id.v_line_face_top);
        rl_friend_bottom = (RelativeLayout) findViewById(R.id.rl_friend_bottom);
        rl_face_bottom = (RelativeLayout) findViewById(R.id.rl_face_bottom);

        et_search = (EditText) findViewById(R.id.et_search);
        tv_close = (TextView) findViewById(R.id.tv_close);
        tv_close.setOnClickListener(this);
        et_search.setOnEditorActionListener(this);
        et_search.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                keyword = et_search.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    searchBySever();
                } else {
                    finish();
                }
                break;
        }
    }

    private void searchBySever() {
        showLoadingDialog();
            String url = HttpUrl.SEARCH_BY_ALL_NET + "?key=" + keyword;
            HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
            com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
            params.addBodyParameter("access_token", access_token);
            httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    try {
                        SearchFriendorFaceEntity data = JSON.parseObject(result, SearchFriendorFaceEntity.class);
                        if (data != null) {
                            int code = data.getCode();
                            if (code == 1) {
                                SearchDataEntity searchData = data.getResult();
                                if (searchData != null) {
                                    showData(searchData);
                                }
                            } else {
                                showToast("获取数据失败", false);
                            }
                        }
                    } catch (Exception e) {
                        showToast("获取数据失败", false);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast("获取数据失败", false);
            }
        });
    }

    private void showData(SearchDataEntity searchData) {
        SearchDataEntity.SearchStore store = searchData.store;
        List<EmoStore> storelist = store.getList();
        SearchDataEntity.SearchCofriend cofirend = searchData.cofriend;
        List<SearchCofriendEntity> firendlist = cofirend.getList();
        if (firendlist != null && firendlist.size() > 0) {
            sv_content.setVisibility(View.VISIBLE);
            rl_default.setVisibility(View.GONE);
            v_line_friend_top.setVisibility(View.VISIBLE);
            lv_friend.setVisibility(View.VISIBLE);
            tv_friend_title.setVisibility(View.VISIBLE);
            if (firendlist.size() > 3) {
                rl_friend_bottom.setVisibility(View.VISIBLE);
            } else {
                rl_friend_bottom.setVisibility(View.GONE);
            }
        } else {
            sv_content.setVisibility(View.GONE);
            v_line_friend_top.setVisibility(View.GONE);
            lv_friend.setVisibility(View.GONE);
            tv_friend_title.setVisibility(View.GONE);
            rl_friend_bottom.setVisibility(View.GONE);
        }
        if (storelist != null && storelist.size() > 0) {
            rl_default.setVisibility(View.GONE);
            sv_content.setVisibility(View.VISIBLE);
            lv_face.setVisibility(View.VISIBLE);
            tv_face_title.setVisibility(View.VISIBLE);
            v_line_face_top.setVisibility(View.VISIBLE);
            if (storelist.size() > 3) {
                rl_face_bottom.setVisibility(View.VISIBLE);
            } else {
                rl_face_bottom.setVisibility(View.GONE);
            }
        } else {
            if (sv_content.getVisibility() == View.GONE) {
                rl_default.setVisibility(View.VISIBLE);
            }
            v_line_face_top.setVisibility(View.GONE);
            lv_face.setVisibility(View.GONE);
            tv_face_title.setVisibility(View.GONE);
            rl_face_bottom.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            keyword = et_search.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                searchBySever();
            } else {
                showToast("搜索关键字不能为空", false);
            }
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int len = editable.length();
        if (len > 0) {
            tv_close.setText("搜索");
        } else {
            tv_close.setText("取消");
        }
    }
}
