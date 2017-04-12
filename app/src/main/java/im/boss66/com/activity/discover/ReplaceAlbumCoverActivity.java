package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.personage.ClipImageActivity;
import im.boss66.com.config.LoginStatus;
import im.boss66.com.entity.AlbumCoverEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.Utils;

/**
 * 更换相册封面
 */
public class ReplaceAlbumCoverActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_photo_album, tv_take_photos;
    private Uri imageUri;
    private String imageName, mOutputPath, access_token;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int REQUEST_CLIP_IMAGE = 3;//裁剪
    private PermissionListener permissionListener;
    private int cameraType;//1:相机 2：相册

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
                cameraType = OPEN_ALBUM;
                getPermission();
                break;
            case R.id.tv_take_photos://拍照
                cameraType = OPEN_CAMERA;
                getPermission();
                break;
        }
    }

    private void uploadImageFile(String path) {
        showLoadingDialog();
        String main = HttpUrl.CHANE_ALBUM_COVER;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        Bitmap bitmap = FileUtils.compressImageFromFile(path, 1080);
        if (bitmap != null) {
            File file = FileUtils.compressImage(bitmap);
            if (file != null) {
                params.addBodyParameter("cover_pic", file);
            }
        }
        params.addBodyParameter("access_token", access_token);

        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    cancelLoadingDialog();
                    AlbumCoverEntity entity = JSON.parseObject(responseInfo.result, AlbumCoverEntity.class);
                    if (entity != null) {
                        if (entity.status == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            AlbumCoverEntity.Result result = entity.getResult();
                            if (result != null) {
                                ToastUtil.showShort(context, "更改成功");
                                String url = result.getAvatar();
                                Intent intent = new Intent();
                                intent.putExtra("icon_url", url);
                                LoginStatus loginStatus = LoginStatus.getInstance();
                                loginStatus.setCover_pic(url);
                                SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putString("albumCover", url);
                                editor.apply();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(context, "上传失败");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast("上传失败", false);
                }
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (imageUri != null) {
                String path;
                if (Build.VERSION.SDK_INT < 24) {
                    path = Utils.getPath(this, imageUri);
                } else {
                    path = imageUri.toString();
                }
                if (!TextUtils.isEmpty(path)) {
                    ClipImageActivity.prepare()
                            .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                            .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                            .startForResult(this, REQUEST_CLIP_IMAGE);
                }
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK && data != null) { //打开相册
            ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (selectPicList != null && selectPicList.size() > 0) {
                String path = selectPicList.get(0);
                if (!TextUtils.isEmpty(path)) {
                    ClipImageActivity.prepare()
                            .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                            .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                            .startForResult(this, REQUEST_CLIP_IMAGE);
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

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == OPEN_CAMERA) {
                    if (Build.VERSION.SDK_INT < 24) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageName = getNowTime() + ".jpg";
                        // 指定调用相机拍照后照片的储存路径
                        File dir = new File(savePath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, imageName);
                        imageUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, OPEN_CAMERA);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageName = getNowTime() + ".jpg";
                        File file = new File(savePath, imageName);
                        imageUri = FileProvider.getUriForFile(ReplaceAlbumCoverActivity.this, "im.boss66.com.fileProvider", file);//这里进行替换uri的获得方式
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                } else if (cameraType == OPEN_ALBUM) {
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(1)
                            .single() // 单选模式
                            .start(ReplaceAlbumCoverActivity.this, OPEN_ALBUM);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(ReplaceAlbumCoverActivity.this, getString(R.string.giving_camera_permissions));
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

}
