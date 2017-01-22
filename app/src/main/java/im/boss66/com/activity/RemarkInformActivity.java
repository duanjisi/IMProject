package im.boss66.com.activity;

import android.os.Bundle;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.task.DownLoaderTask;

/**
 * Created by Johnny on 2017/1/20.
 * 备注信息
 */
public class RemarkInformActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark_inform);
    }


    private void doHandler() {
        DownLoaderTask task = new DownLoaderTask("", "", this);
        task.execute();
    }
}
