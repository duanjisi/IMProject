package im.boss66.com.activity.book;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
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
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.ChatActivity;
import im.boss66.com.activity.im.GroupChatActivity;
import im.boss66.com.adapter.ABaseAdapter;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.GroupEntity;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.GroupAddMemsRequest;
import im.boss66.com.http.request.GroupCreateRequest;
import im.boss66.com.widget.EaseContactList;

/**
 * Created by Johnny on 2017/2/27.
 * 选择联系人
 */
public class SelectContactsActivity extends BaseActivity implements View.OnKeyListener {
    private final static String TAG = SelectContactsActivity.class.getSimpleName();
    private InputMethodManager inputMethodManager;
    private List<EaseUser> contactList;
    //    private LinearLayout rlSearch;
    private TextView tvBack, tvOption, tvTitle;
    //    protected ImageButton clearSearch;
    protected EditText query;
    private ImageLoader imageLoader;
    private LinearLayout linearLayout;
    private ImageView iv_tag;
    //    private Gallery gallery;
//    private MyAdapter adapter;
    private EaseContactList contactListLayout;
    protected ListView listView;
    //    private View viewSearch;
    private View header;
    private RelativeLayout rl_select_group;
    private Handler handler = new Handler();
    private String userid;
    private int mImageHeight = 0;
    private int ll_max_width = 0;
    private boolean isAddMember, isCreateGroup;
    private String user_ids;
    private String groupid;
    private String classType, memberUserNames;
    private List<String> userIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contacts_activity);
        initViews();
    }

    private void initViews() {
//        contactList = new ArrayList<EaseUser>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            classType = bundle.getString("classType");
            isAddMember = bundle.getBoolean("isAddMember", false);
            user_ids = bundle.getString("user_ids", "");
            groupid = bundle.getString("groupid", "");
            isCreateGroup = bundle.getBoolean("isCreateGroup", false);
            if (isCreateGroup) {
                userIdList = new ArrayList<>();
            }
        }
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        contactList = App.getInstance().getContacts();
        userid = App.getInstance().getUid();
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 7;
        ll_max_width = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 120));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        linearLayout = (LinearLayout) findViewById(R.id.ll_image);
        iv_tag = (ImageView) findViewById(R.id.iv_tag);

        header = getLayoutInflater().inflate(R.layout.item_select_contacts, null);
        query = (EditText) findViewById(R.id.query);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvOption = (TextView) findViewById(R.id.tv_option);
        tvTitle = (TextView) findViewById(R.id.title);

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
                if (!TextUtils.isEmpty(classType) && "FriendCircleWhoSeeActivity".equals(classType)) {
                    String member_ids = getMemberIds();
                    Intent intent = new Intent();
                    intent.putExtra("member_id", member_ids);
                    intent.putExtra("memberUserNames", memberUserNames);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (isAddMember) {
                        addMemberRequest();
                    } else {
                        requestGroupCreate();
                    }
                }
            }
        });
        contactListLayout = (EaseContactList) findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
        rl_select_group = (RelativeLayout) header.findViewById(R.id.rl_select_group);

        rl_select_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(GroupChatActivity.class);
            }
        });
        query.setOnKeyListener(this);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                Log.i("info", "=====str:" + str);
//                if (!str.equals("$%$")) {
//                    contactListLayout.filter(s);
//                    if (s.length() > 0) {
//                        clearSearch.setVisibility(View.VISIBLE);
//                    } else {
//                        clearSearch.setVisibility(View.INVISIBLE);
//                    }
//                }
                contactListLayout.filter(s);
