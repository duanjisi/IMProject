package im.boss66.com.activity.connection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.dialog.CancelEditDialog;

/**
 * Created by admin on 2017/3/2.
 */
public class EditSchoolActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_headright_view,tv_headlift_view;
    private boolean isUniversity; //是否大学

    private TextView tv_school_department;
    private TextView tv_year;
    private TextView tv_school_name;

    private RelativeLayout rl_school_department;

    private OptionsPickerView pvOptions;
    private ArrayList<Year> years ;
    private boolean flag =true; //第一次点击时候给pvOptions设置数据
    private CancelEditDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_school);
        Intent intent = getIntent();
        if(intent!=null){
            isUniversity = intent.getBooleanExtra("isUniversity", false);

        }
        initOptionPicker();
        initViews();
    }

    private void initOptionPicker() {
        years = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String formatDate = sdf.format(date);
        for(int i =Integer.parseInt(formatDate);i>=1900;i--){
            Year year = new Year();
            year.setYear(i);
            years.add(year);
        }

        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                int year = years.get(options1).getYear();
                tv_year.setText(year+"");
                tv_year.setTextColor(Color.BLACK);

            }
        })
                .setSubmitColor(Color.RED)
                .setCancelColor(Color.GRAY)
                .setSubCalSize(18)
                .setContentTextSize(20)
                .setSelectOptions(0, 0, 0)  //设置默认选中项
                .build();




    }

    private void initViews() {
        tv_headlift_view  = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view  = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);
        findViewById(R.id.rl_school).setOnClickListener(this);
        RelativeLayout rl_school_department = (RelativeLayout) findViewById(R.id.rl_school_department);
        if(!isUniversity){
            rl_school_department.setVisibility(View.GONE);
        }
        findViewById(R.id.rl_school_department).setOnClickListener(this);
        findViewById(R.id.rl_school_time).setOnClickListener(this);
        tv_school_department = (TextView) findViewById(R.id.tv_school_department);
        tv_school_name = (TextView) findViewById(R.id.tv_school_name);
        tv_year = (TextView) findViewById(R.id.tv_year);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view: //完成

                if(!"请选择".equals(tv_year.getText().toString())
                        &&!"请选择".equals(tv_school_department.getText().toString())
                        &&!"请填写".equals(tv_school_name.getText().toString())){
                    //TODO 请求接口然后弹吐司


                }else {

                    showToast("您填写的信息不全",false);
                }

                break;

            case R.id.rl_school:
                Intent intent = new Intent(this, SearchSchoolActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_school_department:
                if("请填写".equals(tv_school_name.getText().toString())){
                    showToast("请先选择学校",false);
                }else{
                    //TODO 弹院系选择pop
                }
                break;
            case R.id.rl_school_time:

                if(flag){
                    pvOptions.setPicker(years);
                    flag =false;
                }
                pvOptions.show();


                break;



        }

    }
    public class Year implements IPickerViewData{

        private  int year;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        @Override
        public String toString() {
            return "Year{" +
                    "year=" + year +
                    '}';
        }

        @Override
        public String getPickerViewText() {
            return String.valueOf(year);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(!"请选择".equals(tv_year.getText().toString())
                ||!"请选择".equals(tv_school_department.getText().toString())
                ||!"请填写".equals(tv_school_name.getText().toString())){
            if(dialog ==null){
                dialog = new CancelEditDialog(this);
                dialog.show();
            }else if(!dialog.isShowing()){
                dialog.show();
            }
        }else {
            finish();
        }

    }

}
