package im.boss66.com.activity.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.book.SelectContactsActivity;
import im.boss66.com.adapter.ABaseAdapter;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.BaseConversation;
import im.boss66.com.entity.GroupEntity;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.GroupAddMemsRequest;
import im.boss66.com.http.request.GroupCreateRequest;
import im.boss66.com.widget.EaseContactList;
import im.boss66.com.widget.dialog.SelectMultiDialog;
import im.boss66.com.widget.dialog.SelectSingleDialog;

/**
 * Created by Johnny on 2017/4/14.
 * 转发，选择最近聊天成员
 */
public class SelectConversationActivity extends BaseActivity implements View.OnKeyListener {
    private final static String TAG = SelectConversationActivity.class.getSimpleName();
    private InputMethodManager inputMethodManager;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private List<EaseUser> contactList;
    private TextView tvBack, tvOption, tvTitle;
    protected EditText query;
    private ImageLoader imageLoader;
    private LinearLayout linearLayout, ll_search;
    private ImageView iv_tag;
    private EaseContactList contactListLayout;
    protected ListView listView;
    private View header;
    private RelativeLayout rl_select_group;
    private TextView tvTag;
    private Handler handler = new Handler();
    private String userid;
    private int mImageHeight = 0;
    private int ll_max_width = 0;
    private boolean isAddMember, isCreateGroup;
    private String user_ids;
    private String groupid;
    private String classType, memberUserNames;
    private List<String> userIdList;
    private List<String> nameList;
    private HorizontalScrollView horizontalScrollView;
    private boolean flag = false;
    private boolean isSingle = true;
    private MessageItem messageItem;
    private SelectSingleDialog singleDialog;
    private SelectMultiDialog multiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_conversation_activity);
        initViews();
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            classType = bundle.getString("classType");
            messageItem = (MessageItem) bundle.getSerializable("item");
            isAddMember = bundle.getBoolean("isAddMember", false);
            user_ids = bundle.getString("user_ids", "");
            groupid = bundle.getString("groupid", "");
            isCreateGroup = bundle.getBoolean("isCreateGroup", false);
            if (isCreateGroup) {
                userIdList = new ArrayList<>();
                nameList = new ArrayList<>();
            }
        }
        singleDialog = new SelectSingleDialog(context);
        multiDialog = new SelectMultiDialog(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
//        contactList = App.getInstance().getContacts();
        initDatas();
        userid = App.getInstance().getUid();
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 7;
        ll_max_width = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 120));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        linearLayout = (LinearLayout) findViewById(R.id.ll_image);
        ll_search = (LinearLayout) findViewById(R.id.rl_search);
        iv_tag = (ImageView) findViewById(R.id.iv_tag);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_search.getLayoutParams();
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = mImageHeight;
        ll_search.setLayoutParams(params);

        header = getLayoutInflater().inflate(R.layout.item_select_contacts, null);
        query = (EditText) findViewById(R.id.query);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvOption = (TextView) findViewById(R.id.tv_option);
        tvTitle = (TextView) findViewById(R.id.title);

        if (isSingle) {
            tvOption.setText("多选");
        }

        if (isAddMember || isCreateGroup) {
            tvTitle.setText("选择联系人");
        }
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = getText(tvOption);
                if (str.equals("单选")) {
                    tvOption.setText("多选");
                    isSingle = true;
                    contactListLayout.setShowTag(false);
                } else if (str.equals("多选")) {
                    tvOption.setText("单选");
                    isSingle = false;
                    contactListLayout.setShowTag(true);
                } else {
                    ArrayList<EaseUser> users = getCheckedMembers();
                    if (users.size() != 0) {
                        multiDialog.showDialog(users, messageItem);
                    }
                }
            }
        });

        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.EXIT_CURRENT_ACTIVITY);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mLocalBroadcastReceiver, filter);

        contactListLayout = (EaseContactList) findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
        rl_select_group = (RelativeLayout) header.findViewById(R.id.rl_select_group);
        tvTag = (TextView) header.findViewById(R.id.tv_tag);
        TextView tips = (TextView) header.findViewById(R.id.tv_top_tips);

        UIUtils.showView(tips);
        tvTag.setText("更多联系人");
        rl_select_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectContactsActivity.class);
                intent.putExtra("forwarding", true);
                intent.putExtra("user_ids", getUser_ids());
