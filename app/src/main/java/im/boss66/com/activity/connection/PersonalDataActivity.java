package im.boss66.com.activity.connection;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.entity.JobEntity;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChooseJobRequest;
import im.boss66.com.http.request.SaveUserInfoRequest;
import im.boss66.com.widget.dialog.ChooseYourLikeDialog;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * 完善资料
 * Created by admin on 2017/2/20.
 */
public class PersonalDataActivity extends ABaseActivity implements View.OnClickListener, ChooseYourLikeDialog.IFinishCallBack
        , OnWheelChangedListener {

    private final static String TAG = PersonalDataActivity.class.getSimpleName();

    private TextView tv_comfire, tv_sex2, tv_time2, tv_location2, tv_hometown2, tv_job2, tv_like, tv_name2, tv_like2, tv_school2;

    private TextView tv_school3, tv_hometown3; //有数据了隐藏掉


    private LocalAddressEntity jsonDate;


    private int flag = -1; //1 出生年月 2所在地

    private DatePickerDialog datePickerDialog;

    private RelativeLayout rl_sex;
    private RelativeLayout rl_time;
    private RelativeLayout rl_location;
    private RelativeLayout rl_hometown;
    private RelativeLayout rl_job;
    private RelativeLayout rl_like;
    private RelativeLayout rl_school;

    private RelativeLayout rl_name;

    private Dialog dialog; //省市区dialog
    private Dialog dialog2; // 性别dialog


    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;

    private WheelView id_sex;

    private Button mBtnConfirm;
    private Button mBtnConfirm2; //性别选择btn
    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    protected String[] mProvinceDatas2;
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
     * key - 区 values - 邮编
     */
    // protected Map<String, String> mZipcodeDatasMap = new HashMap<String,
    // String>();

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


    private DatePickerDialog.OnDateSetListener DatePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tv_time2.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            String s1 = String.valueOf(year);
            String s2;
            String s3;


            if (monthOfYear > 9) {
                s2 = String.valueOf(monthOfYear);
            } else {
                s2 = "0" + String.valueOf(monthOfYear);
            }
            if (dayOfMonth > 9) {
                s3 = String.valueOf(dayOfMonth);

            } else {
                s3 = "0" + String.valueOf(dayOfMonth);
            }
            String str = s1 + s2 + s3;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            try {
                //时间戳
                millionSecnds = sdf.parse(str).getTime();
                tv_time2.setText(s1 + " " + s2 + " " + s3);
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }
    };
    private List<JobEntity.ResultBean> jobList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showAddressSelection2();


                    break;
            }
        }
    };
    private ChooseYourLikeDialog chooseYourLikeDialog;
    private List<LocalAddressEntity.ThreeChild> list1;       //省
    private List<LocalAddressEntity.FourChild> cityList;      //市
    private List<LocalAddressEntity.LastChild> districtList;   //县
    private String province_id;
    private String province_id2;
    private String city_id;
    private String city_id2;
    private String county_id;
    private String county_id2;


    private JobEntity jobEntity;
    private String[] jobStr; //工作数组

    private boolean isJob;
    private int jobId;
    private String jobName;  //选择的工作
    private String[] sexStr; //性别数组
    private String sexName;  //选择的性别

    private int sexType;
    private long millionSecnds;
    private String youLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        initCityData();
        initViews();
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


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("完善资料");
        tv_headlift_view.setOnClickListener(this);
        tv_comfire = (TextView) findViewById(R.id.tv_comfire);
        tv_comfire.setOnClickListener(this);


        tv_sex2 = (TextView) findViewById(R.id.tv_sex2);
        tv_time2 = (TextView) findViewById(R.id.tv_time2);
        tv_location2 = (TextView) findViewById(R.id.tv_location2);
        tv_hometown2 = (TextView) findViewById(R.id.tv_hometown2);
        tv_job2 = (TextView) findViewById(R.id.tv_job2);
        tv_like = (TextView) findViewById(R.id.tv_like);
        tv_name2 = (TextView) findViewById(R.id.tv_name2);
        tv_like2 = (TextView) findViewById(R.id.tv_like2);
        tv_school2 = (TextView) findViewById(R.id.tv_school2);
        tv_school3 = (TextView) findViewById(R.id.tv_school3);
        tv_hometown3 = (TextView) findViewById(R.id.tv_hometown3);


        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_time = (RelativeLayout) findViewById(R.id.rl_time);
        rl_location = (RelativeLayout) findViewById(R.id.rl_location);
        rl_hometown = (RelativeLayout) findViewById(R.id.rl_hometown);
        rl_job = (RelativeLayout) findViewById(R.id.rl_job);
        rl_like = (RelativeLayout) findViewById(R.id.rl_like);
        rl_school = (RelativeLayout) findViewById(R.id.rl_school);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);

        rl_sex.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        rl_location.setOnClickListener(this);
        rl_hometown.setOnClickListener(this);
        rl_job.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        rl_school.setOnClickListener(this);
        rl_name.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view://头部返回键
                finish();
                break;
            case R.id.tv_comfire: //保存

                if (TextUtils.isEmpty(tv_name2.getText())
                        || TextUtils.isEmpty(tv_sex2.getText())
                        || TextUtils.isEmpty(tv_location2.getText())
                        || TextUtils.isEmpty(tv_hometown2.getText())
                        || TextUtils.isEmpty(tv_job2.getText())
                        || TextUtils.isEmpty(tv_like2.getText())) {

                    showToast("请完善资料", false);
                } else {
                    saveInfo();
                }
                break;

            case R.id.rl_location:
                flag = 4;
                showAddressSelection();
                break;
            case R.id.rl_hometown:
                flag = 5;
                showAddressSelection();
                tv_hometown3.setVisibility(View.GONE);
                break;
            case R.id.rl_sex:
                isJob = false;
                showAddressSelection2();
                break;
            case R.id.rl_time:

                final Calendar objTime = Calendar.getInstance();
                int iYear = objTime.get(Calendar.YEAR);
                int iMonth = objTime.get(Calendar.MONTH);
                int iDay = objTime.get(Calendar.DAY_OF_MONTH);
                if (datePickerDialog == null) {
                    datePickerDialog = new DatePickerDialog(this, R.style.time_style, DatePickerListener, iYear, iMonth, iDay);
                    datePickerDialog.show();
                } else {
                    datePickerDialog.show();
                }
                break;
            case R.id.rl_job:
                isJob = true;
                if (jobList == null) {
                    initJobsData();
                } else {
                    showAddressSelection2();
                }
                break;
            case R.id.rl_like:
                if (chooseYourLikeDialog == null) {
                    chooseYourLikeDialog = new ChooseYourLikeDialog(this);
                    chooseYourLikeDialog.setCallBack(this);
                    chooseYourLikeDialog.show();
                } else if (!chooseYourLikeDialog.isShowing()) {
                    chooseYourLikeDialog.show();
                }


                break;
            case R.id.rl_school:
                Intent intent = new Intent(this, SchoolListActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.rl_name:
                Intent intent1 = new Intent(this, EditNameActivity.class);
                startActivityForResult(intent1, 1);
                break;
            case R.id.btn_confirm:
                showSelectedResult();
                break;
            case R.id.btn_confirm2:
                showSelectedResult2();
                break;

        }

    }

    private void saveInfo() {
        SaveUserInfoRequest request = new SaveUserInfoRequest(TAG,sexType+"",millionSecnds+"",
                province_id+"",city_id+"",county_id+"",province_id2+"",city_id2+"",county_id2+""
        +jobName+youLike);
       request.send(new BaseDataRequest.RequestCallback<String>() {
           @Override
           public void onSuccess(String str) {
               Log.i("liwya",str+1111111);

           }

           @Override
           public void onFailure(String msg) {
               Log.i("liwya",msg+22222222);

           }
       });


    }

    private void showAddressSelection2() {
        if (dialog2 == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                    this);
            View view_dialog = View.inflate(this,
                    R.layout.dialog_single_select, null);

            id_sex = (WheelView) view_dialog.findViewById(R.id.id_sex);
            mBtnConfirm2 = (Button) view_dialog.findViewById(R.id.btn_confirm2);

            // 设置可见条目数量
            id_sex.setVisibleItems(7);

            // 添加change事件
            id_sex.addChangingListener(this);

            // 添加onclick事件
            mBtnConfirm2.setOnClickListener(this);
            dialogBuilder.setView(view_dialog);
            dialog2 = dialogBuilder.create();
            Window dialogWindow = dialog2.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
        }
        dialog2.show();
        setUpData2();
    }

    private void setUpData2() {
        int pCurrent = id_sex.getCurrentItem();
        if (isJob) {
            id_sex.setViewAdapter(new ArrayWheelAdapter<>(this, jobStr));
            jobName = jobStr[pCurrent];
        } else {
            sexStr = new String[]{"男", "女"};
            id_sex.setViewAdapter(new ArrayWheelAdapter<>(this,
                    sexStr));
            sexName = sexStr[pCurrent];
        }
    }

    private void showAddressSelection() {
        if (dialog == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                    this);
            View view_dialog = View.inflate(this,
                    R.layout.dialog_shipping_address_select, null);
            // 省
            mViewProvince = (WheelView) view_dialog
                    .findViewById(R.id.id_province);
            // 市
            mViewCity = (WheelView) view_dialog.findViewById(R.id.id_city);
            // 区或县
            mViewDistrict = (WheelView) view_dialog.findViewById(R.id.id_district);
            mBtnConfirm = (Button) view_dialog.findViewById(R.id.btn_confirm);
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
            // 添加onclick事件
            mBtnConfirm.setOnClickListener(this);
            dialogBuilder.setView(view_dialog);
            dialog = dialogBuilder.create();
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
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
        if(flag==4){
            province_id = mProvinceDatas2[pCurrent];
        }else if(flag==5){
            province_id2 = mProvinceDatas2[pCurrent];

        }
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        districtList = cityList.get(pCurrent).getList();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        if(flag==4){

            city_id = mCitisDatasMap2.get(mCurrentProviceName)[pCurrent];
        }else if(flag==5){
            city_id2 = mCitisDatasMap2.get(mCurrentProviceName)[pCurrent];
        }
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(
                this, areas));
        mViewDistrict.setCurrentItem(0);
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        if(flag==4){
            county_id = mDistrictDatasMap2.get(mCurrentCityName)[0];
        }else if(flag==5){
            county_id2 = mDistrictDatasMap2.get(mCurrentCityName)[0];
        }


    }

    protected void initProvinceDatas() {
        LocalAddressEntity.SecondChild result = jsonDate.getResult();
        list1 = result.getList();
        mProvinceDatas = new String[list1.size()];

        mProvinceDatas2 = new String[list1.size()];
        for (int i = 0; i < list1.size(); i++) {
            mProvinceDatas[i] = list1.get(i).getRegion_name(); //省
            mProvinceDatas2[i] = list1.get(i).getRegion_id();

            cityList = list1.get(i).getList();
            String[] cityNames = new String[cityList.size()];
            String[] cityNames2 = new String[cityList.size()];
            for (int j = 0; j < cityList.size(); j++) {
                // 遍历省下面的所有市的数据
                cityNames[j] = cityList.get(j).getRegion_name();     //市
                cityNames2[j] = cityList.get(j).getRegion_id();     //市

                //县
                districtList = cityList.get(j).getList();

                String[] distrinctArray = new String[districtList
                        .size()];
                String[] distrinctArray2 = new String[districtList
                        .size()];
                for (int k = 0; k < districtList.size(); k++) {
                    // 遍历市下面所有区/县的数据
                    String districtModel = new String(districtList.get(k).getRegion_name());
                    String districtModel2 = new String(districtList.get(k).getRegion_id());

                    distrinctArray[k] = districtModel; // 县
                    distrinctArray2[k] = districtModel2; // 县


                }
                // 市-区/县的数据，保存到mDistrictDatasMap
                mDistrictDatasMap.put(cityNames[j], distrinctArray);
                mDistrictDatasMap2.put(cityNames[j], distrinctArray2);
            }
            mCitisDatasMap.put(list1.get(i).getRegion_name(), cityNames);
            mCitisDatasMap2.put(list1.get(i).getRegion_name(), cityNames2);

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            String name = data.getStringExtra("name");
            tv_name2.setText(name);
        }
        if (requestCode == 2 && data != null) {
            tv_school3.setVisibility(View.GONE);
            tv_school2.setText(data.getStringExtra("school_name"));
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initJobsData() {
        ChooseJobRequest request = new ChooseJobRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String entry) {
                handler.obtainMessage(1).sendToTarget();
                jobEntity = JSON.parseObject(entry, JobEntity.class);
                jobList = jobEntity.getResult();
                jobStr = new String[jobList.size()];

                for (int i = 0; i < jobList.size(); i++) {
                    jobStr[i] = jobList.get(i).getName();
                }

            }


            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void showLike(ArrayList<String> str, ArrayList<Integer> integers) { //保存接口回调,integers为兴趣id集合

        youLike = "";
        for (int i = 0; i < str.size(); i++) {
            String s1 = str.get(i);
            youLike = youLike + s1 + "|";
        }
        youLike = youLike.substring(0, youLike.length()-1);
        tv_like2.setText(youLike);
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
        } else if (wheel == id_sex) {
            int currentItem = id_sex.getCurrentItem();
            if (isJob) {
                jobName = jobStr[currentItem];
                jobId = jobList.get(currentItem).getId();
            } else {
                sexName = sexStr[currentItem];
            }

        }
    }


    private void showSelectedResult() {
        dialog.dismiss();
        if (flag == 4) {
            tv_location2.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);

        } else if (flag == 5) {
            tv_hometown2.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);

        }
    }

    private void showSelectedResult2() {
        dialog2.dismiss();
        if (isJob) {
            tv_job2.setText(jobName);
        } else {
            tv_sex2.setText(sexName);
            if (sexName.equals("男")) {
                sexType = 1;
            } else {
                sexType = 2;
            }
        }

    }
}
