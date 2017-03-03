package im.boss66.com.activity.connection;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.entity.JobEntity;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChooseJobRequest;

/**
 * 完善资料
 * Created by admin on 2017/2/20.
 */
public class PersonalDataActivity extends ABaseActivity implements View.OnClickListener {

    private final static String TAG = PersonalDataActivity.class.getSimpleName();

    private TextView tv_comfire, tv_sex2, tv_time2, tv_location2, tv_hometown2, tv_job2, tv_like,tv_name2;




    private LocalAddressEntity jsonDate;
    private OptionsPickerView pvOptions; //省市区
    private OptionsPickerView pvOptions2; //性别
    private OptionsPickerView pvOptions3; // 工作

    private ArrayList<LocalAddressEntity.ThreeChild> list = new ArrayList<>(); //1级
    private ArrayList<ArrayList<LocalAddressEntity.FourChild>> list2 = new ArrayList<>(); //2级

    private ArrayList<ArrayList<ArrayList<LocalAddressEntity.LastChild>>> list3 = new ArrayList<>(); //3级

    private int flag = -1; //1 出生年月 2所在地
    private ArrayList<Sex> strings;

    private DatePickerDialog datePickerDialog;

    private RelativeLayout rl_sex;
    private RelativeLayout rl_time;
    private RelativeLayout rl_location;
    private RelativeLayout rl_hometown;
    private RelativeLayout rl_job;
    private RelativeLayout rl_like;
    private RelativeLayout rl_school;

    private RelativeLayout rl_name;


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
                long millionSecnds = sdf.parse(str).getTime();
                tv_time2.setText(str);
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

                    pvOptions3.setPicker((ArrayList) jobList);
                    pvOptions3.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);


        initCityData();

        initOptionPicker(); //省市区选择器
        initOptionPicker2(); //性别选择器


        initViews();
    }

    private void initOptionPicker2() {

        strings = new ArrayList<>();
        Sex sex = new Sex();
        sex.setSex("男");
        Sex sex1 = new Sex();
        sex1.setSex("女");
        strings.add(sex);
        strings.add(sex1);

        pvOptions2 = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置

                String tx = strings.get(options1).getPickerViewText();
                tv_sex2.setText(tx);
            }
        })
                .setSubmitColor(Color.RED)
                .setCancelColor(Color.GRAY)
                .setSubCalSize(18)
                .setContentTextSize(20)
                .setSelectOptions(0, 0, 0)  //设置默认选中项
                .build();

        pvOptions2.setPicker(strings);

        pvOptions3 = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置


            }
        })
                .setSubmitColor(Color.RED)
                .setCancelColor(Color.GRAY)
                .setSubCalSize(18)
                .setContentTextSize(20)
                .setSelectOptions(0, 0, 0)  //设置默认选中项
                .build();


    }


    private void initOptionPicker() {

        LocalAddressEntity.SecondChild result = jsonDate.getResult();
        list = (ArrayList<LocalAddressEntity.ThreeChild>) result.getList(); //省
        for (int i = 0; i < list.size(); i++) {
            List<LocalAddressEntity.FourChild> list = this.list.get(i).getList(); //市
            list2.add((ArrayList<LocalAddressEntity.FourChild>) list);
        }
        for (int j = 0; j < list.size(); j++) {
            List<LocalAddressEntity.FourChild> list = this.list.get(j).getList(); //市
            ArrayList<ArrayList<LocalAddressEntity.LastChild>> list3_1 = new ArrayList<>();
            for (int j1 = 0; j1 < list.size(); j1++) {
                ArrayList<LocalAddressEntity.LastChild> list1 = (ArrayList<LocalAddressEntity.LastChild>) list.get(j1).getList();//县

                list3_1.add(list1);
            }
            list3.add(list3_1);
        }


        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (flag == 4 || flag == 5) {
                    String tx = list.get(options1).getPickerViewText()
                            + "-" + list2.get(options1).get(option2).getRegion_name()
                            + "-" + list3.get(options1).get(option2).get(options3).getPickerViewText();
                    if (flag == 4) {
                        tv_location2.setText(tx);
                    } else if (flag == 5) {
                        tv_hometown2.setText(tx);
                    }
                }

            }
        })
                .setSubmitColor(Color.RED)
                .setCancelColor(Color.GRAY)
                .setSubCalSize(18)
                .setContentTextSize(20)
                .setSelectOptions(0, 0, 0)  //设置默认选中项
                .build();

        pvOptions.setPicker(list, list2, list3);


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


        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_time = (RelativeLayout) findViewById(R.id.rl_time);
        rl_location = (RelativeLayout) findViewById(R.id.rl_location);
        rl_hometown = (RelativeLayout) findViewById(R.id.rl_hometown);
        rl_job = (RelativeLayout) findViewById(R.id.rl_hometown);
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
//                if (TextUtils.isEmpty(et_data1.getText()) ||
//                        TextUtils.isEmpty(et_data2.getText()) ||
//                        TextUtils.isEmpty(et_data3.getText()) ||
//                        TextUtils.isEmpty(et_data4.getText()) ||
//                        TextUtils.isEmpty(et_data5.getText()) ||
//                        TextUtils.isEmpty(et_data6.getText()) ||
//                        TextUtils.isEmpty(et_data7.getText())) {
//                    showToast("请完善内容", false);
//                } else {
//                    Intent intent = new Intent(this, PersonalData2Activity.class);
//                    startActivity(intent);
//                }
                break;

            case R.id.rl_location:
                flag = 4;
                pvOptions.show();
                break;
            case R.id.rl_hometown:
                flag = 5;
                pvOptions.show();
                break;
            case R.id.rl_sex:
                flag = 2;
                pvOptions2.show();
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
                if (jobList == null) {
                    initJobsData();
                }
                break;
            case R.id.rl_like:
                break;
            case R.id.rl_school:
                Intent intent = new Intent(this, SchoolListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_name:
                Intent intent1 = new Intent(this, EditNameActivity.class);
                startActivityForResult(intent1,1);

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&data!=null){
            String name = data.getStringExtra("name");
            tv_name2.setText(name);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initJobsData() {
        ChooseJobRequest request = new ChooseJobRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String entry) {
//                Log.i("liw",entry);
//                handler.obtainMessage(1).sendToTarget();
                //TODO 加上工作的数据


            }


            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });

    }


    public class Sex implements IPickerViewData {
        @Override
        public String getPickerViewText() {
            return sex;
        }

        private String sex;

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

    }
}
