package im.boss66.com.activity.discover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.EmojiGroupDetailsActivity;
import im.boss66.com.adapter.EmoStoreAdapter;
import im.boss66.com.adapter.SearchCircleAdapter;
import im.boss66.com.adapter.UserAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.EmoStore;
import im.boss66.com.entity.SearchCofriendEntity;
import im.boss66.com.entity.SearchDataEntity;
import im.boss66.com.entity.SearchFriendorFaceEntity;
import im.boss66.com.entity.UserInform;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.MyListView;

/**
 * Created by GMARUnity on 2017/3/2.
 */
public class SearchByAllNetActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {

    private MyListView lv_friend, lv_face, lv_people;
    private TextView tv_friend_title, tv_face_title, tv_circle_more, tv_face_more, tv_people_title;
    private ScrollView sv_content;
    private View v_line_friend_top, v_line_face_top, v_line_people_top;
    private RelativeLayout rl_friend_bottom, rl_face_bottom, rl_default, rl_people_bottom;
    private EditText et_search;
    private TextView tv_close;
    private String access_token, keyword;
    private EmoStoreAdapter storeAdapter;
    private SearchCircleAdapter searchCircleAdapter;
    private String circleMoreUrl, faceMoreUrl, peopleMoreUrl;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_all_net);
        initView();
    }

    private void initView() {
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();

        lv_people = (MyListView) findViewById(R.id.lv_people);
        tv_people_title = (TextView) findViewById(R.id.tv_people_title);
        v_line_people_top = findViewById(R.id.v_line_people_top);
        rl_people_bottom = (RelativeLayout) findViewById(R.id.rl_people_bottom);

        tv_circle_more = (TextView) findViewById(R.id.tv_circle_more);
        tv_face_more = (TextView) findViewById(R.id.tv_face_more);
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
        tv_circle_more.setOnClickListener(this);
        tv_face_more.setOnClickListener(this);
        rl_people_bottom.setOnClickListener(this);
        searchCircleAdapter = new SearchCircleAdapter(this);
        lv_friend.setAdapter(searchCircleAdapter);
        storeAdapter = new EmoStoreAdapter(this);
        lv_face.setAdapter(storeAdapter);
        userAdapter = new UserAdapter(context);
        lv_people.setAdapter(userAdapter);
        lv_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserInform inform = (UserInform) adapterView.getItemAtPosition(i);
                if (inform != null) {
                    Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                    intent.putExtra("classType", "QureAccountActivity");
                    intent.putExtra("userid", inform.getUser_id());
                    startActivity(intent);
                }
            }
        });
        lv_face.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EmoStore store = (EmoStore) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SearchByAllNetActivity.this, EmojiGroupDetailsActivity.class);
                intent.putExtra("packid", store.getGroup_id());
                startActivity(intent);
            }
        });
        lv_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchCofriendEntity item = (SearchCofriendEntity) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SearchByAllNetActivity.this, PhotoAlbumDetailActivity.class);
                try {
                    int feed = Integer.parseInt(item.getFeed_id());
                    intent.putExtra("feedId", feed);
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
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
            case R.id.tv_circle_more://查看更多朋友圈
                Bundle bundle = new Bundle();
                bundle.putString("url", circleMoreUrl);
                bundle.putInt("type", 1);
                openActivity(SearchLookMoreActivity.class, bundle);
                break;
            case R.id.tv_face_more://查看更多表情
                Bundle bundle1 = new Bundle();
                bundle1.putString("url", faceMoreUrl);
                bundle1.putInt("type", 2);
                openActivity(SearchLookMoreActivity.class, bundle1);
                break;
            case R.id.rl_people_bottom://查看更联系人
                Bundle bundle2 = new Bundle();
                bundle2.putString("url", peopleMoreUrl);
                bundle2.putInt("type", 3);
                openActivity(SearchLookMoreActivity.class, bundle2);
                break;
        }
    }

    private void searchBySever() {
        showLoadingDialog();
        String url = HttpUrl.SEARCH_BY_ALL_NET + "?key=" + keyword;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        RequestParams params = new RequestParams();
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
        List<UserInform> userList = null;
        SearchDataEntity.SearchUser user = searchData.user;
        if (user != null) {
            peopleMoreUrl = user.getMore();
            userList = user.getList();
            if (userList != null && userList.size() > 0) {
                sv_content.setVisibility(View.VISIBLE);
                rl_default.setVisibility(View.GONE);
                v_line_people_top.setVisibility(View.VISIBLE);
                lv_people.setVisibility(View.VISIBLE);
                tv_people_title.setVisibility(View.VISIBLE);
                if (userList.size() > 3) {
                    List<UserInform> user3list = userList.subList(0, 3);
                    userAdapter.setData(user3list);
                    rl_people_bottom.setVisibility(View.VISIBLE);
                } else {
                    userAdapter.setData(userList);
                    rl_people_bottom.setVisibility(View.GONE);
                }
            } else {
                sv_content.setVisibility(View.GONE);
                v_line_people_top.setVisibility(View.GONE);
                lv_people.setVisibility(View.GONE);
                tv_people_title.setVisibility(View.GONE);
                rl_people_bottom.setVisibility(View.GONE);
            }
        }
        List<EmoStore> storelist = null;
        SearchDataEntity.SearchStore store = searchData.store;
        if (store != null) {
            if (!TextUtils.isEmpty(store.getMore())) {
                faceMoreUrl = store.getMore();
            } else {
                faceMoreUrl = store.getMoe();
            }
            storelist = store.getList();
        }
        SearchDataEntity.SearchCofriend cofirend = searchData.cofriend;
        List<SearchCofriendEntity> firendlist = null;
        if (cofirend != null) {
            circleMoreUrl = cofirend.getMore();
            firendlist = cofirend.getList();
        }
        if (firendlist != null && firendlist.size() > 0) {
            sv_content.setVisibility(View.VISIBLE);
            rl_default.setVisibility(View.GONE);
            v_line_friend_top.setVisibility(View.VISIBLE);
            lv_friend.setVisibility(View.VISIBLE);
            tv_friend_title.setVisibility(View.VISIBLE);
            if (firendlist.size() > 3) {
                List<SearchCofriendEntity> firend3list = firendlist.subList(0, 3);
                searchCircleAdapter.setData(firend3list);
                rl_friend_bottom.setVisibility(View.VISIBLE);
            } else {
                searchCircleAdapter.setData(firendlist);
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
                List<EmoStore> emoStore3 = storelist.subList(0, 3);
                storeAdapter.setData(emoStore3);
                rl_face_bottom.setVisibility(View.VISIBLE);
            } else {
                storeAdapter.setData(storelist);
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
