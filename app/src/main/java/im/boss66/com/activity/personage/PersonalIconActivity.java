package im.boss66.com.activity.personage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
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
    private String savePath = Environment.getExternalStorageDirectory()+ "/IMProject/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_icon);
        initView();
    }

    private void initView() {
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
                isLongClick = false;
                showActionSheet();
                break;
        }
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
                    if (bitmap != null){
                        FileUtil.saveBitmap(this,bitmap);
                    }
                    ToastUtil.showShort(this,getString(R.string.success_save));
                } else {//拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                }
                break;
            case 2://从手机相册选择
                boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                Intent getAlbum;
                if (isKitKatO) {
                    getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                }
                getAlbum.setType("image/*");

                startActivityForResult(getAlbum, OPEN_ALBUM);

                break;
            case 3://保存图片
                if (bitmap != null){
                    FileUtil.saveBitmap(this,bitmap);
                }
                ToastUtil.showShort(this,getString(R.string.success_save));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = extras.getParcelable("data");
                    if (bitmap != null) {
                        iv_icon.setImageBitmap(bitmap);
                    }
                }
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK) { //打开相册
            if (data == null) {
                return;
            } else {

                Uri originalUri = data.getData();  //获得图片的uri
                String path = Utils.getPath(this, originalUri);
                if (!TextUtils.isEmpty(path)) {
                    ClipImageActivity.prepare()
                            .aspectX(3).aspectY(2)//裁剪框横向及纵向上的比例
                            .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                            .startForResult(this, REQUEST_CLIP_IMAGE);
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CLIP_IMAGE) {
            String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
            if (path != null) {
                bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    iv_icon.setImageBitmap(bitmap);
                }
            }
            return;
        }
    }
}
