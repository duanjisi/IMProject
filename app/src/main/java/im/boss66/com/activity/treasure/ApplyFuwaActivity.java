package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * Created by liw on 2017/4/5.
 */
public class ApplyFuwaActivity extends BaseActivity implements View.OnClickListener, OnWheelChangedListener {
    private EditText et_name, et_phone, et_num, et_use;
    private ImageView img_person, img_company;
    private TextView tv_where;
    private int applyType = 1;

    private Dialog dialog;
    private WheelView mViewProvince;
    private WheelView mViewCity;

    private TextView mBtnConfirm;
    private TextView btn_cancle;

    protected String[] mProvinceDatas;//所有省

    private LocalAddressEntity jsonDate;

    private List<LocalAddressEntity.ThreeChild> list1;       //省
    private List<LocalAddressEntity.FourChild> cityList;      //市

    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;

    private Dialog dialog2;
    private TextView tv_cancel;
    private TextView tv_submit;

    private boolean flag;

    private boolean chooseLocation;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    dialog2.dismiss();
                    showToast("提交成功",false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1000);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_fuwa);

        initViews();
        initCityData();
    }

    private void initViews() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_num = (EditText) findViewById(R.id.et_num);
        et_use = (EditText) findViewById(R.id.et_use);
        tv_where = (TextView) findViewById(R.id.tv_where);

        findViewById(R.id.rl_where).setOnClickListener(this);
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        findViewById(R.id.tv_comfire).setOnClickListener(this);
        img_person = (ImageView) findViewById(R.id.img_person);
        img_company = (ImageView) findViewById(R.id.img_company);

        img_person.setOnClickListener(this);
        img_company.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_where:
                showAddressSelection();

                break;

            case R.id.tv_headlift_view:
                if(dialog2==null){
                    flag=false;
                    showDialog(ApplyFuwaActivity.this);
                }else if(!dialog2.isShowing()){
                    flag=false;
                    setContent();
                    dialog2.show();
                }
                break;

            case R.id.img_person:
                applyType = 0;
                img_company.setImageResource(R.drawable.choose_btn);
                img_person.setImageResource(R.drawable.choose_click_btn);
                break;

            case R.id.img_company:
                applyType = 1;
                img_person.setImageResource(R.drawable.choose_btn);
                img_company.setImageResource(R.drawable.choose_click_btn);

                break;
            case R.id.tv_comfire:
                if(!TextUtils.isEmpty(et_name.getText())
                        &&!TextUtils.isEmpty(et_phone.getText())
                        &&!TextUtils.isEmpty(et_use.getText())
                        &&!TextUtils.isEmpty(et_num.getText())
                        &&chooseLocation){

                    if(dialog2==null){
                        flag=true;
                        showDialog(ApplyFuwaActivity.this);
                    }else if(!dialog2.isShowing()){
                        flag=true;
                        setContent();
                        dialog2.show();
                    }
                }else{

                    showToast("请完善资料",false);
                }


                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(dialog2==null){
//            flag=false;
//            showDialog(ApplyFuwaActivity.this);
//        }else if(!dialog2.isShowing()){
//            flag=false;
//            setContent();
//            dialog2.show();
//        }
    }

    private void showDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_apply, null);

        dialog2 = new Dialog(context, R.style.dialog_ios_style);
        dialog2.setContentView(view);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_submit = (TextView) view.findViewById(R.id.tv_submit);
        setContent();

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog2.dismiss();
                if(!flag){
                    finish();
                }
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(et_name.getText())
                        &&!TextUtils.isEmpty(et_phone.getText())
                        &&!TextUtils.isEmpty(et_use.getText())
                        &&!TextUtils.isEmpty(et_num.getText())
                        &&chooseLocation){

                    applyFuwa();
                }else{
                    showToast("请完善资料",false);
                }

            }
        });

        //设置dialog大小
        Window dialogWindow = dialog2.getWindow();
        WindowManager manager = ((ApplyFuwaActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        params.height = (int) (params.width * 0.7); //
        dialogWindow.setAttributes(params);

        dialog2.show();
    }

    private void setContent() {
        if(flag){
            tv_submit.setText("确认");
            tv_cancel.setText("取消");
        }else{
            tv_submit.setText("提交并返回");
            tv_cancel.setText("直接返回");
        }

    }

    private void applyFuwa() {

        String url = HttpUrl.APPLY_FUWA +"?userid="+ App.getInstance().getUid()
                +"&name="+et_name.getText().toString()+"&phone="+et_phone.getText().toString()
                +"&shop="+applyType+"&purpose="+et_use.getText().toString()+"&region="+mCurrentProviceName+""+mCurrentCityName
                +"&number="+et_num.getText().toString()+"&time="+System.currentTimeMillis();

        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if(jsonObject.getInt("code")==0){
                        handler.obtainMessage(1).sendToTarget();
                    }else{
                        showToast("申请失败",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
                showToast(e.getMessage(),false);
            }
        });

    }

    private void showAddressSelection() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.Dialog_full);
            View view_dialog = View.inflate(this,
                    R.layout.dialog_shipping_address_select2, null);
            // 省
            mViewProvince = (WheelView) view_dialog
                    .findViewById(R.id.id_province);
            // 市
            mViewCity = (WheelView) view_dialog.findViewById(R.id.id_city);

            mBtnConfirm = (TextView) view_dialog.findViewById(R.id.btn_confirm);
            btn_cancle = (TextView) view_dialog.findViewById(R.id.btn_cancle);
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                }
            });
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewCity.setVisibleItems(7);
            // 添加change事件
            mViewProvince.addChangingListener(this);
            // 添加change事件
            mViewCity.addChangingListener(this);

            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseLocation = true;
                    dialog.dismiss();
                    tv_where.setText(mCurrentProviceName+"  "+mCurrentCityName);
                    tv_where.setTextColor(Color.BLACK);
                }
            });
            dialog.setContentView(view_dialog);

            Window dialogWindow = dialog.getWindow();
            dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
        setUpData();
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<>(this,
                mProvinceDatas));

        updateCities();
        updateAreas();
    }


    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        cityList = list1.get(pCurrent).getList();
        mCurrentProviceName = mProvinceDatas[pCurrent];

        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
    }


    protected void initProvinceDatas() {
        LocalAddressEntity.SecondChild result = jsonDate.getResult();
        list1 = result.getList();
        mProvinceDatas = new String[list1.size()];

        for (int i = 0; i < list1.size(); i++) {
            mProvinceDatas[i] = list1.get(i).getRegion_name(); //省

            cityList = list1.get(i).getList();
            String[] cityNames = new String[cityList.size()];
            for (int j = 0; j < cityList.size(); j++) {
                // 遍历省下面的所有市的数据
                cityNames[j] = cityList.get(j).getRegion_name();     //市
            }
            mCitisDatasMap.put(list1.get(i).getRegion_name(), cityNames);

        }


    }

    private LocalAddressEntity initCityData() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("province.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            jsonDate = JSON.parseObject(stringBuilder.toString(), LocalAddressEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonDate;

    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            int pCurrent = mViewCity.getCurrentItem();
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        }
    }

}
