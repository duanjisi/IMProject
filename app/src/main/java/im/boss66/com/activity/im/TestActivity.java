package im.boss66.com.activity.im;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import im.boss66.com.Code;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/24.
 */
public class TestActivity extends BaseActivity {
    private String imagePath;
    private TextView tvPhoto, tvAlum, tvEdit;
    private ImageView imageView;
    /* 照相机拍照得到的图片 */
    private File mCurrentPhotoFile;
    private String photoPath = null, tempPhotoPath, camera_path;

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initViews();
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.iv_image);
        tvPhoto = (TextView) findViewById(R.id.tv_photo);
        tvAlum = (TextView) findViewById(R.id.tv_alum);
        tvEdit = (TextView) findViewById(R.id.tv_edit);


        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });


        tvAlum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImg();
            }
        });


        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EmojiEditActivity.class);
                intent.putExtra("camera_path", imagePath);
                startActivityForResult(intent, 100);
            }
        });
    }

    //    private String photoPath = "";
    private void takePhoto() {
        String status = Environment.getExternalStorageState();
        // 检测手机是否有sd卡
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            // 创建存放照片的文件夹
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 开启照相机
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            photoPath = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, Code.Request.TAKE_PHOTO);
        } else {
            Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }


    private void takeImg() {
        Intent intent;
        intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Code.Request.GET_PHOTO);
    }

    /* 从相机中获取照片 */
    private void getPictureFormCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        tempPhotoPath = FileUtils.DCIMCamera_PATH + FileUtils.getNewFileName()
                + ".jpg";

        mCurrentPhotoFile = new File(tempPhotoPath);

        if (!mCurrentPhotoFile.exists()) {
            try {
                mCurrentPhotoFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(mCurrentPhotoFile));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode != RESULT_OK) {
//            return;
//        }
//
//        switch (requestCode) {
//            case CAMERA_WITH_DATA:
//
//                photoPath = tempPhotoPath;
//                if (content_layout.getWidth() == 0) {
//                    timer.schedule(task, 10, 1000);
//                } else {
//                    compressed();
//                }
//
//                break;
//
//            case PHOTO_PICKED_WITH_DATA:
//
//                Uri selectedImage = data.getData();
//                String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                photoPath = c.getString(columnIndex);
//                c.close();
//
//                // 延迟每次延迟10 毫秒 隔1秒执行一次
//                if (content_layout.getWidth() == 0) {
//                    timer.schedule(task, 10, 1000);
//                } else {
//                    compressed();
//                }
//
//                break;
//        }

        if (requestCode == Code.Request.GET_PHOTO) {
            if (data != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Uri originalUri = data.getData();
                if (originalUri == null) return;
                Cursor cursor = new CursorLoader(context, originalUri, proj, null, null, null).loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                final String picPath = cursor.getString(column_index);
                uploadImageFile(picPath);
            }
        } else if (requestCode == Code.Request.TAKE_PHOTO) {
            uploadImageFile(photoPath);
        } else if (requestCode == 100) {
            String resultPath = data.getStringExtra("camera_path");
            Bitmap resultBitmap = BitmapFactory.decodeFile(resultPath);
            imageView.setImageBitmap(resultBitmap);
        }
    }

    private void uploadImageFile(final String path) {
        imagePath = path;
    }
}
