package im.boss66.com.activity.connection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    private String person_id;
    private String person_name;
    private String person_desc;
    private String person_photo;

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
                if(bundle.containsKey("person_id")){
                    person_id = bundle.getString("person_id");
                    person_name = bundle.getString("person_name");
                    person_desc = bundle.getString("person_desc");
                    person_photo = bundle.getString("person_photo");

                }
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

        if(person_id==null){
            tv_headcenter_view.setText("添加名人");
        }else {
            tv_headcenter_view.setText("编辑名人");

        }
        tv_headlift_view.setOnClickListener(this);

        findViewById(R.id.rl_img).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

        img_headimg = (ImageView) findViewById(R.id.img_headimg);
        if(person_id!=null){
            et_name.setText(person_name);
            et_info.setText(person_desc);
            Glide.with(context).load(person_photo).into(img_headimg);
        }

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
                if(person_id==null){
                    if(name.length()>0&&info.length()>0&&imgUrl.length()>0){

                        saveData(name, info);
                    }else {
                        ToastUtil.showShort(context,"请完善资料");
                    }

                }else{
                    if(name.length()>0&&info.length()>0){ //编辑名人可以不传图片
                        saveData(name, info);
                    }else {
                        ToastUtil.showShort(context,"请完善资料");
                    }
                }

                break;

        }

    }


    private void saveData(String name, String info) {


        showLoadingDialog();
        if(person_id==null){ //添加名人
            if (isClan) {
                main = HttpUrl.ADD_CLAN_PERSON;
            } else {
                main = HttpUrl.ADD_COFC_PERSON;
            }
        }else{    //编辑名人
            if (isClan) {
                main = HttpUrl.UPDATA_CLAN_PERSON;
            } else {
                main = HttpUrl.UPDATA_COFC_PERSON;
            }
        }

        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        if(person_id==null){ //添加名人
            Bitmap bitmap = FileUtils.compressImageFromFile(imgUrl, 1080);
            Log.i("uploadImageFile", imgUrl);
            if (bitmap != null) {
                File file = FileUtils.compressImage(bitmap);
                if (file != null) {
                    params.addBodyParameter("photo", file);
                }
            }
        }else{   //编辑名人
            if(!TextUtils.isEmpty(imgUrl)){ //如果imgurl不为"" 就上传
                Bitmap bitmap = FileUtils.compressImageFromFile(imgUrl, 1080);
                Log.i("uploadImageFile", imgUrl);
                if (bitmap != null) {
                    File file = FileUtils.compressImage(bitmap);
                    if (file != null) {
                        params.addBodyParameter("photo", file);
                    }
                }

            }
        }

        params.addBodyParameter("access_token", access_token);
        if(person_id==null){ //添加名人
            if (isClan) {
                params.addBodyParameter("clan_id", id);
            } else {
                params.addBodyParameter("cofc_id", id);
            }
        }else{ //编辑名人
                params.addBodyParameter("cele_id", person_id);
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


                            ToastUtil.showShort(context, "保存成功");
                            Intent intent = new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            ToastUtil.showShort(context, "保存失败");
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        ToastUtil.showShort(context, "保存失败");
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
                    showToast("保存失败", false);
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
