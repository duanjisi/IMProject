package im.boss66.com.activity.im;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.EmoPicAdapter;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmojiInform;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoInformRequest;
import im.boss66.com.task.EmoDbTask;
import im.boss66.com.task.ZipExtractorTask;
import im.boss66.com.widget.MyGridView;

/**
 * Created by Johnny on 2017/2/18.
 * 表情组详情
 */
public class EmojiGroupDetailsActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = EmojiSelectWellActivity.class.getSimpleName();
    private PopupWindow popupWindow;
    private TextView tvBack, tvTitle, tvName, tvDesc, tv_download;
    private ProgressBar progressBar;
    private MyGridView gridView;
    private ImageView ivCover;
    private String packid;
    private ImageLoader imageLoader;
    private EmoPicAdapter adapter;
    private HttpHandler<File> sHandler = null;
    private HttpUtils http = null;
    private String userid;
    private EmojiInform inform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(EmojiGroupDetailsActivity.this);//注册，在setContentView之前。
        setContentView(R.layout.activity_emoji_group_details);
        initViews();
        request();
    }

    private void initViews() {
        userid = App.getInstance().getUid();
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        packid = getIntent().getExtras().getString("packid", "");
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.title);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tv_download = (TextView) findViewById(R.id.tv_download);
        progressBar = (ProgressBar) findViewById(R.id.download_pb);
        ivCover = (ImageView) findViewById(R.id.iv_cover);
        gridView = (MyGridView) findViewById(R.id.gridView);
        adapter = new EmoPicAdapter(context);
        gridView.setAdapter(adapter);
//        gridView.setOnItemLongClickListener(new ItemLongClickListener());
        tvBack.setOnClickListener(this);
        tv_download.setOnClickListener(this);
    }

    private void request() {
        if (packid != null && !packid.equals("")) {
            showLoadingDialog();
            EmoInformRequest request = new EmoInformRequest(TAG, packid);
            request.send(new BaseDataRequest.RequestCallback<EmojiInform>() {
                @Override
                public void onSuccess(EmojiInform pojo) {
                    cancelLoadingDialog();
                    bindDatas(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                }
            });
        }
    }

    private void bindDatas(EmojiInform inform) {
        this.inform = inform;
        String cover = inform.getGroup_cover();
        if (!cover.equals("")) {
            ivCover.getLayoutParams().height = (int) ((UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 10)) * 9 / 16f);
            imageLoader.displayImage(cover, ivCover, ImageLoaderUtils.getDisplayImageOptions());
        }
        tvTitle.setText(inform.getGroup_name());
        tvName.setText(inform.getGroup_name());
        tvDesc.setText(inform.getGroup_desc());

        if (isDownload(inform)) {
            tv_download.setText("已下载");
            tv_download.setEnabled(false);
        } else {
            tv_download.setText("下载");
        }

        ArrayList<EmoEntity> emos = inform.getEmos();
        if (emos != null && emos.size() != 0) {
            adapter.initData(emos);
        }
    }

    private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoEntity entity = (EmoEntity) adapterView.getItemAtPosition(i);
            if (popupWindow == null) {
                showCurrentEmo(entity, view);
            } else {
                if (!popupWindow.isShowing()) {
                    showCurrentEmo(entity, view);
                }
            }
            return true;
        }
    }

    private void showCurrentEmo(EmoEntity entity, View v) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindows_item_emo, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);

        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        SimpleDraweeView gif = (SimpleDraweeView) view.findViewById(R.id.gif);
        String imageUrl = entity.getUrl();
        if (NetworkUtil.networkAvailable(context) && !imageUrl.equals("")) {
            Uri uri = Uri.parse(imageUrl);
            //加载动态图片
            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
            gif.setController(controller);
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
//        popupWindow.showAtLocation(Parent, Gravity.NO_GRAVITY, location[0], location[1]);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2, location[1] - popupWindow.getHeight());
    }


    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_download:
                if (tv_download.isEnabled()) {
                    if (inform != null) {
                        download(inform);
                    }
                } else {
                    showToast("该表情包已下载至本地!", true);
                }
                break;
        }
    }

    public void download(final EmojiInform entity) {
//        final EmoBagEntity entity = adapter.getData().get(position);
        String dowloadUrl = entity.getUrl();
        final String fileName = FileUtils.getFileNameFromPath(dowloadUrl);
        final File file = new File(Constants.EMO_DIR_PATH, fileName);
        if (file.exists()) {
            file.delete();
        }
        http = new HttpUtils();
        sHandler = http.download(dowloadUrl, file.getPath().toString(), true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        double i = current / (double) total * 100;
                        int progress = (int) Math.ceil(i);
                        if (progressBar != null) {
                            progressBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        ZipExtractorTask task = new ZipExtractorTask(file.getPath(), Constants.EMO_DIR_PATH,
                                EmojiGroupDetailsActivity.this, true, new ZipExtractorTask.Callback() {
                            @Override
                            public void onPreExecute() {
                                if (tv_download != null) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_download.setVisibility(View.VISIBLE);
                                    tv_download.setText("解压中");
//                                    tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
//                                    tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
                                }
                            }

                            @Override
                            public void onComplete() {//解压完成，删除压缩包文件
                                FileUtil.deleteFile(file.getPath().toString());
                                String filepath = Constants.EMO_DIR_PATH + File.separator + fileName.replace(".zip", ".json");
                                EmoDbTask dbTask = new EmoDbTask(filepath, new EmoDbTask.dbTaskCallback() {//解析.json文件。保持db数据
                                    @Override
                                    public void onPreExecute() {

                                    }

                                    @Override
                                    public void onComplete() {
                                        if (tv_download != null) {
                                            progressBar.setVisibility(View.GONE);
                                            tv_download.setVisibility(View.VISIBLE);
                                            tv_download.setText("已下载");
                                            saveDownloadedStatus(entity);
                                            tv_download.setEnabled(false);
//                                            tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
//                                            tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
                                        }
                                    }
                                });
                                dbTask.execute();
                            }
                        });
                        task.execute();
                    }
                });
    }

    private boolean isDownload(EmojiInform entity) {
        String key = PrefKey.EMOJI_DOWNLOAD_KEY + "/" + userid + "/" + entity.getGroup_id();
        return PreferenceUtils.getBoolean(context, key, false);
    }

    public void saveDownloadedStatus(EmojiInform entity) {
        String key = PrefKey.EMOJI_DOWNLOAD_KEY + "/" + userid + "/" + entity.getGroup_id();
        PreferenceUtils.putBoolean(context, key, true);
    }
}
