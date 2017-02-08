package im.boss66.com.activity.personage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.utils.L;

import im.boss66.com.R;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.MainActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.RoundImageView;

/**
 * 我的二维码
 */
public class QrCodeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title,tv_back;
    private RoundImageView iv_head;
    private TextView tv_name,tv_sex,tv_area;
    private ImageView iv_qcode;
    private Bitmap bitmap;
    private int width, height;
    private int screenW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_head = (RoundImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_area = (TextView) findViewById(R.id.tv_area);
        iv_qcode = (ImageView) findViewById(R.id.iv_qcode);
        tv_title.setText(getString(R.string.my_qrcode));
        tv_back.setOnClickListener(this);
        screenW = UIUtils.getScreenWidth(QrCodeActivity.this)*3/5;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv_qcode.getLayoutParams();
        linearParams.height = screenW;
        linearParams.width = screenW;
        iv_qcode.setLayoutParams(linearParams);
        MakeQRCodeUtil.createQRImage("www.2345.com",screenW,screenW,iv_qcode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获得控件的宽高
        //calculateView();
    }

    /*
      * 计算控件的宽高
    */
    private void calculateView() {
        final ViewTreeObserver vto = iv_qcode.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                }
                height = iv_qcode.getMeasuredHeight();
                width = iv_qcode.getMeasuredWidth();
                //Bitmap logo = MakeQRCodeUtil.gainBitmap(QrCodeActivity.this, R.drawable.ic_launcher);
                Bitmap background = MakeQRCodeUtil.gainBitmap(QrCodeActivity.this, R.drawable.bg_top);
                //Bitmap markBMP = MakeQRCodeUtil.gainBitmap(QrCodeActivity.this, R.drawable.water);
                try {
                    //获得二维码图片
                    bitmap = MakeQRCodeUtil.makeQRImage(
                            "http://www.baidu.com",
                            screenW, screenW);
                    //给二维码加背景
                    bitmap = MakeQRCodeUtil.addBackground(bitmap, background);
                    //加水印
                    //bitmap = MakeQRCodeUtil.composeWatermark(bitmap, markBMP);
                    //设置二维码图片
                    iv_qcode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

    }



}
