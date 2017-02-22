package im.boss66.com.activity.im;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.EmojiWellAdapter;
import im.boss66.com.entity.BaseEmoWell;
import im.boss66.com.entity.EmoAdEntity;
import im.boss66.com.entity.EmoBagEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoWellChoseRequest;
import im.boss66.com.task.EmoDbTask;
import im.boss66.com.task.ZipExtractorTask;
import im.boss66.com.widget.ViewPagerLayout;

/**
 * Created by Johnny on 2017/1/23.
 * 精选表情
 */
public class EmojiSelectWellActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {
    private final static String TAG = EmojiSelectWellActivity.class.getSimpleName();
    private int pagerNum = 10;
    private int pager = 1;
    private TextView tvBack;
    private ImageView ivSeting;
    private TextView tvSearch;
    private ViewPagerLayout mADLayout;
    private View header;
    private ListView listView;
    private EmojiWellAdapter adapter;
    private HttpHandler<File> sHandler = null;
    private HttpUtils http = null;
    private Resources resources;
    /**
     * 设置布局显示目标最大化
     */
    private LinearLayout.LayoutParams WClayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
    private ProgressBar progressBar;
    private View rootView;

    private void initRootView() {
        LinearLayout layout = new LinearLayout(context);
        //设置布局 水平方向
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //进度条
        progressBar = new ProgressBar(context);
        //进度条显示位置
        progressBar.setPadding(0, 0, 15, 0);
        layout.addView(progressBar, WClayoutParams);

        TextView textView = new TextView(context);
        textView.setText("加载中...");
        textView.setGravity(Gravity.CENTER_VERTICAL);

        layout.addView(textView, FFlayoutParams);
        layout.setGravity(Gravity.CENTER);

        LinearLayout loadingLayout = new LinearLayout(context);
        loadingLayout.addView(layout, WClayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        rootView = loadingLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_select_well);
        initRootView();
        initViews();
    }

    private void initViews() {
        resources = getResources();
        header = getLayoutInflater().inflate(R.layout.view_well_chose_top, null);
        mADLayout = (ViewPagerLayout) header.findViewById(R.id.viewPager);
        tvBack = (TextView) findViewById(R.id.tv_back);
        ivSeting = (ImageView) findViewById(R.id.iv_more);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listView = (ListView) findViewById(R.id.listView);
        tvBack.setOnClickListener(this);
        listView.addHeaderView(header);
        listView.addFooterView(rootView);
        adapter = new EmojiWellAdapter(context, mListener);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(new ItemClickListener());
        request();
    }

    private EmojiWellAdapter.MyClickListener mListener = new EmojiWellAdapter.MyClickListener() {
        @Override
        public void myOnClick(int position, View v) {
            v.setVisibility(View.GONE);
            download(position);
        }
    };


