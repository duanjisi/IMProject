package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.book.SelectContactsActivity;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.MultiImageView;

/**
 * 朋友圈发消息
 */
public class FriendSendNewMsgActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private RelativeLayout rl_video, rl_who_can_see, rl_remind_who_see;
    private ImageView iv_video_bg, iv_video_play, iv_video_add;
    private MultiImageView multiImagView;
    private EditText et_tx;
    private TextView tv_back, tv_title, tv_right, tv_remind_people, tv_people;
    private String SEND_TYPE_TEXT = "text";
    private String SEND_TYPE_PHOTO = "photo";
    private String SEND_TYPE_VIDEO = "video";
    private int sceenW;
    private String access_token, who_see = "0";//who_see----0:公开 所有朋友可见 1:私密 仅自己可见 2:部分可见 3:不给谁看
    private String who_see_ext = "all";//若who_see=0 ，此字段值为all 若who_see=1，
    // 此字段值为myself 若who_see=2, 此字段值为可见的好友ID 若who_see=3，此字段值为不可见的好友ID
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int RECORD_VIDEO = 3;//视频
    private final int EDIT_PHOTO = 4;//查看相片
    private final int SELECT_PERSON = 5;//选择联系人
    private List<String> imgList;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private Uri imageUri;
    private String sendType;
    private int cameraType;
    private PermissionListener permissionListener;
    private String videoPath;
    private Dialog dialog;
    private String selectMember, remindMember;
    private int whoCanSee;
    private boolean isSelectCanSee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_send_new_msg);
        initView();
    }

    private void initView() {
        imgList = new ArrayList<>();
        access_token = App.getInstance().getAccount().getAccess_token();
        sceenW = UIUtils.getScreenWidth(this);
        tv_people = (TextView) findViewById(R.id.tv_people);
        tv_remind_people = (TextView) findViewById(R.id.tv_remind_people);
        rl_remind_who_see = (RelativeLayout) findViewById(R.id.rl_remind_who_see);
        rl_who_can_see = (RelativeLayout) findViewById(R.id.rl_who_can_see);
        iv_video_add = (ImageView) findViewById(R.id.iv_video_add);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        iv_video_bg = (ImageView) findViewById(R.id.iv_video_bg);
        iv_video_play = (ImageView) findViewById(R.id.iv_video_play);
        multiImagView = (MultiImageView) findViewById(R.id.multiImagView);
        et_tx = (EditText) findViewById(R.id.et_tx);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.send_text));
        tv_right.setText(getString(R.string.send));
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_right.setVisibility(View.VISIBLE);
        rl_remind_who_see.setOnClickListener(this);
        rl_who_can_see.setOnClickListener(this);

        LinearLayout.LayoutParams params_add = (LinearLayout.LayoutParams) iv_video_add.getLayoutParams();
        params_add.width = (int) (sceenW / 3 * 0.8);
        params_add.height = (int) (sceenW / 3 * 0.8);
        iv_video_add.setLayoutParams(params_add);
        iv_video_add.setOnClickListener(this);

        RelativeLayout.LayoutParams params_bg = (RelativeLayout.LayoutParams) iv_video_bg.getLayoutParams();
        params_bg.width = (int) (sceenW / 3 * 0.8);
        params_bg.height = (int) (sceenW / 3 * 0.8);
        iv_video_bg.setLayoutParams(params_bg);
        RelativeLayout.LayoutParams params_p = (RelativeLayout.LayoutParams) iv_video_play.getLayoutParams();
        params_p.width = sceenW / 8;
        params_p.height = sceenW / 8;
        iv_video_play.setLayoutParams(params_p);
        rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("url", videoPath);
                bundle.putBoolean("isFull", false);
                bundle.putBoolean("isDelete", true);
                openActvityForResult(VideoPlayerActivity.class, RECORD_VIDEO, bundle);
            }
        });
        multiImagView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (imgList != null) {
                    int size = imgList.size();
                    if (size < 9 && position == size) {
                        showActionSheet();
                    } else {
                        //imagesize是作为loading时的图片size
                        ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                        Intent intent = new Intent(FriendSendNewMsgActivity.this, ImagePagerActivity.class);
                        intent.putStringArrayListExtra("imgurls", new ArrayList<>(imgList));
                        intent.putExtra("position", position);
                        intent.putExtra("imagesize", imageSize);
                        intent.putExtra("isdelete", true);
                        startActivityForResult(intent, EDIT_PHOTO);
                    }
                }
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) et_tx.getLayoutParams();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                sendType = bundle.getString("sendType");
                if (!TextUtils.isEmpty(sendType)) {
                    if (SEND_TYPE_TEXT.equals(sendType)) {
                        multiImagView.setVisibility(View.GONE);
                        params.height = sceenW / 4;
                        et_tx.setLayoutParams(params);
                    } else if (SEND_TYPE_PHOTO.equals(sendType)) {
                        tv_title.setVisibility(View.GONE);
                        multiImagView.setVisibility(View.VISIBLE);
                        params.height = sceenW / 6;
                        et_tx.setLayoutParams(params);
                        int type = bundle.getInt("type");
                        if (type == OPEN_CAMERA) {
                            String img = bundle.getString("img");
                            if (!TextUtils.isEmpty(img)) {
                                imgList.add(img);
                            }
                        } else if (type == OPEN_ALBUM) {
                            ArrayList<String> selectPicList = bundle.getStringArrayList("imglist");
                            imgList.addAll(selectPicList);
                        }
                        showImg();
                    } else if (SEND_TYPE_VIDEO.equals(sendType)) {
                        int type = bundle.getInt("type");
                        if (type == RECORD_VIDEO) {
                            tv_title.setVisibility(View.GONE);
                            rl_video.setVisibility(View.VISIBLE);
                            videoPath = bundle.getString("videoPath");
                            MediaMetadataRetriever media = new MediaMetadataRetriever();
                            media.setDataSource(videoPath);
                            Bitmap bitmap = media.getFrameAtTime();
                            if (bitmap != null) {
                                iv_video_bg.setImageBitmap(bitmap);
                            }
                            media.release();
                        }
                    }
                }
            }
        }
    }

    private void showImg() {
        if (imgList != null && imgList.size() > 0) {
            multiImagView.setVisibility(View.VISIBLE);
            int imgSize = imgList.size();
            int imgW = (int) (sceenW / 3 * 0.8);
            List<PhotoInfo> lists = new ArrayList<>();
            for (int i = 0; i < imgSize; i++) {
                PhotoInfo item = new PhotoInfo();
                item.file_url = imgList.get(i);
                item.type = 0;
                item.w = imgW;
                item.h = imgW;
                lists.add(item);
            }
            if (imgSize < 9) {
                PhotoInfo item = new PhotoInfo();
                item.resourceid = R.drawable.hp_add_photos;
                item.type = 1;
                item.w = imgW;
                item.h = imgW;
                lists.add(item);
            }
            multiImagView.setList(lists);
        } else {
            multiImagView.setVisibility(View.GONE);
            iv_video_add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                showDialog();
                break;
            case R.id.tv_right://发送
                sendPhotoText();
                break;
            case R.id.iv_video_add:
                if (SEND_TYPE_PHOTO.equals(sendType)) {
                    showActionSheet();
                } else {
                    Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                    mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                    startActivityForResult(mIntent, RECORD_VIDEO);
                }
                break;
            case R.id.bt_close:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
            case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
                break;
            case R.id.rl_remind_who_see://提醒谁看
                isSelectCanSee = false;
                Bundle bundle = new Bundle();
                bundle.putString("classType", "FriendCircleWhoSeeActivity");
                bundle.putString("user_ids",remindMember);
                openActvityForResult(SelectContactsActivity.class, SELECT_PERSON, bundle);
                break;
            case R.id.rl_who_can_see://谁可以看
                isSelectCanSee = true;
                Bundle bundle1 = new Bundle();
                bundle1.putString("classType", "FriendSendNewMsgActivity");
                openActvityForResult(FriendCircleWhoSeeActivity.class, SELECT_PERSON, bundle1);
                break;
        }
    }

    private void sendPhotoText() {
        String content = et_tx.getText().toString().trim();
        showLoadingDialog();
        String url = HttpUrl.CREATE_CIRCLE_PHOTO_TX;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        switch (whoCanSee) {
            case 0:
                who_see_ext = "all";
                break;
            case 1:
                who_see_ext = "myself";
                break;
            case 2:
            case 3:
                who_see_ext = selectMember;
                break;
        }
        who_see = String.valueOf(whoCanSee);
        params.addBodyParameter("who_see", who_see);
        params.addBodyParameter("who_see_ext", who_see_ext);
        if (SEND_TYPE_PHOTO.equals(sendType) && imgList != null) {
            for (int i = 0; i < imgList.size(); i++) {
                String path = imgList.get(i);
                File file = new File(path);
                params.addBodyParameter("files" + "[" + i + "]", file);
            }
        } else if (SEND_TYPE_VIDEO.equals(sendType) && !TextUtils.isEmpty(videoPath)) {
            url = HttpUrl.CREATE_CIRCLE_VIDEO_TX;
            File file = new File(videoPath);
            params.addBodyParameter("files", file);
        }
        if (!TextUtils.isEmpty(content)) {
            params.addBodyParameter("content", content);
        }
        if (!TextUtils.isEmpty(remindMember)) {
            params.addBodyParameter("remind_user", remindMember);
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                showToast("发布成功", false);
                                setResult(RESULT_OK);
                                finish();
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
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(s, false);
            }
        });
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(FriendSendNewMsgActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet
                .addSheetItem(getString(R.string.take_photos), ActionSheet.SheetItemColor.Black,
                        FriendSendNewMsgActivity.this)
                .addSheetItem(getString(R.string.from_the_mobile_phone_photo_album_choice),
                        ActionSheet.SheetItemColor.Black, FriendSendNewMsgActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1://拍照
                cameraType = OPEN_CAMERA;
                getPermission();
                break;
            case 2://从相册选择
                cameraType = OPEN_ALBUM;
                getPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (imageUri != null) {
                String path = Utils.getPath(this, imageUri);
                if (!TextUtils.isEmpty(path)) {
                    imgList.add(path);
                    showImg();
                }
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK && data != null) { //打开相册
            ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (selectPicList != null && imgList.size() > 0) {
                imgList.addAll(selectPicList);
                showImg();
            }
        } else if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK && data != null) {//视频
            boolean isdelete = data.getBooleanExtra("isdelete", false);
            if (isdelete) {
                rl_video.setVisibility(View.GONE);
                iv_video_add.setVisibility(View.VISIBLE);
            } else {
                // 录制视频完成
                try {
                    AssetFileDescriptor videoAsset = getContentResolver()
                            .openAssetFileDescriptor(data.getData(), "r");
                    FileInputStream fis = videoAsset.createInputStream();
                    File tmpFile = new File(
                            Environment.getExternalStorageDirectory(),
                            "recordvideo.mp4");
                    FileOutputStream fos = new FileOutputStream(tmpFile);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    fis.close();
                    fos.close();
                    // 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4
                    deleteDefaultFile(data.getData());
                    videoPath = tmpFile.getAbsolutePath();
                    rl_video.setVisibility(View.VISIBLE);
                    iv_video_add.setVisibility(View.GONE);
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(videoPath);
                    Bitmap bitmap = media.getFrameAtTime();
                    if (bitmap != null) {
                        iv_video_bg.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == EDIT_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<String> list = data.getStringArrayListExtra("lits");
                if (list != null) {
                    imgList = list;
                } else {
                    imgList.clear();
                }
            } else {
                imgList.clear();
            }
            showImg();
        } else if (requestCode == SELECT_PERSON && resultCode == RESULT_OK && data != null) {
            String memberUserNames = data.getStringExtra("memberUserNames");
            if (isSelectCanSee) {
                selectMember = data.getStringExtra("member_id");
                whoCanSee = data.getIntExtra("whoCanSee", 0);
                if (!TextUtils.isEmpty(memberUserNames)) {
                    showWhoCanSee(memberUserNames);
                }
            } else {
                remindMember = data.getStringExtra("member_id");
                if (!TextUtils.isEmpty(memberUserNames)) {
                    tv_remind_people.setText(memberUserNames);
                }
            }
        }
    }

    private void showWhoCanSee(String name) {
        switch (whoCanSee) {
            case 0:
                tv_people.setText("公开");
                break;
            case 1:
                tv_people.setText("私密");
                break;
            case 2:
                tv_people.setText(name);
                break;
            case 3:
                tv_people.setText("除去 " + name);
                break;
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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageName = getNowTime() + ".png";
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
                } else if (cameraType == OPEN_ALBUM) {
                    int size = 9 - imgList.size();
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(size)
                            .multi() // 多选模式, 默认模式;
                            .start(FriendSendNewMsgActivity.this, OPEN_ALBUM);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(FriendSendNewMsgActivity.this, getString(R.string.giving_camera_permissions));
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

    // 删除在/sdcard/dcim/Camera/默认生成的文件
    private void deleteDefaultFile(Uri uri) {
        String fileName = null;
        if (uri != null) {
            // content
            Log.d("Scheme", uri.getScheme().toString());
            if (uri.getScheme().toString().equals("content")) {
                Cursor cursor = getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor.moveToNext()) {
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    fileName = cursor.getString(columnIndex);
                    //获取缩略图id
                    int id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Video.VideoColumns._ID));
                    //获取缩略图
//                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                            getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND,
//                            null);

                    if (!fileName.startsWith("/mnt")) {
                        fileName = "/mnt/" + fileName;
                    }
                    Log.d("fileName", fileName);
                }
            }
        }
        // 删除文件
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            Log.d("delete", "删除成功");
        }
    }

    private void showDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_sure_phone_num, null);
            Button bt_close = (Button) view.findViewById(R.id.bt_close);
            Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
            TextView tv_dia_num = (TextView) view.findViewById(R.id.tv_dia_num);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            TextView tv_dia_title = (TextView) view.findViewById(R.id.tv_dia_title);
            tv_dia_title.setText(getString(R.string.if_exit_edit));
            tv_content.setVisibility(View.GONE);
            tv_dia_num.setVisibility(View.GONE);
            bt_close.setOnClickListener(this);
            bt_ok.setText("退出");
            bt_ok.setOnClickListener(this);
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = sceenW / 3 * 2;
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            showDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
