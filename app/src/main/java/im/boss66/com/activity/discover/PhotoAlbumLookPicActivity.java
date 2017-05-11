package im.boss66.com.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.CircleCommentListEntity;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleCommentEntity;
import im.boss66.com.entity.FriendCirclePraiseEntity;
import im.boss66.com.entity.PhotoAlbumDetailEntity;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.DoPraiseRequest;
import im.boss66.com.widget.photoview.PhotoView;
import im.boss66.com.widget.photoview.PhotoViewAttacher;

/**
 * 单个相册详情
 */
public class PhotoAlbumLookPicActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = PhotoAlbumLookPicActivity.class.getSimpleName();

    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private boolean isAnimation1 = false;
    private boolean isAnimation2 = false;

    private ViewPager viewPager;
    private RelativeLayout rl_title;
    private TextView tv_back, tv_title, tv_content, tv_praise, tv_commit, tv_commit_num, tv_praise_num;
    private ImageView iv_set;
    private LinearLayout ll_bottom;
    private int SHOW_HIDE_ANIMATE = 102;
    private String feedId, access_token, feed_uid;
    private FriendCircle friendCircle;
    private ImageAdapter mAdapter;
    private PhotoView pv_view;
    private int sceenW;
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    private int startPos = 0, isPrase, praseNum;
    private List<FriendCirclePraiseEntity> praise_list;
    private String userId, userName;
    private PLVideoTextureView mVideoView;
    private RelativeLayout rl_video;
    //    private ImageView iv_bg;
