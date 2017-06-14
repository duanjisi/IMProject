package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.discover.FriendCircleActivity;
import im.boss66.com.activity.discover.PeopleNearbyActivity;
import im.boss66.com.activity.discover.SharkItOffActivity;
import im.boss66.com.activity.discover.WebViewActivity;
import im.boss66.com.activity.treasure.MainTreasureActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.CircleNewest;
import im.boss66.com.entity.CircleNewestEntity;
import im.boss66.com.http.HttpUrl;

/**
 * 发现的主界面
 */
public class DiscoverFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_friends, rl_richScan, rl_shark_it_off, rl_people_nearby, rl_shopping, rl_game;
    private TextView tv_friends_no_read, tv_nearby_no_read;
    private String access_token;
    private boolean isLoad = false;
    private String newCount, newIcon;
    private ImageView iv_avatar;
    private long curTime;
//    private ImageLoader imageLoader;
//    private AccountEntity account;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        //account = App.getInstance().getAccount();
        //imageLoader = ImageLoaderUtils.createImageLoader(getActivity());
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);

        rl_friends = (RelativeLayout) view.findViewById(R.id.rl_friends);
        rl_richScan = (RelativeLayout) view.findViewById(R.id.rl_richScan);
        rl_shark_it_off = (RelativeLayout) view.findViewById(R.id.rl_shark_it_off);
        rl_people_nearby = (RelativeLayout) view.findViewById(R.id.rl_people_nearby);
        rl_shopping = (RelativeLayout) view.findViewById(R.id.rl_shopping);
        rl_game = (RelativeLayout) view.findViewById(R.id.rl_game);
        tv_friends_no_read = (TextView) view.findViewById(R.id.tv_friends_no_read);
        tv_nearby_no_read = (TextView) view.findViewById(R.id.tv_nearby_no_read);
        rl_friends.setOnClickListener(this);
        rl_richScan.setOnClickListener(this);
        rl_shark_it_off.setOnClickListener(this);
        rl_people_nearby.setOnClickListener(this);
        rl_shopping.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
//        imageLoader.displayImage(account.getAvatar(), iv_avatar, ImageLoaderUtils.getDisplayImageOptions());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_friends:
                Bundle bundle1 = new Bundle();
                if (!TextUtils.isEmpty(newCount)) {
                    bundle1.putString("newCount", newCount);
                }
                if (!TextUtils.isEmpty(newIcon)) {
                    bundle1.putString("newIcon", newIcon);
                }
                openActivity(FriendCircleActivity.class, bundle1);
                newCount = "";
                newIcon = "";
                if (tv_friends_no_read.getVisibility() == View.VISIBLE) {
                    tv_friends_no_read.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_richScan:
                Bundle bundle = new Bundle();
                bundle.putString("classType", "DiscoverFragment");
                openActivity(CaptureActivity.class, bundle);
                break;
            case R.id.rl_shark_it_off:
                openActivity(SharkItOffActivity.class, null);
                break;
            case R.id.rl_people_nearby:
                openActivity(PeopleNearbyActivity.class, null);
                break;
            case R.id.rl_shopping:
                Bundle bundle2 = new Bundle();
                bundle2.putString("url", "http://m.66boss.com/");
                bundle2.putBoolean("isHasTitle", false);
                openActivity(WebViewActivity.class, bundle2);
                break;
            case R.id.rl_game:
                openActivity(MainTreasureActivity.class, null);
                break;
            case R.id.iv_avatar:
                EventBus.getDefault().post(new ActionEntity(Constants.Action.MENU_CAHNGE_CURRENT_TAB));
                break;
        }
    }
//    public void openActivity(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(getActivity(), clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivity(intent);
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoad) {
            getServerNew();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !isLoad) {
            getServerNew();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        long nowTime = System.currentTimeMillis();
        if (curTime == 0 || (nowTime - curTime) > 10000 && !isLoad) {
            curTime = nowTime;
            getServerNew();
        }
    }

    private void getServerNew() {
//        if (!NetworkUtil.networkAvailable(getActivity())) {
//            showToast("断网啦，请检查网络", false);
//            return;
//        }
        isLoad = true;
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        String url = HttpUrl.GET_CIRCLE_NEWEST_MSG;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                isLoad = false;
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    BaseResult res = BaseResult.parse(result);
                    if (res != null) {
                        if (res.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            if (res.getCode() == 1) {
                                try {
                                    CircleNewestEntity data = JSON.parseObject(result, CircleNewestEntity.class);
                                    if (data != null) {
                                        showData(data);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                isLoad = false;
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                }
            }
        });
    }

    private void showData(CircleNewestEntity data) {
        CircleNewest result = data.getResult();
        if (result != null) {
            newCount = result.getCount();
            CircleNewest.CircleNewestUser frist_user = result.getFrist_user();
            if (frist_user != null) {
                newIcon = frist_user.getAvatar();
            }
            if (tv_friends_no_read != null) {
                if (!TextUtils.isEmpty(newCount) && !"0".equals(newCount)) {
                    tv_friends_no_read.setVisibility(View.VISIBLE);
                    tv_friends_no_read.setText(newCount);
                } else {
                    tv_friends_no_read.setVisibility(View.GONE);
                }
            }
        }
    }

}