//                intent.putExtra("isAddMember", true);
                startActivityForResult(intent, 100);
            }
        });
        query.setOnKeyListener(this);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                Log.i("info", "=====str:" + str);
                contactListLayout.filter(s);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        if (isAddMember || isCreateGroup) {
            for (int i = 0; i < contactList.size(); i++) {
                EaseUser user = contactList.get(i);
                initEntity(user);
            }
        }

        if (isAddMember || isCreateGroup) {
            contactListLayout.init(contactList, false);
        } else {
            contactListLayout.init(contactList, header, false);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                if (isSingle) {
                    if (messageItem != null) {
                        singleDialog.showDialog(user, messageItem);
                    }
                } else {
                    ArrayList<EaseUser> list = getCheckedMembers();
                    if (list.size() < 9) {
                        boolean isAdded = user.isAdded();
                        if (!isAdded) {
                            boolean checked = user.isChecked();
                            if (!checked) {
                                addView(user, position);
                            } else {
                                deleteView(user);
                            }
                            user.setChecked(!checked);
                            contactListLayout.refresh();
                        }
                        tvOption.setText(getTips());
                    } else {
                        showToast("最多只能选9个", true);
                    }
                }
            }
        });
    }


    private void initDatas() {
        contactList = new ArrayList<>();
        ArrayList<BaseConversation> list = (ArrayList<BaseConversation>) ConversationHelper.getInstance().query();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                BaseConversation entity = list.get(i);
                EaseUser easeUser = new EaseUser();
                easeUser.setAvatar(entity.getAvatar());
                easeUser.setInitialLetter("");
                easeUser.setNick(entity.getUser_name());
                easeUser.setUserName(entity.getUser_name());
                easeUser.setUserid(entity.getConversation_id());
                easeUser.setMsgType(entity.getNewest_msg_type());
//                easeUser.setFid(entity.getFid());
                contactList.add(easeUser);
            }
        }
    }

    private void initEntity(EaseUser user) {
        if (user_ids != null && !user_ids.equals("")) {
            String[] ids = user_ids.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].equals(user.getUserid())) {
                    user.setAdded(true);
                }
            }
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL
                && event.getAction() == KeyEvent.ACTION_DOWN && getText(query).equals("")) {
            deleteLastView();
            return true;
        }
        return false;
    }

    private String getTips() {
        String member_ids = getMemberIds();
        String[] str = member_ids.split(",");
        if (!member_ids.equals("")) {
            return "发送(" + str.length + ")";
        } else {
            return "单选";
        }
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void addMemberRequest() {
        String member_ids = getMemberIds();
        Log.i("info", "member_ids:" + member_ids);
        if (member_ids.equals("")) {
            showToast("请选择群成员!", true);
            return;
        }
        showLoadingDialog();
        GroupAddMemsRequest request = new GroupAddMemsRequest(TAG, groupid, member_ids);
        request.send(new BaseModelRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                onAddMember();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void onAddMember() {
        try {
            Thread.sleep(1000);
            cancelLoadingDialog();
            showToast("添加成员成功!", true);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestGroupCreate() {//建群请求
        String member_ids = null;
        String grpName = "";
        if (isCreateGroup) {
            member_ids = user_ids;
            for (String id : userIdList) {
                member_ids = member_ids + "," + id;
            }
            grpName = getGroupName();
        } else {
            member_ids = userid + "," + getMemberIds();
            grpName = getMemberNames();
        }

        Log.i("info", "member_ids:" + member_ids);
        if (member_ids.equals("")) {
            showToast("请选择群成员!", true);
            return;
        }
        if (member_ids.length() < 3) {
            showToast("群成员不够!", true);
            return;
        }
        if (TextUtils.isEmpty(grpName)) {
            showToast("群名称为空!", true);
            return;
        }

        showLoadingDialog();
        GroupCreateRequest request = new GroupCreateRequest(TAG, userid, member_ids, grpName);
        request.send(new BaseModelRequest.RequestCallback<GroupEntity>() {
            @Override
            public void onSuccess(GroupEntity pojo) {
                cancelLoadingDialog();
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private String getGroupName() {
        String name = "";
        Log.i("info", "==========nameList:" + nameList);
        if (nameList != null && nameList.size() != 0) {
            Log.i("info", "==========nameList.toString():" + nameList.toString());
            for (int i = 0; i < nameList.size(); i++) {
                Log.i("info", "==========name:" + name);
                if (i < 3) {
                    name = name + "、" + nameList.get(i);
                }
            }
        }
        return name;
    }

    private void bindDatas(GroupEntity groupEntity) {
        if (groupEntity != null) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("title", groupEntity.getName());
            intent.putExtra("toUid", groupEntity.getGroupid());
            intent.putExtra("toAvatar", groupEntity.getSnap());
            intent.putExtra("isgroup", true);
            startActivity(intent);
            finish();
        }
    }

    private ArrayList<EaseUser> getCheckedMembers() {
        ArrayList<EaseUser> users = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            EaseUser user = contactList.get(i);
            if (user.isChecked()) {
                users.add(user);
            }
        }
        return users;
    }

    private String getUser_ids() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contactList.size(); i++) {
            EaseUser user = contactList.get(i);
            if (user.isChecked() && !user.getMsgType().equals("group")) {
                String userid = user.getUserid();
                sb.append(userid + ",");
            }
        }
        String str = sb.toString();
        if (!str.contains(",")) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(","));
        }
    }

    private String getMemberIds() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_name = new StringBuilder();
        for (int i = 0; i < contactList.size(); i++) {
            EaseUser user = contactList.get(i);
            if (user.isChecked()) {
                String userid = user.getUserid();
                sb.append(userid + ",");
                String name = user.getUsername();
                if (!TextUtils.isEmpty(name)) {
                    sb_name.append(name + ",");
                } else {
                    sb_name.append(userid + ",");
                }
            }
        }

        String str = sb.toString();
        memberUserNames = sb_name.toString();
        if (!TextUtils.isEmpty(memberUserNames)) {
            memberUserNames = memberUserNames.substring(0, memberUserNames.lastIndexOf(","));
        }
        if (!str.contains(",")) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(","));
        }
    }

    private String getMemberNames() {
        String names = "";
        int count = 0;
        for (int i = 0; i < contactList.size(); i++) {
            EaseUser user = contactList.get(i);
            if (user.isChecked()) {
                count++;
                if (count < 4) {
                    String name = user.getUsername();
                    if (!TextUtils.isEmpty(name)) {
                        if (names.equals("")) {
                            names = name;
                        } else {
                            names = names + "、" + name;
                        }
                    }
                }
            }
        }
        return names;
    }

    private Drawable getDrawable(String urlpath) {
        Drawable d = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            d = Drawable.createFromStream(in, "background.jpg");
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }


    private class MyAdapter extends ABaseAdapter<EaseUser> {
        private ImageLoader imageLoader;

        public MyAdapter(Context context) {
            super(context);
            imageLoader = ImageLoaderUtils.createImageLoader(context);
        }

        @Override
        protected View setConvertView(int position, EaseUser easeUser, View convertView) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
            imageView.getLayoutParams().width = (int) mImageHeight;
            imageView.getLayoutParams().height = (int) mImageHeight;
            String imageUrl = easeUser.getAvatar();
            if (imageUrl != null && !imageUrl.equals("")) {
                imageLoader.displayImage(imageUrl, imageView, ImageLoaderUtils.getDisplayImageOptions());
            }
            return imageView;
        }
    }

    private View insertImage(EaseUser user, int position) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(mImageHeight, mImageHeight));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(mImageHeight, mImageHeight));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageLoader.displayImage(user.getAvatar(), imageView, ImageLoaderUtils.getDisplayImageOptions());
        layout.addView(imageView);
        layout.setTag(user.getUserid());
        layout.setTag(R.id.top_area, position);
        return layout;
    }

    private void addView(final EaseUser user, final int position) {
        String uid = user.getUserid();
        if (isCreateGroup) {
            userIdList.add(uid);
            nameList.add(user.getNick());
        }
        UIUtils.hindView(iv_tag);
        linearLayout.addView(insertImage(user, position));
        scaleMaxWidth();
    }

    private void scaleMaxWidth() {
        flag = true;
        linearLayout.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) horizontalScrollView.getLayoutParams();
                int width = linearLayout.getWidth();
                if (width >= mImageHeight * 6) {
                    params.width = mImageHeight * 6;
                    horizontalScrollView.setLayoutParams(params);
                    if (flag) {
                        flag = false;
                        horizontalScrollView.scrollTo(linearLayout.getMeasuredWidth() - horizontalScrollView.getWidth(), 0);
                    }
                } else {
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    horizontalScrollView.setLayoutParams(params);
                }
            }
        });
    }

    int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    private void deleteView(EaseUser user) {
        String uid = user.getUserid();
        if (isCreateGroup && userIdList.contains(uid)) {
            userIdList.remove(uid);
            nameList.remove(user.getNick());
        }
        int count = linearLayout.getChildCount();
        if (count != 0) {
            for (int i = 0; i < count; i++) {
                View view = linearLayout.getChildAt(i);
                String tag = (String) view.getTag();
                if (tag.equals(user.getUserid())) {
                    linearLayout.removeView(view);
                    if (linearLayout.getChildCount() == 0) {
                        UIUtils.showView(iv_tag);
                    }
                    break;
                }
            }
        }
        scaleMaxWidth();
    }

    private void deleteLastView() {
        int count = linearLayout.getChildCount();
        if (count != 0) {
            Log.i("info", "===============deleteLastView()");
            View view = linearLayout.getChildAt(count - 1);
            String userid = (String) view.getTag();
            int position = (int) view.getTag(R.id.top_area);
            refreshDatas(userid);
            Log.i("info", "==========view:" + view + "\n" + "tag:" + view.getTag());
            linearLayout.removeViewAt(count - 1);
            tvOption.setText(getTips());
            if (linearLayout.getChildCount() == 0) {
                UIUtils.showView(iv_tag);
            }
            scaleMaxWidth();
        }
    }

    private void refreshDatas(String userid) {
        for (EaseUser user : contactList) {
            if (user.getUserid().equals(userid)) {
                user.setChecked(false);
            }
        }
        contactListLayout.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mLocalBroadcastReceiver);
        reserverDatas();
    }

    private void reserverDatas() {
        ArrayList<EaseUser> list = App.getInstance().getContacts();
        for (EaseUser user : list) {

            if (user.isAdded()) {
                user.setAdded(false);
            }

            if (user.isChecked()) {
                user.setChecked(false);
            }
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.EXIT_CURRENT_ACTIVITY.equals(action)) {
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    if (data != null) {
                        ArrayList<EaseUser> users = (ArrayList<EaseUser>) data.getSerializableExtra("list");
                        if (users != null && users.size() != 0) {
                            showMultiDialog(users);
                        }
                    }
                    break;
            }
        }
    }

    private void showMultiDialog(ArrayList<EaseUser> users) {
        printStr(users);
        ArrayList<EaseUser> list = getCheckedMembers();
        list.addAll(users);
        multiDialog.showDialog(list, messageItem);
    }


    private void printStr(ArrayList<EaseUser> users) {
        for (EaseUser user : users) {
            Log.i("info", "==========nick:" + user.getUsername());
        }
    }
}