//    private ProgressBar pb_video;
    //private MediaController mediaco;
    private String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album_look_pic);
        initView();
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(this);
//        pb_video = (ProgressBar) findViewById(R.id.pb_video);
//        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        mVideoView = (PLVideoTextureView) findViewById(R.id.videoView);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_praise = (TextView) findViewById(R.id.tv_praise);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_commit_num = (TextView) findViewById(R.id.tv_commit_num);
        tv_praise_num = (TextView) findViewById(R.id.tv_praise_num);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        viewPager = (ViewPager) findViewById(R.id.pager);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tv_back.setOnClickListener(this);
        tv_commit_num.setOnClickListener(this);
        tv_praise.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        tv_praise_num.setOnClickListener(this);
        tv_back.setText("相册");

        if (mShowAction == null) {
            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
            mShowAction.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimation1 = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rl_title.setVisibility(View.VISIBLE);
                    isAnimation1 = false;

                }
            });
        }
        if (mHiddenAction == null) {
            mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            mHiddenAction.setDuration(500);
            mHiddenAction.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimation2 = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ll_bottom.setVisibility(View.VISIBLE);
                    isAnimation2 = false;
                }
            });
        }
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        userId = sAccount.getUser_id();
        userName = sAccount.getUser_name();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                feedId = String.valueOf(bundle.getInt("feedId"));
                getAlbumDetail();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_praise:
                doPraise();
                break;
            case R.id.tv_commit:
                Bundle bundle = new Bundle();
                bundle.putString("feed_uid", feed_uid);
                bundle.putString("feedId", feedId);
                openActvityForResult(CommentActivity.class, 101, bundle);
                break;
            case R.id.tv_praise_num:
            case R.id.tv_commit_num:
                Bundle bundle1 = new Bundle();
                bundle1.putString("feedId", feedId);
                bundle1.putString("classType", "PhotoAlbumLookPicActivity");
                bundle1.putSerializable("data", friendCircle);
                openActvityForResult(PhotoAlbumDetailActivity.class, 401, bundle1);
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SHOW_HIDE_ANIMATE) {
                if (rl_title.getVisibility() == View.VISIBLE) {
                    rl_title.clearAnimation();
                    rl_title.setVisibility(View.GONE);
                } else if (rl_title.getVisibility() == View.GONE
                        && !isAnimation1) {
                    rl_title.startAnimation(mShowAction);
                }
                if (ll_bottom.getVisibility() == View.VISIBLE) {
                    ll_bottom.clearAnimation();
                    ll_bottom.setVisibility(View.GONE);
                } else if (!isAnimation2) {
                    ll_bottom.startAnimation(mHiddenAction);
                }
            }
        }
    };

    //获取某个相册详情
    private void getAlbumDetail() {
        showLoadingDialog();
        String main = HttpUrl.GET_FRIEND_CIRCLE_DETAIL + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    PhotoAlbumDetailEntity data = JSON.parseObject(result, PhotoAlbumDetailEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            String msg = data.getMessage();
                            int code = data.getCode();
                            if (code == 1) {
                                friendCircle = data.getResult();
                                if (friendCircle != null) {
                                    int feedType = friendCircle.getFeed_type();
                                    showSigleTxData(friendCircle, feedType);
                                }
                            } else {
                                showToast(msg, false);
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
                    showToast("删除失败", false);
                }
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }

    private void showSigleTxData(FriendCircle friendCircle, int feedType) {
        feed_uid = friendCircle.getFeed_uid();
        int prase = friendCircle.getIs_praise();
        if (prase == 0) {
            isPrase = 1;
            tv_praise.setText("赞");
        } else {
            isPrase = 0;
            tv_praise.setText("取消");
        }
        String addTime = friendCircle.getAdd_time();
        if (!TextUtils.isEmpty(addTime)) {
            try {
                long time = Long.parseLong(addTime);
                String time_ = TimeUtil.getCircleTime(time);
                tv_title.setText("" + time_);
            } catch (Exception e) {
                tv_title.setText("" + addTime);
            }
        }
        String content = friendCircle.getContent();
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }
        praise_list = friendCircle.getPraise_list();
        if (praise_list != null && praise_list.size() > 0) {
            praseNum = praise_list.size();
            tv_praise_num.setText("" + praseNum);
        }
        List<FriendCircleCommentEntity> comment_list = friendCircle.getComment_list();
        if (comment_list != null && comment_list.size() > 0) {
            int com_size = comment_list.size();
            tv_commit_num.setText("" + com_size);
        }
        List<PhotoInfo> files = friendCircle.getFiles();
        if (files != null && files.size() > 0) {
            if (feedType == 1) {
                ArrayList<String> photoUrls = new ArrayList<String>();
                for (PhotoInfo photoInfo : files) {
                    photoUrls.add(photoInfo.file_url);
                }
                mAdapter = new ImageAdapter(this);
                mAdapter.setDatas(photoUrls);
                viewPager.setAdapter(mAdapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for (int i = 0; i < guideViewList.size(); i++) {
                            guideViewList.get(i).setSelected(i == position ? true : false);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                viewPager.setCurrentItem(startPos);

                //addGuideView(guideGroup, startPos, photoUrls);
            } else if (feedType == 2) {
                rl_video.setVisibility(View.VISIBLE);
                mVideoPath = files.get(0).file_url;
//                mediaco = new MediaController(this);
//                mediaco.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(mVideoPath)) {
                    mVideoView.setDisplayOrientation(270);
                    mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
                    mVideoView.setOnCompletionListener(mOnCompletionListener);
                    mVideoView.setOnErrorListener(mOnErrorListener);
                    setOptions(0);
                    mVideoView.setVideoPath(mVideoPath);
                    View loadingView = findViewById(R.id.loading);
                    mVideoView.setBufferingIndicator(loadingView);
                    mVideoView.setScreenOnWhilePlaying(true);
                    //让VideiView获取焦点
                    mVideoView.start();
                    mVideoView.requestFocus();
                }
//                String imgUrl = files.get(0).file_thumb;
//                if (!TextUtils.isEmpty(imgUrl)) {
//                    Glide.with(this).load(imgUrl).into(iv_bg);
//                }
            }
        }
    }

//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
////        pb_video.setVisibility(View.GONE);
////        iv_bg.setVisibility(View.GONE);
//        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
//                // 获得当前播放时间和当前视频的长度
////                int currentPosition = vv_video.getCurrentPosition();
////                int duration = vv_video.getDuration();
////                int time = ((currentPosition * 100) / duration);
////                // 设置进度条的主要进度，表示当前的播放时间
////                SeekBar seekBar = new SeekBar(PhotoAlbumLookPicActivity.this);
////                seekBar.setProgress(time);
////                // 设置进度条的次要进度，表示视频的缓冲进度
////                seekBar.setSecondaryProgress(percent);
//            }
//        });
//    }

    private class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;
        private ImageView smallImageView = null;

        public void setDatas(List<String> datas) {
            if (datas != null)
                this.datas = datas;
        }

        public ImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if (view != null) {
                pv_view = (PhotoView) view.findViewById(R.id.iv_icon);
                pv_view.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        handler.sendMessageDelayed(
                                handler.obtainMessage(SHOW_HIDE_ANIMATE), 5);
                    }
                });
                pv_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        return false;
                    }
                });
                //预览imageView
                smallImageView = new ImageView(context);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sceenW, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                smallImageView.setLayoutParams(layoutParams);
                smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((LinearLayout) view).addView(smallImageView);

                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((LinearLayout) view).addView(loading);

                final String imgurl = datas.get(position);
                Glide.with(context)
                        .load(imgurl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                        .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                        .error(R.drawable.ic_launcher)
                        .into(new GlideDrawableImageViewTarget(pv_view) {
                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                                loading.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                                loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                loading.setVisibility(View.GONE);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                            }
                        });

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls) {
        if (imgUrls != null && imgUrls.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < imgUrls.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_circle_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_heigh),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    //点or取消赞
    private void doPraise() {
        DoPraiseRequest request = new DoPraiseRequest(TAG, feedId, String.valueOf(isPrase));
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                Log.i("dopraise", pojo);
                if (!TextUtils.isEmpty(pojo)) {
                    try {
                        JSONObject obj = new JSONObject(pojo);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                if (isPrase == 0) {
                                    isPrase = 1;
                                    tv_praise.setText("赞");
                                    if (praseNum > 0) {
                                        praseNum = praseNum - 1;
                                        editPraseList(false);
                                    }
                                } else {
                                    isPrase = 0;
                                    tv_praise.setText("取消");
                                    praseNum = praseNum + 1;
                                    editPraseList(false);
                                }
                                if (praseNum > 0) {
                                    tv_praise_num.setText("" + praseNum);
                                } else {
                                    tv_praise_num.setText("");
                                }
                            } else {
                                showToast(msg, false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

    private void editPraseList(boolean isAdd) {
        if (praise_list != null && friendCircle != null) {
            if (isAdd) {
                FriendCirclePraiseEntity item = new FriendCirclePraiseEntity();
                item.setUser_id(userId);
                item.setUser_name(userName);
                praise_list.add(item);
                friendCircle.setPraise_list(praise_list);
            } else {
                for (int i = 0; i < praise_list.size(); i++) {
                    FriendCirclePraiseEntity item = praise_list.get(i);
                    if (item.getUser_id().equals(userId)) {
                        praise_list.remove(i);
                        friendCircle.setPraise_list(praise_list);
                    }
                }
            }
        }
    }

    //获取评论列表
    private void getServerCommentList() {
        String main = HttpUrl.FRIEND_CIRCLE_GET_COMMENT_LIST + "?feed_id=" + feedId + "&page=0&size=1024";
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    CircleCommentListEntity entity = JSON.parseObject(result, CircleCommentListEntity.class);
                    if (entity != null) {
                        if (entity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            List<FriendCircleCommentEntity> list = entity.getResult();
                            if (list != null) {
                                friendCircle.setComment_list(list);
                                int size = list.size();
                                if (size > 0) {
                                    tv_commit_num.setText("" + size);
                                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            getServerCommentList();
        } else if (requestCode == 401 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void getCommunityDetail() {
        showLoadingDialog();
        String main = HttpUrl.COMMUNITY_GET_DETAIL + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    PhotoAlbumDetailEntity data = JSON.parseObject(result, PhotoAlbumDetailEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            String msg = data.getMessage();
                            int code = data.getCode();
                            if (code == 1) {
                                friendCircle = data.getResult();
                                if (friendCircle != null) {
                                    int feedType = friendCircle.getFeed_type();
                                    showSigleTxData(friendCircle, feedType);
                                }
                            } else {
                                showToast(msg, false);
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
                    showToast("获取失败", false);
                }
            }
        });
    }

    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        //options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_PROBESIZE, 500000000);
        // Some optimization with buffering mechanism when be set to 1
        //options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);
    }

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
//            showToastTips("Play Completed !");
//            finish();
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToast("Invalid URL !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToast("404 resource not found !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToast("Connection refused !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToast("连接超时 !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToast("Empty playlist !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToast("Stream disconnected !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToast("Network IO Error !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToast("Unauthorized Error !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToast("Prepare timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToast("Read frame timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToast("unknown error !", false);
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private void sendReconnectMessage() {
        showToast("正在重连...", true);
        //mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0x01), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 0x01) {
                return;
            }
            if (NetworkUtil.networkAvailable(PhotoAlbumLookPicActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
}
