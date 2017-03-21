package im.boss66.com.activity.personage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.CircleMessageListActivity;
import im.boss66.com.activity.discover.FriendSendNewMsgActivity;
import im.boss66.com.activity.discover.PhotoAlbumDetailActivity;
import im.boss66.com.activity.discover.PhotoAlbumLookPicActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.adapter.PersonalPhotoAlbumAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleEntity;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;

/**
 * 个人相册
 */
public class PersonalPhotoAlbumActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back, tv_title, tv_signature;
    private ImageView iv_set;
    private LRecyclerView rv_friend;
    private LinearLayout ll_personal;
    private PersonalPhotoAlbumAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private boolean isChangeIcon = false;
    private int ALBUM_COVER = 101;
    private ImageLoader imageLoader;
    private ImageView iv_bg;
    private String access_token;
    private int page;
    private List<FriendCircle> allList;
    private AccountEntity sAccount;
    private boolean isJumpLookPic;
    private App mApplication;
    private boolean isSendNew = false;

    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private Uri imageUri;
    private final int RECORD_VIDEO = 3;//视频
    private PermissionListener permissionListener;
    private int cameraType;//1:相机 2：相册 3：视频
    private int SEND_TYPE_PHOTO_TX = 101;
    private boolean isAddNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_photo_album);
        initView();
    }

    private void initView() {
        mApplication = App.getInstance();
        allList = new ArrayList<>();
        if (mApplication != null) {
            sAccount = mApplication.getAccount();
            access_token = sAccount.getAccess_token();
        }
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_friend = (LRecyclerView) findViewById(R.id.rv_friend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_friend.setLayoutManager(layoutManager);
        iv_set.setOnClickListener(this);
        iv_set.setImageResource(R.drawable.hp_chat_more);
        iv_set.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.item_friend_circle_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        ImageView iv_today = (ImageView) header.findViewById(R.id.iv_today);
        ll_personal = (LinearLayout) header.findViewById(R.id.ll_personal);
        tv_signature = (TextView) header.findViewById(R.id.tv_signature);
        iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        ImageView iv_head = (ImageView) header.findViewById(R.id.iv_head);
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context) / 3 * 2;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
        if (mPreferences != null) {
            String albumCover = mPreferences.getString("albumCover", "");
            if (!TextUtils.isEmpty(albumCover)) {
                imageLoader.displayImage(albumCover, iv_bg,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
        }

        String avatar = App.getInstance().getAccount().getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            imageLoader.displayImage(avatar, iv_head,
                    ImageLoaderUtils.getDisplayImageOptions());
        }
        ll_personal.setVisibility(View.VISIBLE);
        tv_signature.setVisibility(View.VISIBLE);

        adapter = new PersonalPhotoAlbumAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        rv_friend.addOnItemTouchListener(new PersonalPhotoAlbumAdapter.RecyclerItemClickListener(this,
                new PersonalPhotoAlbumAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position > 1) {
                            List<FriendCircle> list = adapter.getDatas();
                            if (list != null) {
                                int size = list.size();
                                int curPos = position - 2;
                                if (curPos >= 0 && curPos < size) {
                                    FriendCircle item = list.get(curPos);
                                    Bundle bundle = new Bundle();
                                    if (item != null) {
                                        List<PhotoInfo> files = item.getFiles();
                                        if (files != null && files.size() > 0) {
                                            int feedType = item.getFeed_type();
                                            String file = files.get(0).file_url;
                                            if (feedType == 1) {
                                                if (TextUtils.isEmpty(file)) {
                                                    isJumpLookPic = false;
                                                } else if (!file.contains(".jpg") && !file.contains(".png")) {
                                                    isJumpLookPic = false;
                                                } else {
                                                    isJumpLookPic = true;
                                                }
                                            } else if (feedType == 2) {
                                                if (TextUtils.isEmpty(file)) {
                                                    isJumpLookPic = false;
                                                } else if (!file.contains(".mp4")) {
                                                    isJumpLookPic = false;
                                                } else {
                                                    isJumpLookPic = true;
                                                }
                                            } else {
                                                isJumpLookPic = false;
                                            }
                                        }
                                        if (isJumpLookPic) {
                                            bundle.putInt("feedId", item.getFeed_id());
                                            openActvityForResult(PhotoAlbumLookPicActivity.class, 101, bundle);
                                        } else {
                                            String username = sAccount.getUser_name();
                                            if (!TextUtils.isEmpty(username)) {
                                                bundle.putString("username", username);
                                                bundle.putInt("feedId", item.getFeed_id());
                                                openActvityForResult(PhotoAlbumDetailActivity.class, 101, bundle);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                        Log.d("addOnItemTouchListener", "onLongClick position : " + posotion);
                    }
                }));
        rv_friend.setAdapter(mLRecyclerViewAdapter);
        iv_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeIcon = true;
                showActionSheet(0);
            }
        });
        iv_today.setOnClickListener(this);
        rv_friend.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getServerGallery();
            }
        });
        rv_friend.setPullRefreshEnabled(false);
        getServerGallery();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                isChangeIcon = false;
                showActionSheet(0);
                break;
            case R.id.iv_today:
                showActionSheet(1);
                break;
        }
    }

    private void showActionSheet(int type) {
        ActionSheet actionSheet = new ActionSheet(PersonalPhotoAlbumActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (type == 0) {
            isSendNew = false;
            if (isChangeIcon) {
                actionSheet.addSheetItem(getString(R.string.replace_the_album_cover), ActionSheet.SheetItemColor.Black, PersonalPhotoAlbumActivity.this);
            } else {
                actionSheet.addSheetItem(getString(R.string.message_list), ActionSheet.SheetItemColor.Black, PersonalPhotoAlbumActivity.this);
            }
        } else {
            isSendNew = true;
            actionSheet.addSheetItem(getString(R.string.small_video), ActionSheet.SheetItemColor.Black,
                    PersonalPhotoAlbumActivity.this)
                    .addSheetItem(getString(R.string.take_photos), ActionSheet.SheetItemColor.Black,
                            PersonalPhotoAlbumActivity.this)
                    .addSheetItem(getString(R.string.from_the_mobile_phone_photo_album_choice), ActionSheet.SheetItemColor.Black,
                            PersonalPhotoAlbumActivity.this);
        }
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        if (isSendNew) {
            switch (which) {
                case 1://1小视频 or 2更换相册封面 or 3消息列表 or 4 删除评论
                    cameraType = RECORD_VIDEO;
                    getPermission();
                    break;
                case 2://拍照
                    cameraType = OPEN_CAMERA;
                    getPermission();
                    break;
                case 3://从手机相册选择
                    cameraType = OPEN_ALBUM;
                    getPermission();
                    break;
            }
        } else {
            if (isChangeIcon) {
                openActvityForResult(ReplaceAlbumCoverActivity.class, ALBUM_COVER);
            } else {
                openActivity(CircleMessageListActivity.class);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALBUM_COVER && resultCode == RESULT_OK && data != null) {
            String albumCover = data.getStringExtra("icon_url");
            if (!TextUtils.isEmpty(albumCover)) {
                imageLoader.displayImage(albumCover, iv_bg,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
        } else if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {//打开相机
            if (imageUri != null) {
                String path = Utils.getPath(this, imageUri);
                Bundle bundle = new Bundle();
                bundle.putString("sendType", "photo");
                bundle.putInt("type", OPEN_CAMERA);
                bundle.putString("img", path);
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK && data != null) { //打开相册
            ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            Bundle bundle = new Bundle();
            bundle.putInt("type", OPEN_ALBUM);
            bundle.putString("sendType", "photo");
            bundle.putStringArrayList("imglist", selectPicList);
            openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
        } else if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {
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
                String videoPath = tmpFile.getAbsolutePath();

                Bundle bundle = new Bundle();
                bundle.putInt("type", RECORD_VIDEO);
                bundle.putString("sendType", "video");
                bundle.putString("videoPath", videoPath);
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SEND_TYPE_PHOTO_TX && resultCode == RESULT_OK) {
            isAddNew = true;
            getServerGallery();
        }
    }

    private void getServerGallery() {
        showLoadingDialog();
        String url = HttpUrl.GET_PERSONAL_GALLERY;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        if (isAddNew) {
            int allsize = (page + 1) * 20;
            url = url + "?page=" + 0 + "&size=" + allsize;
        } else {
            url = url + "?page=" + page + "&size=" + 20;
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    FriendCircleEntity data = JSON.parseObject(result, FriendCircleEntity.class);
                    if (data != null) {
                        if (data.getCode() == 1) {
                            List<FriendCircle> list = data.getResult();
                            if (list != null && list.size() > 0) {
                                showData(list);
                            }
                        } else {
                            showToast(data.getMessage(), false);
                        }
                    } else {
                        showToast("没有更多数据了", false);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast("获取数据失败", false);
            }
        });
    }

    private void showData(List<FriendCircle> list) {
        isAddNew = false;
        int size = list.size();
        if (size == 20) {
            page++;
        } else if (size < 20) {
            rv_friend.setNoMore(true);
        }
        allList.addAll(list);
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int all_size = allList.size();
        int lastYear = 0, firstYear = 0;
        FriendCircle firstItem = allList.get(0);
        if (firstItem != null) {
            String time = firstItem.getAdd_time();
            String[] timeArr = TimeUtil.timestamp(time);
            if (timeArr.length > 0) {
                firstYear = Integer.parseInt(timeArr[0]);
            }
        }
        FriendCircle lastItem = allList.get(all_size - 1);
        if (lastItem != null) {
            String time = lastItem.getAdd_time();
            String[] timeArr = TimeUtil.timestamp(time);
            if (timeArr.length > 0) {
                lastYear = Integer.parseInt(timeArr[0]);
            }
        }

        if (firstYear > 0 && lastYear > 0) {
            List<FriendCircle> itemList = new ArrayList<>();
            for (int i = firstYear; i > lastYear - 1; i--) {
                if (i != curYear) {
                    FriendCircle item1 = new FriendCircle();
                    item1.setParent(true);
                    item1.setAdd_time("" + i);
                    itemList.add(item1);
                }
                for (int j = 0; j < all_size; j++) {
                    FriendCircle item = allList.get(j);
                    if (item != null) {
                        String time = item.getAdd_time();
                        String[] timeArr = TimeUtil.timestamp(time);
                        if (timeArr.length > 2) {
                            int year = Integer.parseInt(timeArr[0]);
                            String month = timeArr[1];
                            String day = timeArr[2];
                            item.setTimeMonth(month);
                            item.setTimeDay(day);
                            Log.i("year：", "" + curYear);
                            if (year == i) {
                                itemList.add(item);
                            }
                        }
                    }
                }
            }
            adapter.setDatas(itemList);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    // 删除在/sdcard/dcim/Camera/默认生成的文件
    private void deleteDefaultFile(Uri uri) {
        String fileName = null;
        if (uri != null) {
            // content
            Log.d("Scheme", uri.getScheme());
            if (uri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor != null) {
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
                    cursor.close();
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

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == RECORD_VIDEO) {
                    Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                    startActivityForResult(mIntent, RECORD_VIDEO);
                    //openActivity(RecordVideoActivity.class);
                } else if (cameraType == OPEN_CAMERA) {
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
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(9)
                            .multi() // 多选模式, 默认模式;
                            .start(PersonalPhotoAlbumActivity.this, OPEN_ALBUM);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(PersonalPhotoAlbumActivity.this, getString(R.string.giving_camera_permissions));
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
