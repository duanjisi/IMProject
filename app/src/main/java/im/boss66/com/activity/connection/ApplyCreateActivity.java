package im.boss66.com.activity.connection;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.event.CreateSuccess;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.CreateClanRequest;
import im.boss66.com.http.request.CreateClubRequest;
import im.boss66.com.http.request.CreateTribeRequest;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * 申请宗亲，商会
 * Created by liw on 2017/4/13.
 */
public class ApplyCreateActivity extends ABaseActivity implements View.OnClickListener, OnWheelChangedListener, ActionSheet.OnSheetItemClickListener {

    private final static String TAG = ApplyCreateActivity.class.getSimpleName();

    private Dialog dialog;
    private Dialog Typedialog;
    private TextView tv_type;
    private EditText et_name;
    private TextView tv_area;
    private TextView tv_create;

    private LocalAddressEntity jsonDate;


    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;


    private List<LocalAddressEntity.ThreeChild> proviceList;       //省
    private List<LocalAddressEntity.FourChild> cityList;      //市
    private List<LocalAddressEntity.LastChild> districtList;   //县

    protected String[] mProvinceDatas;//所有省名字
    protected String[] mProvinceDatasId;//所有省id

    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mCitisDatasMap2 = new HashMap<String, String[]>();

    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictDatasMap2 = new HashMap<String, String[]>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";

