package im.boss66.com.activity.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.adapter.UserAdapter;
import im.boss66.com.entity.BaseUserInform;
import im.boss66.com.entity.UserInform;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.UsersInformRequest;

/**
 * Created by Johnny on 2017/2/28.
 */
public class QureAccountActivity extends BaseActivity {
    private static final String TAG = QureAccountActivity.class.getSimpleName();
    private InputMethodManager inputMethodManager;
    private TextView tvBack;
    protected ListView listView;
    private UserAdapter adapter;
    protected ImageButton clearSearch;
    protected EditText query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qure_account);
        initViews();
    }

    private void initViews() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        adapter = new UserAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListener());

        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        query.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String keyWords = getText(query);
                    if (keyWords != null && !keyWords.equals("")) {
                        requestUsers(keyWords);
                    }
                    return true;
                }
                return false;
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
    }

    private void requestUsers(String keywords) {
        UsersInformRequest request = new UsersInformRequest(TAG, keywords, "1", "20");
        request.send(new BaseDataRequest.RequestCallback<BaseUserInform>() {
            @Override
            public void onSuccess(BaseUserInform pojo) {
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void bindDatas(BaseUserInform userInform) {
        if (userInform != null) {
            ArrayList<UserInform> informs = userInform.getResult();
            if (informs != null && informs.size() != 0) {
                if (informs.size() == 1) {
                    Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                    intent.putExtra("classType", "QureAccountActivity");
                    intent.putExtra("userid", informs.get(0).getUser_id());
                    startActivity(intent);
                } else {
                    adapter.initData(informs);
                }
            }
        }
    }


    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            UserInform inform = (UserInform) adapterView.getItemAtPosition(i);
            if (inform != null) {
                Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                intent.putExtra("classType", "QureAccountActivity");
                intent.putExtra("userid", inform.getUser_id());
                startActivity(intent);
            }
        }
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
