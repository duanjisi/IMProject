package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.ChooseFuwaHideAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.FuwaDetailEntity;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;

/**
 * 选择福娃
 */
public class ChooseFuwaHideActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back;
    private RecyclerView rv_content;
    private ChooseFuwaHideAdapter adapter;
    private List<FuwaEntity.Data> fuwaList;
    private Dialog dialog, recommondDialog, detailDialog;
    private TextView tv_dia_name, tv_serial_dia_number;
    private int selectPos;
    private String selectId;
    private static File imgFile;
    private String userId, geohash, address;
    private EditText et_recommond;
    private FuwaEntity.Data fuwaItem;
    private String recommond = "暂无活动介绍";
    private ImageView iv_left, iv_right;
    private TextView tv_selected_tag;
    private FuwaDetailEntity fuwaDetailEntity;
    private List<FuwaDetailEntity.DataBean> fuwaDetailEntitieList;
    private int selectDetailPos = 0, lastSelectPos = -1;
    private ViewPager vp_fuwa;
    private int allDetailSize;
    private List<View> views = new ArrayList<>();
    private List<Integer> hasLoad = new ArrayList<>();
    private TextView tv_number;
    private TextView tv_fuwa_num;
    private TextView tv_from;
    private TextView tv_catch;
    private final int RECORD_VIDEO = 3;//视频
    private PermissionListener permissionListener;
    private TextView tv_time;
    private ImageView iv_time, iv_video, iv_video_img;
    private FrameLayout fl_video_dialog_img;
    private PopupWindow popWindow;
    private RelativeLayout rl_time;
    private File videoFile;
    private String[] popTime;
    private int validtime = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fuwa_hide);
        initView();
    }

    private void initView() {
        fuwaDetailEntitieList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                address = bundle.getString("address");
                geohash = bundle.getString("geohash");
            }
        }
        tv_back = (TextView) findViewById(R.id.tv_back);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        AccountEntity sAccount = App.getInstance().getAccount();
        userId = sAccount.getUser_id();
        tv_back.setOnClickListener(this);
        fuwaList = new ArrayList<>();
        adapter = new ChooseFuwaHideAdapter(this, fuwaList);
        rv_content.setAdapter(adapter);
        rv_content.addOnItemTouchListener(new ChooseFuwaHideAdapter.RecyclerItemClickListener(this,
                new ChooseFuwaHideAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (selectPos >= 0 && selectPos < fuwaList.size()) {
                            FuwaEntity.Data lastitem = fuwaList.get(selectPos);
                            lastitem.setSel(false);
                            adapter.notifyItemChanged(selectPos);
                        }
                        selectPos = position;
                        fuwaItem = fuwaList.get(position);
                        fuwaItem.setSel(true);
                        adapter.notifyItemChanged(selectPos);
                        List<String> idList = fuwaItem.getIdList();
                        if (idList != null) {
                            allDetailSize = idList.size();
                            for (int i = 0; i < idList.size(); i++) {
                                FuwaDetailEntity.DataBean fuwaDetailEntity = new FuwaDetailEntity.DataBean();
                                fuwaDetailEntity.setFuwaId(fuwaItem.getId());
                                fuwaDetailEntitieList.add(fuwaDetailEntity);
                            }
                            selectDetailPos = 0;
                            selectId = idList.get(0);
                            showFuwaDetailDialog();
                        }
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                    }
                }));
        getSeverData();
    }

    private void getSeverData() {
        String url = HttpUrl.QUERY_MY_FUWA + userId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.configCurrentHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    FuwaEntity entity = JSON.parseObject(res, FuwaEntity.class);
                    List<FuwaEntity.Data> data = entity.getData();
                    if (data != null && data.size() > 0) {
                        showData(data);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
            }
        });
    }

    private void showData(List<FuwaEntity.Data> data) {
        for (FuwaEntity.Data bill : data) {
            boolean state = false;
            for (FuwaEntity.Data bills : fuwaList) {
                if (bills.getId().equals(bill.getId())) {
                    List<String> list = bills.getIdList();
                    String id = bill.getGid();
                    if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                        list.add(id);
                        bills.setIdList(list);
                    }
                    int num = bills.getNum();
                    num += bill.getNum();
                    bills.setNum(num);
                    state = true;
                }
            }
            if (!state) {
                List<String> list = bill.getIdList();
                String id = bill.getGid();
                if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                    list.add(id);
                    bill.setIdList(list);
                }
                fuwaList.add(bill);
            }
        }
        adapter.onDataChange(fuwaList);
    }

    private void showDialog(FuwaEntity.Data item) {
        if (recommondDialog != null && recommondDialog.isShowing()) {
            recommondDialog.dismiss();
        }
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_choose_fuwa, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);
            ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
            tv_dia_name = (TextView) view.findViewById(R.id.tv_name);
            tv_serial_dia_number = (TextView) view.findViewById(R.id.tv_serial_number);
            Button bt_catch = (Button) view.findViewById(R.id.bt_catch);
            bt_catch.setOnClickListener(this);
            iv_close.setOnClickListener(this);

            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (sceenW * 0.8);
            lp.height = (int) (sceenH * 0.6);
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
        }
        if (item != null) {
            String num = item.getId();
            tv_dia_name.setText(num + "号福娃");
            tv_serial_dia_number.setText("" + num);
            List<String> idList = item.getIdList();
            if (idList != null && idList.size() > 0) {
                selectId = idList.get(0);
            }
        }
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_catch:
                if (detailDialog != null && detailDialog.isShowing()) {
                    detailDialog.dismiss();
                }
                showRecommondDialog();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;
            case R.id.bt_sure:
                if (recommondDialog != null && recommondDialog.isShowing()) {
                    recommondDialog.dismiss();
                }
                recommond = et_recommond.getText().toString().trim();
                et_recommond.setText("");
                hideFuwaServer();
                break;
            case R.id.tv_jump_over:
                if (recommondDialog != null && recommondDialog.isShowing()) {
                    recommondDialog.dismiss();
                }
                recommond = "暂无活动介绍";
                hideFuwaServer();
                break;
            case R.id.rl_time:
                showTimeChoosePop();
                break;
            case R.id.iv_video:
                getPermission();
                break;
            case R.id.tv_time_1:
                validtime = 1;
                setPopTime(validtime);
                break;
            case R.id.tv_time_2:
                validtime = 2;
                setPopTime(validtime);
                break;
            case R.id.tv_time_3:
                validtime = 3;
                setPopTime(validtime);
                break;
            case R.id.tv_time_4:
                validtime = 4;
                setPopTime(validtime);
                break;
        }
    }

    public static void getImgFile(File file) {
        imgFile = file;
    }

    private void hideFuwaServer() {
        showLoadingDialog();
        if (TextUtils.isEmpty(recommond)) {
            recommond = "暂无活动介绍";
        }
        String url = HttpUrl.HIDE_MY_FUWA + userId + "&fuwagid=" +
                selectId + "&pos=" + address + "&geohash=" + geohash
                + "&detail=" + recommond + "&validtime=" + validtime;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", imgFile);
        if (videoFile != null) {
            params.addBodyParameter("video", videoFile);
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    BaseResult data = BaseResult.parse(res);
                    if (data != null) {
                        int code = data.getCode();
                        if (code == 0) {
                            EventBus.getDefault().post(selectId);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                } else {
                    showToast("藏福娃失败TAT，请重试", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(s, false);
            }
        });
    }

    private void showRecommondDialog() {
        if (recommondDialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_hide_fuwa_recommend, null);
            int sceenH = UIUtils.getScreenHeight(this);

            rl_time = (RelativeLayout) view.findViewById(R.id.rl_time);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            iv_time = (ImageView) view.findViewById(R.id.iv_time);
            iv_video = (ImageView) view.findViewById(R.id.iv_video);
            iv_video_img = (ImageView) view.findViewById(R.id.iv_video_img);
            fl_video_dialog_img = (FrameLayout) view.findViewById(R.id.fl_video_dialog_img);

            rl_time.setOnClickListener(this);
            iv_video.setOnClickListener(this);

            et_recommond = (EditText) view.findViewById(R.id.et_recommond);
            Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
            TextView tv_jump_over = (TextView) view.findViewById(R.id.tv_jump_over);
            bt_sure.setOnClickListener(this);
            tv_jump_over.setOnClickListener(this);
            LinearLayout.LayoutParams etParam = (LinearLayout.LayoutParams) et_recommond.getLayoutParams();
            etParam.height = sceenH / 7;
            et_recommond.setLayoutParams(etParam);

            recommondDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            recommondDialog.setContentView(view);
            recommondDialog.setCanceledOnTouchOutside(true);
        }
        recommondDialog.show();
    }

    private void showFuwaDetailDialog() {
        if (detailDialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_fuwa_select_tag, null);
            tv_selected_tag = (TextView) view.findViewById(R.id.tv_selected_tag);
            vp_fuwa = (ViewPager) view.findViewById(R.id.vp_fuwa);
            Button bt_catch = (Button) view.findViewById(R.id.bt_catch);
            bt_catch.setOnClickListener(this);
            int sceenW = UIUtils.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vp_fuwa.getLayoutParams();
            params.width = sceenW / 5 * 3;
            params.height = sceenW / 5 * 2;
            vp_fuwa.setLayoutParams(params);
            iv_left = (ImageView) view.findViewById(R.id.iv_left);
            iv_right = (ImageView) view.findViewById(R.id.iv_right);
            detailDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            tv_selected_tag.setText("1/" + allDetailSize);
            if (allDetailSize > 1) {
                iv_left.setVisibility(View.INVISIBLE);
                iv_right.setVisibility(View.VISIBLE);
            } else {
                iv_left.setVisibility(View.INVISIBLE);
                iv_right.setVisibility(View.INVISIBLE);
            }
            detailDialog.setContentView(view);
            detailDialog.setCanceledOnTouchOutside(true);
            vp_fuwa.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        iv_left.setVisibility(View.INVISIBLE);
                    } else {
                        iv_left.setVisibility(View.VISIBLE);
                    }
                    if (position == allDetailSize - 1) {
                        iv_right.setVisibility(View.INVISIBLE);
                    } else {
                        iv_right.setVisibility(View.VISIBLE);
                    }
                    if (allDetailSize <= 1) {
                        iv_right.setVisibility(View.INVISIBLE);
                        iv_left.setVisibility(View.INVISIBLE);
                    }
                    selectDetailPos = position;
                    tv_selected_tag.setText((selectDetailPos + 1) + "/" + allDetailSize);
                    selectId = fuwaItem.getIdList().get(position);
                    if (!hasLoad.contains(position)) {
                        hasLoad.add(position);
                        getFuwaDetail(selectId);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            iv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectDetailPos > 0) {
                        vp_fuwa.setCurrentItem(selectDetailPos - 1);
                    }
                }
            });
            iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectDetailPos < allDetailSize - 1) {
                        vp_fuwa.setCurrentItem(selectDetailPos + 1);
                    }

                }
            });
        }
        if (lastSelectPos != selectPos) {
            hasLoad.clear();
            views.clear();   //清空views
            lastSelectPos = selectPos;
            allDetailSize = fuwaItem.getIdList().size();
            tv_selected_tag.setText("1/" + allDetailSize);
            for (int i = 0; i < allDetailSize; i++) {
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_fuwa_detail, null);
                views.add(view1);
            }
            hasLoad.add(0);
            getFuwaDetail(fuwaItem.getIdList().get(0));
            vp_fuwa.removeAllViews();
            vp_fuwa.setAdapter(new ViewAdapter(views));
        }
        detailDialog.show();
    }

    private void getFuwaDetail(String fuwa_gid) {
        Log.i("getFuwaDetail", "fuwa_gid:" + fuwa_gid);
        String url = HttpUrl.FUWA_DETAIL + fuwa_gid;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    fuwaDetailEntity = JSON.parseObject(res, FuwaDetailEntity.class);
                    handler.sendEmptyMessage(1);
                } else {
                    showToast(fuwaDetailEntity.getMessage(), false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    private void setContent() {
        View view = views.get(selectDetailPos);    //通过vp位置拿到view
        tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_fuwa_num = (TextView) view.findViewById(R.id.tv_fuwa_num);
        tv_from = (TextView) view.findViewById(R.id.tv_from);
        tv_catch = (TextView) view.findViewById(R.id.tv_catch);
        String fuwa_id = fuwaItem.getId();
        if (!TextUtils.isEmpty(fuwa_id)){
            tv_number.setText(fuwa_id);
        }
        tv_fuwa_num.setText(fuwa_id + "号福娃");
        String creator = fuwaDetailEntity.getData().getCreator();
        String pos = fuwaDetailEntity.getData().getPos();
        Log.i("getFuwaDetail", "Creator:" + creator + "Pos" + pos);
        tv_from.setText("来源于:" + creator);
        tv_catch.setText("捕获于：" + pos);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setContent();
                    break;
            }
        }
    };

    class ViewAdapter extends PagerAdapter {
        private List<View> viewList;//数据源

        public ViewAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position < viewList.size()) {
                container.removeView(viewList.get(position));
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < viewList.size()) {
                container.addView(viewList.get(position));//千万别忘记添加到container
                return viewList.get(position);
            }
            return null;
        }
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                startActivityForResult(mIntent, RECORD_VIDEO);
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(ChooseFuwaHideActivity.this, getString(R.string.giving_camera2_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_CAMERA //相机权限
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    private void showTimeChoosePop() {
        if (popWindow == null) {
            popTime = new String[]{"72小时", "一个月", "一年", "三年"};
            View popupView = getLayoutInflater().inflate(R.layout.popwindow_hide_fuwa_time, null);
            TextView tv_time_1 = (TextView) popupView.findViewById(R.id.tv_time_1);
            TextView tv_time_2 = (TextView) popupView.findViewById(R.id.tv_time_2);
            TextView tv_time_3 = (TextView) popupView.findViewById(R.id.tv_time_3);
            TextView tv_time_4 = (TextView) popupView.findViewById(R.id.tv_time_4);
            tv_time_1.setOnClickListener(this);
            tv_time_2.setOnClickListener(this);
            tv_time_3.setOnClickListener(this);
            tv_time_4.setOnClickListener(this);
            popWindow = new PopupWindow(popupView, rl_time.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, true);
            popWindow.setAnimationStyle(R.style.hide_fuwa_pop_anim);
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
        }
        int xOff = UIUtils.getScreenWidth(this) / 2 - rl_time.getWidth() / 3;
        int xOffDp = UIUtils.px2dip(this, xOff);
        popWindow.showAsDropDown(rl_time, 0, 10);
        //popWindow.showAsDropDown(rl_time);
    }

    private void setPopTime(int tag) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
        if (tv_time != null && popTime != null) {
            tv_time.setText(popTime[tag - 1]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {
            // 录制视频完成
            try {
                AssetFileDescriptor videoAsset = getContentResolver()
                        .openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                videoFile = new File(
                        Environment.getExternalStorageDirectory(),
                        "fuwavideo.mp4");
                FileOutputStream fos = new FileOutputStream(videoFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
                if (videoFile != null) {
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(videoFile.getAbsolutePath());
                    Bitmap videoBitmap = media.getFrameAtTime();
                    if (videoBitmap != null && iv_video_img != null) {
                        fl_video_dialog_img.setVisibility(View.VISIBLE);
                        iv_video.setVisibility(View.GONE);
                        iv_video_img.setImageBitmap(videoBitmap);
//                        Glide.with(this).load(videoBitmap).into(iv_video_img);
                    }
                    media.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoFile != null && videoFile.exists()) {
            videoFile.delete();
            videoFile = null;
        }
    }
}