    private String province_id;
    private String city_id;
    private String county_id;
    private String name;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_create);
        Intent intent = getIntent();
        if(intent!=null){
            from = intent.getIntExtra("from", -1);
        }

        initViews();
        initCityData();
    }


    private void initViews() {

        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("创建");

        RelativeLayout rl_choose = (RelativeLayout) findViewById(R.id.rl_choose);
        if(from==-1){
            rl_choose.setOnClickListener(this);
        }

        tv_type = (TextView) findViewById(R.id.tv_type);
        if(from==2){
            tv_type.setText("商会");
        }
        et_name = (EditText) findViewById(R.id.et_name);
        Editable ea = et_name.getText();
        et_name.setSelection(ea.length());
        findViewById(R.id.rl_area).setOnClickListener(this);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_create  = (TextView) findViewById(R.id.tv_create);
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) tv_create.getLayoutParams();
        params.width= UIUtils.getScreenWidth(this)*9/10;
        tv_create.setLayoutParams(params);
        tv_create.setOnClickListener(this);

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
    private long time1;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.rl_choose:
                showActionSheet();

                break;
            case R.id.rl_area:

                showAddressSelection();
                break;

            case R.id.tv_create:

                long time2 = System.currentTimeMillis();
                if(time2-time1<1000){
                    return;
                }
                name = et_name.getText().toString();
                String type = tv_type.getText().toString();
                String area = tv_area.getText().toString();
                //都不为空
                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(area)){
                    if("宗亲".equals(type)){
                        //创建宗亲
                        createClan();

                    }else if("商会".equals(type)){
                        //创建商会
                        createClub();
                    }else{
                        //创建部落
                        createTribe();
                    }
                }else{
                    ToastUtil.showShort(context,"请完善资料");
                }
                time1 = time2;
                break;

        }
    }

    private void createTribe() {
        CreateTribeRequest request = new CreateTribeRequest(TAG,name,province_id,city_id,county_id);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if(TextUtils.isEmpty(pojo)){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(pojo);
                    if(jsonObject.getInt("code")==1){
                        showToast("创建成功",false);
                        EventBus.getDefault().post(new CreateSuccess(""));
                        finish();
                    }else{
                        showToast("创建失败",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast("创建失败",false);

            }
        });

    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
            actionSheet.addSheetItem("宗亲", ActionSheet.SheetItemColor.Black,this)
                    .addSheetItem("商会", ActionSheet.SheetItemColor.Black,this)
                    .addSheetItem("部落", ActionSheet.SheetItemColor.Black,this);;

        actionSheet.show();
    }

    private void createClub() {
        CreateClubRequest request = new CreateClubRequest(TAG,name,province_id,city_id,county_id);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if(TextUtils.isEmpty(pojo)){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(pojo);
                    if(jsonObject.getInt("code")==1){
                        showToast("创建成功",false);
                        EventBus.getDefault().post(new CreateSuccess(""));
                        finish();
                    }else{
                        showToast("创建失败",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast("创建失败",false);

            }
        });

    }

    private void createClan() {
        CreateClanRequest request = new CreateClanRequest(TAG,name,province_id,city_id,county_id);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if(TextUtils.isEmpty(pojo)){
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(pojo);
                    if(jsonObject.getInt("code")==1){
                        showToast("创建成功",false);
                        EventBus.getDefault().post(new CreateSuccess(""));
                        finish();
                    }else{
                        showToast("创建失败",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast("创建失败",false);

            }
        });

    }

    private void showAddressSelection() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.Dialog_full);
            View view_dialog = View.inflate(this,
                    R.layout.dialog_shipping_address_select, null);
            // 省
            mViewProvince = (WheelView) view_dialog
                    .findViewById(R.id.id_province);
            // 市
            mViewCity = (WheelView) view_dialog.findViewById(R.id.id_city);
            // 区或县
            mViewDistrict = (WheelView) view_dialog.findViewById(R.id.id_district);
            TextView btn_cancle = (TextView) view_dialog.findViewById(R.id.btn_cancle);
            TextView mBtnConfirm = (TextView) view_dialog.findViewById(R.id.btn_confirm);
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            // 添加onclick事件
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("liwya",province_id+"-"+city_id+"-"+county_id);
                    tv_area.setText(mCurrentProviceName+"  "+mCurrentCityName+"  "+mCurrentDistrictName);
                    dialog.dismiss();
                }
            });
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewCity.setVisibleItems(7);
            mViewDistrict.setVisibleItems(7);
            // 添加change事件
            mViewProvince.addChangingListener(this);
            // 添加change事件
            mViewCity.addChangingListener(this);
            // 添加change事件
            mViewDistrict.addChangingListener(this);

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
        int pCurrent = mViewProvince.getCurrentItem(); //拿到位置
        cityList = proviceList.get(pCurrent).getList();
        mCurrentProviceName = mProvinceDatas[pCurrent];    //通过位置拿到name
        province_id = mProvinceDatasId[pCurrent];   //拿到id

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
        districtList = cityList.get(pCurrent).getList();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        city_id = mCitisDatasMap2.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(
                this, areas));
        mViewDistrict.setCurrentItem(0);
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        county_id = mDistrictDatasMap2.get(mCurrentCityName)[0];
    }


    protected void initProvinceDatas() {
        LocalAddressEntity.SecondChild result = jsonDate.getResult();
        proviceList = result.getList();
        mProvinceDatas = new String[proviceList.size()];

        mProvinceDatasId = new String[proviceList.size()];
        for (int i = 0; i < proviceList.size(); i++) {
            mProvinceDatas[i] = proviceList.get(i).getRegion_name(); //省
            mProvinceDatasId[i] = proviceList.get(i).getRegion_id();

            cityList = proviceList.get(i).getList();
            String[] cityNames = new String[cityList.size()];
            String[] cityIds = new String[cityList.size()];
            for (int j = 0; j < cityList.size(); j++) {
                // 遍历省下面的所有市的数据
                cityNames[j] = cityList.get(j).getRegion_name();     //市
                cityIds[j] = cityList.get(j).getRegion_id();     //市

                //县
                districtList = cityList.get(j).getList();

                String[] distrinctArray = new String[districtList
                        .size()];
                String[] distrinctIds = new String[districtList
                        .size()];
                for (int k = 0; k < districtList.size(); k++) {
                    // 遍历市下面所有区/县的数据
                    String districtModel = new String(districtList.get(k).getRegion_name());
                    String districtModel2 = new String(districtList.get(k).getRegion_id());

                    distrinctArray[k] = districtModel; // 县
                    distrinctIds[k] = districtModel2; // 县

                }
                // 市-区/县的数据，保存到mDistrictDatasMap
                mDistrictDatasMap.put(cityNames[j], distrinctArray);
                mDistrictDatasMap2.put(cityNames[j], distrinctIds);
            }
            mCitisDatasMap.put(proviceList.get(i).getRegion_name(), cityNames);
            mCitisDatasMap2.put(proviceList.get(i).getRegion_name(), cityIds);

        }


    }



    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            county_id = mDistrictDatasMap2.get(mCurrentCityName)[newValue];
        }
    }

    @Override
    public void onClick(int which) {

        switch (which){
            case 1:
                tv_type.setText("宗亲");
                break;
            case 2:
                tv_type.setText("商会");
                break;
            case 3:
                tv_type.setText("部落");
                break;
        }

    }
}
