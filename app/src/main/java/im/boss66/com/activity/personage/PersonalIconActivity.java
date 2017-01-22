package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by GMARUnity on 2017/1/21.
 */
public class PersonalIconActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back,tv_right;
    private ImageView iv_icon;
    private ActionSheet actionSheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_icon);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_right = (TextView) findViewById(R.id.tv_right);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:

                break;
        }
    }

    private void showActionSheet(){
        actionSheet=new ActionSheet(PersonalIconActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("拍照", ActionSheet.SheetItemColor.Blue,
                        PersonalIconActivity.this)
                .addSheetItem("从手机相册选择", ActionSheet.SheetItemColor.Blue,
                        PersonalIconActivity.this)
                .addSheetItem("保存图片", ActionSheet.SheetItemColor.Blue,
                        PersonalIconActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {

    }
}
