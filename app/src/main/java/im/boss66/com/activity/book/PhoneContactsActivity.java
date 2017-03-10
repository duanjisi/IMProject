package im.boss66.com.activity.book;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ContactUtils;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PingYinUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BasePhoneContact;
import im.boss66.com.entity.PhoneContact;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.PhoneContactsRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.EaseContactList;

/**
 * Created by Johnny on 2017/2/27.
 */
public class PhoneContactsActivity extends BaseActivity {
    private static final String TAG = PhoneContactsActivity.class.getSimpleName();
    private InputMethodManager inputMethodManager;
    private RelativeLayout rlSearch;
    //    private TextView tvSearch;
    private TextView tvBack, tvTitle;
    private ImageView ivSearch;
    protected List<EaseUser> contactList;
    protected ListView listView;
    protected boolean hidden;
    protected ImageButton clearSearch;
    protected EditText query;
    protected Handler handler = new Handler();
    protected EaseUser toBeProcessUser;
    protected String toBeProcessUsername;
    protected EaseContactList contactListLayout;
    protected boolean isConflict;
    protected FrameLayout contentContainer;
    private Map<String, EaseUser> contactsMap;
    private AccountEntity account;
    private PermissionListener permissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contacts);
        initViews();
    }

    private void initViews() {
        getPermission();
        account = App.getInstance().getAccount();
        contactList = new ArrayList<EaseUser>();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        contentContainer = (FrameLayout) findViewById(R.id.content_container);
        contactListLayout = (EaseContactList) findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();


        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.title);
        ivSearch = (ImageView) findViewById(R.id.iv_search);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvTitle.getVisibility() != View.VISIBLE) {
                    UIUtils.showView(tvTitle);
                    UIUtils.showView(ivSearch);
                    UIUtils.hindView(rlSearch);
                } else {
                    finish();
                }
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.hindView(tvTitle);
                UIUtils.hindView(view);
                UIUtils.showView(rlSearch);
            }
        });

        //search
        rlSearch = (RelativeLayout) findViewById(R.id.rl_search);
//        rlTag = (RelativeLayout) findViewById(R.id.rl_tag);
//        tvSearch = (TextView) findViewById(R.id.tv_search);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);

//        tvSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                UIUtils.hindView(rlTag);
//                UIUtils.showView(rlSearch);
//            }
//        });

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactListLayout.filter(s);
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
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
        requestPhoneContact();
    }

    private void requestPhoneContact() {
        String phones = ContactUtils.getPhoneNumbers(context);
//        Log.i("info", "===phones:" + phones);
//        showToast("phones:" + phones, true);
        if (phones != null && !phones.equals("")) {
            showLoadingDialog();
            PhoneContactsRequest request = new PhoneContactsRequest(TAG, phones);
            request.send(new BaseDataRequest.RequestCallback<BasePhoneContact>() {
                @Override
                public void onSuccess(BasePhoneContact pojo) {
                    cancelLoadingDialog();
                    initData(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                    showToast(msg, true);
                }
            });
        }
    }

    private void initData(BasePhoneContact basePhone) {
        ArrayList<PhoneContact> contacts = basePhone.getResult();
        if (contacts != null && contacts.size() != 0) {
            HashMap<String, String> map = ContactUtils.getContactMap(context);
            for (int i = 0; i < contacts.size(); i++) {
                PhoneContact contact = contacts.get(i);
                if (contact != null) {
                    String c_userId = contact.getUser_id();
                    if (c_userId != null && !c_userId.equals(account.getUser_id())) {
                        EaseUser easeUser = new EaseUser();
                        easeUser.setUserName(contact.getUser_name());
                        easeUser.setUserid(c_userId);
                        String name = map.get(contact.getMobile_phone());
                        if (name != null && !name.equals("")) {
                            easeUser.setContactName(name);
                            String word = name.substring(0, 1);
//                    char[] mChar = firstChar.toCharArray();
//                    String word = String.valueOf(mChar[0]);
                            String letter;
                            if (isHanzi(word)) {
                                letter = PingYinUtils.toPinYin(word);
                            } else if (isChar(word)) {
                                letter = word.toUpperCase();
                            } else {
                                letter = "#";
                            }
                            MycsLog.i("info", "letter:" + letter);
                            easeUser.setInitialLetter(letter);
                        }
                        easeUser.setAvatar(contact.getAvatar());
                        easeUser.setIs_friends(contact.getIs_friends());
                        contactList.add(easeUser);
                    }
                }
            }
        }
        contactListLayout.init(contactList, R.layout.ease_row_phone_contact, null);
    }

    private static boolean isChar(String str) {
        return str.matches("[a-zA-Z]*");
    }

    private boolean isHanzi(String ch) {
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        return p.matches(regEx, ch);
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
            }

            @Override
            public void onRequestPermissionError() {
//                ToastUtil.showShort(PhoneContactsActivity.this, getString(R.string.giving_camera_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_CONTCATS //相机权限
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }
}
