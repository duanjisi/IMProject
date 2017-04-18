package im.boss66.com.activity.connection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONObject;

import java.io.File;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.activity.event.EditWeb;
import im.boss66.com.entity.EditClanCofcEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by liw on 2017/4/18.
 */
public class EditClanCofcPersonActivity extends ABaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private boolean isClan;
    private String id;
    private ImageView img_headimg;
    private String main;
    private String imgUrl = "";
    private String access_token;
    private EditText et_name;
    private EditText et_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isClan = bundle.getBoolean("isClan", false);
                id = bundle.getString("id");
            }
        }
        initViews();
    }

    private void initViews() {
        access_token = App.getInstance().getAccount().getAccess_token();
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        et_name = (EditText) findViewById(R.id.et_name);
        et_info = (EditText) findViewById(R.id.et_info);

        tv_headcenter_view.setText("添加名人");
        tv_headlift_view.setOnClickListener(this);

        findViewById(R.id.rl_img).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        img_headimg = (ImageView) findViewById(R.id.img_headimg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.rl_img:
                showActionSheet();

                break;
            case R.id.tv_save:
                String name = et_name.getText().toString();
                String info = et_info.getText().toString();
                if(name.length()>0&&info.length()>0&&imgUrl.length()>0){
                    saveData(name, info);
                }else {
                    ToastUtil.showShort(context,"请完善资料");
                }

                break;

        }

    }

    private void saveData(String name, String info) {


        showLoadingDialog();
        if (isClan) {
            main = HttpUrl.ADD_CLAN_PERSON;
        } else {
            main = HttpUrl.ADD_COFC_PERSON;
        }
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        Bitmap bitmap = FileUtils.compressImageFromFile(imgUrl, 1080);
        Log.i("uploadImageFile", imgUrl);
        if (bitmap != null) {
            File file = FileUtils.compressImage(bitmap);
            if (file != null) {
                params.addBodyParameter("photo", file);
            }
        }
        params.addBodyParameter("access_token", access_token);
        if (isClan) {
            params.addBodyParameter("clan_id", id);
        } else {
            params.addBodyParameter("cofc_id", id);
        }
        params.addBodyParameter("name", name);
        params.addBodyParameter("desc", info);

        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;

                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {

                            ToastUtil.showShort(context, "添加成功");
                            Intent intent = new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            ToastUtil.showShort(context, "添加失败");
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        ToastUtil.showShort(context, "添加失败");
                    }

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast("添加失败", false);
                }
            }
        });

    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("更改头像", ActionSheet.SheetItemColor.Black, this);

        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromEditPerson", true);
                bundle.putBoolean("isClan", isClan);
                bundle.putString("id", id);
                openActvityForResult(ReplaceAlbumCoverActivity.class, 1, bundle);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imgUrl = data.getStringExtra("imgUrl");
            Glide.with(context).load(imgUrl).into(img_headimg);
        }
    }


    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }
}