    public void download(final int position) {
        final EmoBagEntity entity = adapter.getData().get(position);
//        String path = this.getFilesDir().getAbsolutePath();
        String dowloadUrl = entity.getDownload();
        final String fileName = FileUtils.getFileNameFromPath(dowloadUrl);
        final File file = new File(Constants.EMO_DIR_PATH, fileName);
        if (file.exists()) {
            file.delete();
        }
        http = new HttpUtils();
//        String url = "http://220.194.224.12/dd.myapp.com/16891/526EB7BDA5A8F4A4640E49B51F66D0B4.apk?mkey=56de6f924af9d42f&f=2484&fsname=com.zcycjy.mobile_1.0.5_5.apk&p=.apk";
//        String url = entity.getDownload();
        ProgressBar item = null;
        TextView textView = null;
        if (position >= listView.getFirstVisiblePosition()
                && position <= listView
                .getLastVisiblePosition()) {
//            int positionInListView = position
//                    - listView.getFirstVisiblePosition();
            View view = listView
                    .getChildAt(position + 1);
            item = (ProgressBar) view.findViewById(R.id.download_pb);
            item.setVisibility(View.VISIBLE);
            textView = (TextView) view.findViewById(R.id.tv_download);
        }
        final ProgressBar progressBar = item;
        final TextView tv_download = textView;
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
//                        strList.get(position).setProgress(progress);
//                        if (position >= listView.getFirstVisiblePosition()
//                                && position <= listView
//                                .getLastVisiblePosition()) {
//                            int positionInListView = position
//                                    - listView.getFirstVisiblePosition();
//                            Log.i("info", "======positionInListView:" + positionInListView);
//                            View view = listView
//                                    .getChildAt(positionInListView);
//                            Log.i("info", "======view:" + view);
//                            ProgressBar item = (ProgressBar) view.findViewById(R.id.download_pb);
//                            Log.i("info", "======item:" + item);
//                            item.setVisibility(View.VISIBLE);
//                            item.setProgress(progress);
//                        }
                        if (progressBar != null) {
                            progressBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
//                        if (position >= listView.getFirstVisiblePosition()
//                                && position <= listView
//                                .getLastVisiblePosition()) {
//                            int positionInListView = position
//                                    - listView.getFirstVisiblePosition();
//                            TextView textView = (TextView) listView.getChildAt(
//                                    positionInListView).findViewById(
//                                    R.id.tv_download);
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setTextColor(resources.getColor(R.color.btn_green_noraml));
//                            textView.setBackgroundResource(R.drawable.bg_frame_emo_downloaded);
//                        }

//                        if (tv_download != null) {
//                            progressBar.setVisibility(View.GONE);
//                            tv_download.setVisibility(View.VISIBLE);
//                            tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
//                            tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
//                        }
                        Log.i("info", "======path:" + file.getPath() + "\n" + "AbsolutePath:" + file.getAbsolutePath());
                        ZipExtractorTask task = new ZipExtractorTask(file.getPath(), Constants.EMO_DIR_PATH,
                                EmojiSelectWellActivity.this, true, new ZipExtractorTask.Callback() {
                            @Override
                            public void onPreExecute() {
                                if (tv_download != null) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_download.setVisibility(View.VISIBLE);
                                    tv_download.setText("解压中");
                                    tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
                                    tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
                                }
                            }

                            @Override
                            public void onComplete() {
//                                if (tv_download != null) {
//                                    progressBar.setVisibility(View.GONE);
//                                    tv_download.setVisibility(View.VISIBLE);
//                                    tv_download.setText("下载");
//                                    adapter.saveDownloadedStatus(entity);
//                                    tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
//                                    tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
//                                }
                                String filepath = Constants.EMO_DIR_PATH + File.separator + fileName.replace(".zip", ".json");
                                EmoDbTask dbTask = new EmoDbTask(filepath, new EmoDbTask.dbTaskCallback() {
                                    @Override
                                    public void onPreExecute() {

                                    }
                                    @Override
                                    public void onComplete() {
                                        if (tv_download != null) {
                                            progressBar.setVisibility(View.GONE);
                                            tv_download.setVisibility(View.VISIBLE);
                                            tv_download.setText("下载");
                                            adapter.saveDownloadedStatus(entity);
                                            tv_download.setTextColor(resources.getColor(R.color.btn_green_noraml));
                                            tv_download.setBackgroundResource(R.drawable.bg_frame_emo_default);
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

//    private void ZipExtractorWork(String filePath) {
//        ZipExtractorTask task = new ZipExtractorTask(filePath, Constants.EMO_DIR_PATH, this, true);
//        task.execute();
//    }

//    private void parseJson(String path) {
//        String json = getFileFromSD(path);
//        if (!json.equals("")) {
//            EmoClasses classes = JSON.parseObject(json, EmoClasses.class);
//            if (classes != null) {
//                ArrayList<EmoCate> cates = classes.getCategorys();
//                if (cates.size() != 0) {
//                    EmoCateHelper.getInstance().save(cates);
//                }
//            }
//        }
//    }

//    private String getFileFromSD(String path) {
//        String result = "";
//        try {
//            FileInputStream f = new FileInputStream(path);
//            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
//            String line = "";
//            while ((line = bis.readLine()) != null) {
//                result += line;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_more://设置
                openActivity(EmojiMyActivity.class);
                break;
            case R.id.tv_search://收索

                break;
        }
    }

    private void request() {
        showLoadingDialog();
        EmoWellChoseRequest request = new EmoWellChoseRequest(TAG, "" + pager, "" + pagerNum);
        request.send(new BaseDataRequest.RequestCallback<BaseEmoWell>() {
            @Override
            public void onSuccess(BaseEmoWell pojo) {
                cancelLoadingDialog();
                initData(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }


    private void initData(BaseEmoWell well) {
        ArrayList<EmoAdEntity> ads = well.getHot();
        mADLayout.init(ads);
        mADLayout.setOnAdClickListener(new ViewPagerLayout.OnAdClickListener() {
            @Override
            public void onClick(EmoAdEntity entity) {
                String packid = entity.getGroup_id();
                if (!packid.equals("")) {
                    Intent intent = new Intent(context, EmojiGroupDetailsActivity.class);
                    intent.putExtra("packid", packid);
                    startActivity(intent);
                }
            }
        });
        ArrayList<EmoBagEntity> bags = well.getGroups();
        int size = bags.size();
        if (bags != null && size != 0) {
            if (size < pagerNum) {
                listView.removeFooterView(rootView);
                isload = false;
            } else {
                pager++;
            }
            adapter.initData(bags);
        } else {
            listView.removeFooterView(rootView);
            isload = false;
        }
    }

    private void requestNextPager() {
        if (isload) {
            EmoWellChoseRequest request = new EmoWellChoseRequest(TAG, "" + pager, "" + pagerNum);
            request.send(new BaseDataRequest.RequestCallback<BaseEmoWell>() {
                @Override
                public void onSuccess(BaseEmoWell pojo) {
                    nextPager(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    private boolean isload = true;

    private void nextPager(BaseEmoWell well) {
        ArrayList<EmoBagEntity> bags = well.getGroups();
        int size = bags.size();
        if (bags != null && size != 0) {
            if (size < pagerNum) {
                listView.removeFooterView(rootView);
                isload = false;
                showToast("数据全部加载完!", true);
            } else {
                pager++;
            }
            adapter.addData(bags);
        } else {
            listView.removeFooterView(rootView);
            isload = false;
            showToast("数据全部加载完!", true);
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoBagEntity bagEntity = (EmoBagEntity) adapterView.getItemAtPosition(i);
            String packid = bagEntity.getGroup_id();
            if (!packid.equals("")) {
                Intent intent = new Intent(context, EmojiGroupDetailsActivity.class);
                intent.putExtra("packid", packid);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
// 当不滚动时
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            // 判断是否滚动到底部
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                //加载更多功能的代码
                requestNextPager();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mADLayout.startADLoop();
    }

    @Override
    public void onStop() {
        super.onStop();
        mADLayout.stopADLoop();
    }
}
