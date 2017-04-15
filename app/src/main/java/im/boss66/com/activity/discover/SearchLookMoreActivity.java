package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.EmojiGroupDetailsActivity;
import im.boss66.com.adapter.EmoStoreAdapter;
import im.boss66.com.adapter.UserAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseEmoStore;
import im.boss66.com.entity.BaseUserInform;
import im.boss66.com.entity.EmoStore;
import im.boss66.com.entity.LookMoreCircleEntity;
import im.boss66.com.entity.SearchCofriendEntity;
import im.boss66.com.entity.UserInform;

/**
 * 全网搜索-查看更多
 */
public class SearchLookMoreActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title;
    private ListView lv_content;
    private String url, access_token;
    private boolean isCircle;
    private EmoStoreAdapter storeAdapter;
    private SearchCircleAdapter searchCircleAdapter;
    private UserAdapter userAdapter;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_more);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_content = (ListView) findViewById(R.id.lv_content);
        tv_back.setOnClickListener(this);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type == 1) {
                    SearchCofriendEntity item = (SearchCofriendEntity) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(context, PhotoAlbumDetailActivity.class);
                    try {
                        int feed = Integer.parseInt(item.getFeed_id());
                        intent.putExtra("feedId", feed);
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                } else if (type == 2) {
                    EmoStore store = (EmoStore) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(context, EmojiGroupDetailsActivity.class);
                    intent.putExtra("packid", store.getGroup_id());
                    startActivity(intent);
                } else if (type == 3) {
                    UserInform inform = (UserInform) adapterView.getItemAtPosition(i);
                    if (inform != null) {
                        Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                        intent.putExtra("classType", "QureAccountActivity");
                        intent.putExtra("userid", inform.getUser_id());
                        startActivity(intent);
                    }
                }
            }
        });
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        Intent intet = getIntent();
        if (intet != null) {
            Bundle bundle = intet.getExtras();
            if (bundle != null) {
                type = bundle.getInt("type");
                switch (type) {
                    case 1:
                        searchCircleAdapter = new SearchCircleAdapter(this);
                        lv_content.setAdapter(searchCircleAdapter);
                        tv_title.setText("查看更多朋友圈");
                        break;
                    case 2:
                        storeAdapter = new EmoStoreAdapter(this);
                        lv_content.setAdapter(storeAdapter);
                        tv_title.setText("查看更多表情");
                        break;
                    case 3:
                        userAdapter = new UserAdapter(this);
                        lv_content.setAdapter(userAdapter);
                        tv_title.setText("查看更多联系人");
                        break;
                }
                url = bundle.getString("url");
                if (!TextUtils.isEmpty(url)) {
                    getSeverData();
                }
            }
        }
    }

    private void getSeverData() {
        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    if (type == 1) {
                        LookMoreCircleEntity data = JSON.parseObject(result, LookMoreCircleEntity.class);
                        if (data != null) {
                            if (data.getStatus() == 401) {
                                Intent intent = new Intent();
                                intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                App.getInstance().sendBroadcast(intent);
                            } else {
                                if (data.getCode() == 1) {
                                    List<SearchCofriendEntity> list = data.getResult();
                                    if (list != null && list.size() > 0) {
                                        showFriendData(list);
                                    }
                                } else {
                                    showToast(data.getMessage(), false);
                                }
                            }
                        } else {
                            showToast("没有更多数据了", false);
                        }
                    } else if (type == 2) {
                        BaseEmoStore store = JSON.parseObject(result, BaseEmoStore.class);
                        if (store != null) {
                            List<EmoStore> storeList = store.getResult();
                            if (storeList != null && storeList.size() > 0) {
                                showFace(storeList);
                            }
                        }
                    } else if (type == 3) {
                        BaseUserInform user = JSON.parseObject(result, BaseUserInform.class);
                        if (user != null) {
                            List<UserInform> userList = user.getResult();
                            if (userList != null && userList.size() > 0) {
                                userAdapter.setData(userList);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast(s, false);
                }
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }

    private void showFace(List<EmoStore> storeList) {
        storeAdapter.setData(storeList);
    }

    private void showFriendData(List<SearchCofriendEntity> list) {
        searchCircleAdapter.setData(list);
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
