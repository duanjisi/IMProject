package im.boss66.com.activity.book;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/13.
 */
public class BookSearchActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCancel;
    private ListView listView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        initViews();
    }

    private void initViews() {
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.et_keyword);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