//                if (s.length() > 0) {
//                    clearSearch.setVisibility(View.VISIBLE);
//                } else {
//                    clearSearch.setVisibility(View.INVISIBLE);
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
//        clearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                query.getText().clear();
//                hideSoftKeyboard();
//            }
//        });

        if (isAddMember || isCreateGroup) {
            for (int i = 0; i < contactList.size(); i++) {
                EaseUser user = contactList.get(i);
                initEntity(user);
            }
        }

        if (isAddMember || isCreateGroup) {
            contactListLayout.init(contactList, true);
        } else {
            contactListLayout.init(contactList, header, true);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
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
            }
        });
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
//            deleteLastView();
            return true;
        }
        return false;
    }

    private String getTips() {
        String member_ids = getMemberIds();
        String[] str = member_ids.split(",");
        Log.i("info", "=========member_ids:" + member_ids);
        if (!member_ids.equals("")) {
            return "确定(" + str.length + ")";
        } else {
            return "确定";
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
        if (isCreateGroup){
            member_ids = user_ids;
            for (String id:userIdList){
                member_ids = member_ids + "," + id;
            }
        }else {
            member_ids = userid + "," + getMemberIds();
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
        showLoadingDialog();
        GroupCreateRequest request = new GroupCreateRequest(TAG, userid, member_ids, "我的群");
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


    private class loadDrawableTask extends AsyncTask<Void, Integer, Drawable> {

        private String url;
        private EditText editText;

        public loadDrawableTask(EditText edit, String image) {
            super();
            this.url = image;
            this.editText = edit;
        }

        @Override
        protected Drawable doInBackground(Void... voids) {
            return getDrawable(url);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            Editable eb = editText.getEditableText();
            int startPosition = editText.getSelectionStart();
            SpannableString ss = new SpannableString(null);
            // �������ͼƬ
//            Drawable drawable = getResources().getDrawable(
//                    R.drawable.qingfenglou);
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//            ss.setSpan(
//                    new ImageSpan(getimage(""), ImageSpan.ALIGN_BASELINE),
//                    0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(imageSpan, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            drawable.setBounds(2, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
            drawable.setBounds(0, 0, mImageHeight,
                    mImageHeight);
            eb.insert(startPosition, ss);
        }
    }

    private void delete() {
        int startPosition = query.getSelectionStart();
        Editable eb = query.getEditableText();
        eb.delete(startPosition - 1, startPosition);
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
//        imageView.setBackgroundResource(id);
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
        }
        UIUtils.hindView(iv_tag);
//        ViewTreeObserver vto = linearLayout.getViewTreeObserver();
//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                int width = linearLayout.getMeasuredWidth();
//                if (width >= ll_max_width) {
//                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) linearLayout.getLayoutParams();
//                    params.width = ll_max_width;
//                    linearLayout.setLayoutParams(params);
//                }
//                return true;
//            }
//        });
        linearLayout.addView(insertImage(user, position));
    }

    private void deleteView(EaseUser user) {
        String uid = user.getUserid();
        if (isCreateGroup && userIdList.contains(uid)) {
            userIdList.remove(uid);
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
//        else {
//            Drawable drawable = getResources().getDrawable(R.drawable.ease_search_bar_icon_normal);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            query.setCompoundDrawables(drawable, null, null, null);
//        }
    }

    private void deleteLastView() {
        int count = linearLayout.getChildCount();
        if (count != 0) {
            linearLayout.removeViewAt(count - 1);
            View view = linearLayout.getChildAt(count - 1);
            String userid = (String) view.getTag();
            int position = (int) view.getTag(R.id.top_area);
            Session.getInstance().actionRefreshViews(userid, position);
//            final ArrayList<EaseUser> users = (ArrayList<EaseUser>) contactListLayout.getList();
//            for (int i = 0; i < users.size(); i++) {
//                EaseUser user = (EaseUser) listView.getItemAtPosition(i);
////                EaseUser user = users.get(i);
//                if (userid.equals(user.getUserid())) {
//                    user.setChecked(false);
//                }
//            }
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    contactListLayout.refresh();
//                }
//            });
        }
//        else {
//            Drawable drawable = getResources().getDrawable(R.drawable.ease_search_bar_icon_normal);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            query.setCompoundDrawables(drawable, null, null, null);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
