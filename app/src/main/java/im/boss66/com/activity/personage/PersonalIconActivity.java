package im.boss66.com.activity.personage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.config.LoginStatus;
import im.boss66.com.entity.ChangeAvatarEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.photoview.PhotoView;

/**
 * 个人头像
 */
public class PersonalIconActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back, tv_right;
    private PhotoView iv_icon;
    private ActionSheet actionSheet;
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int REQUEST_CLIP_IMAGE = 3;//裁剪
    private Bitmap bitmap;
    private boolean isLongClick = false;
    private String mOutputPath;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private ImageLoader imageLoader;
    //    private final String savePath = Environment
//            .getExternalStorageDirectory()
//            + File.separator;
    private Uri imageUri;
    private String imageName;
    private String access_token;
    private boolean isHeadChange = false;
    private String headurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_icon);
        initView();
    }

    private void initView() {
        access_token = App.getInstance().getAccount().getAccess_token();
        mOutputPath = new File(getExternalCacheDir(), "chosen.jpg").getPath();
        int screenW = UIUtils.getScreenWidth(this);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_right = (TextView) findViewById(R.id.tv_right);
        iv_icon = (PhotoView) findViewById(R.id.iv_icon);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) iv_icon.getLayoutParams();
        linearParams.height = screenW;
        iv_icon.setLayoutParams(linearParams);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isLongClick = true;
                return false;
            }
        });
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String head = bundle.getString("head");
                if (!TextUtils.isEmpty(head)) {
                    imageLoader.displayImage(head, iv_icon,
                            ImageLoaderUtils.getDisplayImageOptions());
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                goBack();
                break;
            case R.id.tv_right:
                isLongClick = false;
                showActionSheet();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void goBack() {
        if (isHeadChange) {
            Intent intent = new Intent();
            intent.putExtra("headicon", headurl);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void showActionSheet() {
        actionSheet = new ActionSheet(PersonalIconActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (isLongClick) {
            actionSheet.addSheetItem("保存图片", ActionSheet.SheetItemColor.Black,
                    PersonalIconActivity.this);
        } else {
            actionSheet.addSheetItem("拍照", ActionSheet.SheetItemColor.Black,
                    PersonalIconActivity.this)
                    .addSheetItem("从手机相册选择", ActionSheet.SheetItemColor.Black,
                            PersonalIconActivity.this)
                    .addSheetItem("保存图片", ActionSheet.SheetItemColor.Black,
                            PersonalIconActivity.this);
        }
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1://拍照 or 长按 保存图片
                if (isLongClick) {//长按 保存图片
                    if (bitmap != null) {
                        FileUtil.saveBitmap(this, bitmap);
                    }
                    ToastUtil.showShort(this, getString(R.string.success_save));
                } else {//拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    imageName = getNowTime() + ".png";
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
                }
                break;
            case 2://从手机相册选择
                imageName = getNowTime() + ".png";
                imageUri = Uri
                        .fromFile(new File(savePath, imageName));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, OPEN_ALBUM);
                break;
            case 3://保存图片
                if (bitmap != null) {
                    FileUtil.saveBitmap(this, bitmap);
                }
                ToastUtil.showShort(this, getString(R.string.success_save));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (imageUri != null) {
                String path = Utils.getPath(this, imageUri);
                if (!TextUtils.isEmpty(path)) {
                    ClipImageActivity.prepare()
                            .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                            .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                            .startForResult(this, REQUEST_CLIP_IMAGE);
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

    private void uploadImageFile(String path) {
        showLoadingDialog();
        String main = HttpUrl.CHANGE_AVAYAR;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        File file = new File(path);
        params.addBodyParameter("access_token", access_token);
        params.addBodyParameter("avatar", file);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    cancelLoadingDialog();
                    ChangeAvatarEntity entity = JSON.parseObject(responseInfo.result, ChangeAvatarEntity.class);
                    if (entity != null) {
                        ChangeAvatarEntity.Result result = entity.getResult();
                        if (result != null) {
                            ToastUtil.showShort(context, "更改成功");
                            headurl = result.getAvatar();
                            imageLoader.displayImage(headurl, iv_icon,
                                    ImageLoaderUtils.getDisplayImageOptions());
                            LoginStatus loginStatus = LoginStatus.getInstance();
                            String avatar = result.getAvatar();
                            if (!TextUtils.isEmpty(avatar)) {
                                loginStatus.setAvatar(avatar);
                            }
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

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

}
