package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.personage.ClipImageActivity;
import im.boss66.com.config.LoginStatus;
import im.boss66.com.entity.AlbumCoverEntity;
import im.boss66.com.entity.ChangeAvatarEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.util.Utils;

/**
 * 更换相册封面
 */
public class ReplaceAlbumCoverActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_photo_album, tv_take_photos;
    private Uri imageUri;
    private String imageName,mOutputPath,access_token;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int REQUEST_CLIP_IMAGE = 3;//裁剪
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_album_cover);
        initView();
    }

    private void initView() {
        access_token = App.getInstance().getAccount().getAccess_token();
        mOutputPath = new File(getExternalCacheDir(), "chosen.jpg").getPath();
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_photo_album = (TextView) findViewById(R.id.tv_photo_album);
        tv_take_photos = (TextView) findViewById(R.id.tv_take_photos);
        tv_title.setText(getString(R.string.replace_the_album_cover));
        tv_back.setOnClickListener(this);
        tv_photo_album.setOnClickListener(this);
        tv_take_photos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_photo_album://相册
                imageName = getNowTime() + ".png";
                imageUri = Uri
                        .fromFile(new File(savePath, imageName));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                startActivityForResult(intent, OPEN_ALBUM);
                break;
            case R.id.tv_take_photos://拍照
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                imageName = getNowTime() + ".png";
                // 指定调用相机拍照后照片的储存路径
                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, imageName);
                imageUri = Uri.fromFile(file);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent1, OPEN_CAMERA);
                }
                break;
        }
    }

    private void uploadImageFile(String path) {
        showLoadingDialog();
        String main = HttpUrl.CHANE_ALBUM_COVER;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        File file = new File(path);
        params.addBodyParameter("access_token", access_token);
        params.addBodyParameter("cover_pic", file);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    cancelLoadingDialog();
                    AlbumCoverEntity entity = JSON.parseObject(responseInfo.result, AlbumCoverEntity.class);
                    if (entity != null) {
                        AlbumCoverEntity.Result result = entity.getResult();
                        if (result != null) {
                            ToastUtil.showShort(context, "更改成功");
                            String url = result.getAvatar();
                            Intent intent = new Intent();
                            intent.putExtra("icon_url",url);
                            SharedPreferences mPreferences = context.getSharedPreferences("albumCover",MODE_PRIVATE);
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putString("albumCover", url);
                            editor.apply();
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(context, "上传失败");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
                cancelLoadingDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (data == null) {
                return;
            } else {
                if (imageUri != null) {
                    String path = Utils.getPath(this, imageUri);
                    if (!TextUtils.isEmpty(path)) {
                        ClipImageActivity.prepare()
                                .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                                .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                                .startForResult(this, REQUEST_CLIP_IMAGE);
                    }
                }
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK) { //打开相册
            if (data == null) {
                return;
            } else {
                Uri originalUri = data.getData();  //获得图片的uri
                if (originalUri != null) {
                    String path = Utils.getPath(this, originalUri);
                    if (!TextUtils.isEmpty(path)) {
                        ClipImageActivity.prepare()
                                .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                                .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                                .startForResult(this, REQUEST_CLIP_IMAGE);
                    }
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CLIP_IMAGE) {
            String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
            if (path != null) {
                uploadImageFile(path);
            }
            return;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

}
