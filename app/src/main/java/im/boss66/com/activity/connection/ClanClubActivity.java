package im.boss66.com.activity.connection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.adapter.CommunityListAdapter;
import im.boss66.com.entity.ClanCofcEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by liw on 2017/4/14.
 */
public class ClanClubActivity extends ABaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private boolean isClan;  //是否是宗亲
    private String name;    //标题
    private String id;     //宗亲或者商会id

    private LRecyclerView rcv_news;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private CommunityListAdapter adapter;
    private ImageView iv_bg;
    private String url;
    private ClanCofcEntity clanCofcEntity;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ClanCofcEntity.ResultBean result = clanCofcEntity.getResult();
                    if (result != null) {
                        tv_count.setVisibility(View.VISIBLE);
                        tv_follow.setVisibility(View.VISIBLE);
                        count = result.getCount();
                        tv_count.setText(count + "人");
                        is_follow = result.getIs_follow();

                        String banner = result.getBanner();
                        if(banner.length()>0){
                            Glide.with(context).load(banner).into(iv_bg);
                        }else {
                            Glide.with(context).load(R.drawable.bg_top).into(iv_bg);
                        }

                        if (is_follow == 1) { //关注了
                            tv_follow.setText("已关注");
                            tv_follow.setBackgroundResource(R.drawable.shape_isfollow);
                            tv_follow.setTextColor(Color.GRAY);
                            //TODO 让人脉首页刷新？
                        } else {
                            tv_follow.setText("关注");
                            tv_follow.setBackgroundResource(R.drawable.shape_follow);
                            tv_follow.setTextColor(Color.WHITE);
                            //TODO 让人脉首页刷新？
                        }
                    }
                    break;
                case 2: //关注成功
                    is_follow = 1;
                    tv_follow.setText("已关注");
                    tv_follow.setBackgroundResource(R.drawable.shape_isfollow);
                    tv_follow.setTextColor(Color.GRAY);
                    count = Integer.parseInt(count) + 1+"";
                    tv_count.setText(count+"人");
                    break;

                case 3: //取消关注
                    is_follow = 0;
                    tv_follow.setText("关注");
                    tv_follow.setBackgroundResource(R.drawable.shape_follow);
                    tv_follow.setTextColor(Color.WHITE);
                    count = Integer.parseInt(count) -1+"";
                    tv_count.setText(count+"人");
                    break;
            }
        }
    };
    private TextView tv_follow;
    private TextView tv_count;
    private int is_follow;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clan_club);
        initArgument();
        initViews();
        initData();
    }

    private void initData() {
        if (isClan) {
            url = HttpUrl.CLAN_INFO;
        } else {
            url = HttpUrl.COFC_INFO;
        }
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        if (isClan) {
            url = url + "?clan_id=" + id;
        } else {
            url = url + "?cofc_id=" + id;
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    clanCofcEntity = JSON.parseObject(result, ClanCofcEntity.class);
                    handler.obtainMessage(1).sendToTarget();
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
    }

    private void initArgument() {
        Intent intent = getIntent();
        if (intent != null) {
            isClan = intent.getBooleanExtra("isClan", false);
            name = intent.getStringExtra("name");
            id = intent.getStringExtra("id");

        }
    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);
        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText(name);

        View header = LayoutInflater.from(this).inflate(R.layout.item_people_news_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        //头部view设置，加入到adapter中
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context) / 3 * 2;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

        TextView tv_introduce = (TextView) header.findViewById(R.id.tv_introduce);
        TextView tv_famous_person = (TextView) header.findViewById(R.id.tv_famous_person);
        TextView tv_club = (TextView) header.findViewById(R.id.tv_club);
        TextView tv_news = (TextView) header.findViewById(R.id.tv_news);
        tv_follow = (TextView) header.findViewById(R.id.tv_follow);
        tv_follow.setOnClickListener(this);
        tv_count = (TextView) header.findViewById(R.id.tv_count);
        tv_club.setText("动态");
        if (isClan) {
            tv_news.setText("创建宗亲");
        } else {
            tv_news.setText("创建商会");
        }
        iv_bg.setOnClickListener(this);
        tv_introduce.setOnClickListener(this);
        tv_famous_person.setOnClickListener(this);
        tv_club.setOnClickListener(this);
        tv_news.setOnClickListener(this);


        rcv_news = (LRecyclerView) findViewById(R.id.rcv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcv_news.setLayoutManager(layoutManager);

        adapter = new CommunityListAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        rcv_news.setAdapter(mLRecyclerViewAdapter);


    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("更换相册封面", ActionSheet.SheetItemColor.Black, this);

        actionSheet.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.iv_headright_view:
                //右上角 ...

                break;
            case R.id.iv_bg:
                showActionSheet();

                break;
            case R.id.tv_introduce:  //简介
                if (isClan) {
                    Intent intent = new Intent(this, ClanCofcDetailActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("isClan", true);
                    intent.putExtra("name", name);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, ClanCofcDetailActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("isClan", false);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
                break;
            case R.id.tv_famous_person: //名人
                break;
            case R.id.tv_club:  //动态
                break;
            case R.id.tv_news:  //创建宗亲
                Intent intent = new Intent(this, ApplyCreateActivity.class);
                if (isClan) {
                    intent.putExtra("from", 1);
                } else {
                    intent.putExtra("from", 2);
                }
                startActivity(intent);
                break;
            case R.id.tv_follow:

                if (is_follow == 0) { //未关注，点击关注

                    if (isClan) {  //宗亲
                        followClan();

                    } else {
                        followCofc();
                    }


                } else { //已经关注，点击取消关注
                    if (isClan) {

                        cancelFollowClan();
                    } else {

                        cancelFollowCofc();

                    }

                }
                break;


        }
    }

    private void cancelFollowCofc() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.CANCEL_FOLLOW_COFC + "?cofc_id=" + id;

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            handler.obtainMessage(3).sendToTarget();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });

    }

    private void cancelFollowClan() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.CANCEL_FOLLOW_CLAN + "?clan_id=" + id;

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            handler.obtainMessage(3).sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
    }

    private void followCofc() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.FOLLOW_COFC + "?cofc_id=" + id;

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            showToast("关注成功",false);
                            handler.obtainMessage(2).sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
    }

    private void followClan() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.FOLLOW_CLAN + "?clan_id=" + id;

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            showToast("关注成功",false);
                            handler.obtainMessage(2).sendToTarget();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
    }

    @Override
    public void onClick(int which) {
        if (which == 1) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromClanClub", true);
            bundle.putBoolean("isClan", isClan);
            bundle.putString("id", id);
            openActvityForResult(ReplaceAlbumCoverActivity.class, 1, bundle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (data != null) {
                String imgurl = data.getStringExtra("imgurl");
                Glide.with(this).load(imgurl).into(iv_bg);

            }
        }
    }
}
