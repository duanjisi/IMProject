package im.boss66.com.activity.connection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.SaveSchoolEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.EditSchoolRequest;
import im.boss66.com.http.request.SaveSchoolRequest;
import im.boss66.com.widget.dialog.CancelEditDialog;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * Created by admin on 2017/3/2.
 */
public class EditSchoolActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = EditSchoolActivity.class.getSimpleName();
    private TextView tv_headright_view, tv_headlift_view;
    private boolean isUniversity; //是否大学

    private EditText tv_school_department;
    private TextView tv_year;
    private TextView tv_school_name;


    private String[] years;
    private CancelEditDialog dialog;
    private String schoolname;
    private int schoolType; //学校类别,后台接口要
    private int schoolId;     //学校id

    private Dialog dialog2;
    private WheelView id_sex;
    private TextView mBtnConfirm2;
    private TextView btn_cancle2;
    private String year;
    private String majorInfo; //学院
    private SaveSchoolEntity saveSchoolEntity;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(saveSchoolEntity.getCode()==1){
                        SharedPreferencesMgr.setBoolean("EditSchool",true);
                        finish();
                    }else{
                        showToast("添加失败",false);
                    }
                    break;
                case 2:
                    SharedPreferencesMgr.setBoolean("EditSchool",true);
                    finish();
                    break;

            }
        }
    };
    private boolean flag; //是否携带学校info过来
    private int us_id; //编辑学校接口需要参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_school);
        Intent intent = getIntent();
        if (intent != null) {
            flag =intent.getExtras().containsKey("SchoolListActivity");
            if(flag){
                schoolname = intent.getStringExtra("schoolName");
                majorInfo = intent.getStringExtra("schoolNote");
                year = intent.getStringExtra("schoolyear");

                schoolId = intent.getIntExtra("schoolId", -1);
                schoolType = intent.getIntExtra("schoolType",-1);
                us_id = intent.getIntExtra("us_id", -1);
            }else{
                isUniversity = intent.getBooleanExtra("isUniversity", false);
                schoolType = intent.getIntExtra("schoolType", -1);
            }
        }
        initTime();
        initViews();

    }

    private void initTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String formatDate = sdf.format(date);
        years=new String[Integer.parseInt(formatDate)-1900+1];
        for (int i = Integer.parseInt(formatDate); i >= 1900; i--) {
            years[Integer.parseInt(formatDate)-i] = i+"";
        }

    }

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);
        findViewById(R.id.rl_school).setOnClickListener(this);
        RelativeLayout rl_school_department = (RelativeLayout) findViewById(R.id.rl_school_department);
        if(!flag){
            if (!isUniversity) {
                rl_school_department.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.rl_school_department).setOnClickListener(this);
        findViewById(R.id.rl_school_time).setOnClickListener(this);
        tv_school_department = (EditText) findViewById(R.id.tv_school_department);
        Editable ea = tv_school_department.getText();
        tv_school_department.setSelection(ea.length());
        tv_school_department.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                majorInfo = editable.toString();
            }
        });
        tv_school_name = (TextView) findViewById(R.id.tv_school_name);
        tv_year = (TextView) findViewById(R.id.tv_year);

        if(flag){
            tv_school_name.setText(schoolname);
            tv_school_name.setTextColor(Color.BLACK);
            tv_school_department.setText(majorInfo);
            tv_year.setText(year+"年入学");
            tv_year.setTextColor(Color.BLACK);
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view: //完成

                if (!"请选择".equals(tv_year.getText().toString())
                        && !TextUtils.isEmpty(tv_school_department.getText().toString())
                        && !"请填写".equals(tv_school_name.getText().toString())) {
                    if(flag){
                        editSchoolInfo();
                    }else{
                        saveSchoolInfo();
                    }

                } else {
                    ToastUtil.showShort(context,"您填写的信息不全");
                }

                break;

            case R.id.rl_school:
                Intent intent = new Intent(this, SearchSchoolActivity.class);
                intent.putExtra("schoolType",schoolType);

                startActivityForResult(intent, 1);
                break;

            case R.id.rl_school_time:
                showAddressSelection();

                break;


        }
    }

    private void editSchoolInfo() {
        showLoadingDialog();
        String url = HttpUrl.EDIT_SCHOOL_INFO;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        params.addBodyParameter("school_id",schoolId+"");
        params.addBodyParameter("note",majorInfo);
        params.addBodyParameter("edu_year",year);
        url = url+"?us_id=" + us_id;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;

                try {
                    if(result!=null){
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt("code")==1){
                            handler.obtainMessage(2).sendToTarget();
                        }else{
                            ToastUtil.show(EditSchoolActivity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(e.getMessage(), false);
            }
        });
    }

    private void showAddressSelection() {
        if (dialog2 == null) {
            dialog2 = new Dialog(context, R.style.Dialog_full);
            View view_dialog = View.inflate(this,
                    R.layout.dialog_single_select, null);

            id_sex = (WheelView) view_dialog.findViewById(R.id.id_sex);
            mBtnConfirm2  = (TextView) view_dialog.findViewById(R.id.btn_confirm2);
            btn_cancle2  = (TextView) view_dialog.findViewById(R.id.btn_cancle2);

            // 设置可见条目数量
            id_sex.setVisibleItems(7);

            // 添加change事件
            id_sex.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    int currentItem = id_sex.getCurrentItem();
                    year = years[currentItem];

                }
            });

            // 添加onclick事件
            mBtnConfirm2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                    tv_year.setText(year);
                    tv_year.setTextColor(Color.BLACK);

                }
            });
            btn_cancle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });
            dialog2.setContentView(view_dialog);
            Window dialogWindow = dialog2.getWindow();

            dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent); //加上可以在底部对其屏幕底部

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
            dialog2.setCanceledOnTouchOutside(true);
        }
        dialog2.show();
        id_sex.setViewAdapter(new ArrayWheelAdapter<>(this,years));
        int currentItem = id_sex.getCurrentItem();
        year= years[currentItem];

    }

    private void saveSchoolInfo() {
        showLoadingDialog();
        SaveSchoolRequest request = new SaveSchoolRequest(TAG, schoolType + "", year, schoolId + "", majorInfo);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String str) {
                cancelLoadingDialog();
                saveSchoolEntity = JSON.parseObject(str, SaveSchoolEntity.class);
                if(saveSchoolEntity!=null){

                    if(saveSchoolEntity.getCode()==1){

                        handler.obtainMessage(1).sendToTarget();
                    }else{
                        showToast(saveSchoolEntity.getMessage(),false);
                    }
                }

            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg,false);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            schoolname = data.getStringExtra("schoolName");
            schoolId = Integer.parseInt(data.getStringExtra("schoolId"));
            tv_school_name.setText(schoolname);
            tv_school_name.setTextColor(Color.BLACK);
            schoolId=17;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (!"请选择".equals(tv_year.getText().toString())
                || !"请填写".equals(tv_school_name.getText().toString())) {
            if (dialog == null) {
                dialog = new CancelEditDialog(this);
                dialog.show();
            } else if (!dialog.isShowing()) {
                dialog.show();
            }
        } else {
            finish();
        }

    }

}
