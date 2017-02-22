package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.PictureAdapter;
import im.boss66.com.db.dao.EmoLoveHelper;
import im.boss66.com.entity.EmoLove;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.MyGridView;

/**
 * 我添加的表情
 * Created by Johnny on 2017/2/11.
 */
public class EmojiAddActivity extends BaseActivity implements View.OnClickListener {
    public final static String CHATPHOTO_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator;
    private TextView tvBack, tvDo;
    private MyGridView gridView;
    private PictureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_add);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
//        tvDo = (TextView) findViewById(R.id.tv_ok);
        tvBack.setOnClickListener(this);
        gridView = (MyGridView) findViewById(R.id.gridView);
        adapter = new PictureAdapter(context);
        adapter.setAddPager(true);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        adapter.initData(EmoLoveHelper.getInstance().qureList());
//        tvDo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                setResult(RESULT_OK);
                finish();
                break;
//            case R.id.tv_ok:
//                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String string = (String) parent.getItemAtPosition(position);
            if (string.equals("lastItem")) {
                Log.i("info", "=====lastItem");
                MultiImageSelector.create(context).
                        showCamera(true).
                        count(1)
                        .start(EmojiAddActivity.this, 100);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 100) {
                ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (selectPicList != null && selectPicList.size() != 0) {
                    String image = selectPicList.get(0);
                    if (image != null && !image.equals("")) {
                        uploadImageFile(image);
                    }
                }
            }
        }
    }

    private void uploadImageFile(final String path) {
        String main = HttpUrl.UPLOAD_IMAGE_URL;
//        showLoadingDialog();
        final HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        final com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        try {
            String fileName = FileUtils.getFileNameFromPath(path);
            String compressPath = FileUtils.compressImage(path, CHATPHOTO_PATH + fileName, 30);
            File file = new File(compressPath);
            if (file.exists() && file.length() > 0) {
                params.addBodyParameter("file", file);
            }
            MycsLog.i("info", "AbsolutePath:" + file.getAbsolutePath());
            httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Log.i("info", "responseInfo:" + responseInfo.result);
                    EmoLove love = new EmoLove();
                    love.setEmo_url(parsePath(responseInfo.result));
                    EmoLoveHelper.getInstance().save(love);
                    adapter.addItem2(parsePath(responseInfo.result));
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
                    cancelLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePath(String json) {
        try {
            JSONObject object = new JSONObject(json);
            int code = object.getInt("code");
            if (code == 0) {
                return object.getString("data");
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
