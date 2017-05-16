package im.boss66.com.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.book.NewFriendsActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.activity.discover.SearchByAllNetActivity;
import im.boss66.com.activity.im.GroupChatActivity;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.entity.BaseContact;
import im.boss66.com.entity.ContactEntity;
import im.boss66.com.entity.FriendState;
import im.boss66.com.entity.MessageEvent;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ContactsRequest;
import im.boss66.com.http.request.NewFriendNumRequest;
import im.boss66.com.widget.EaseBookList;
import im.boss66.com.widget.RefreshListView;
import im.boss66.com.widget.TopNavigationBar;

/**
 * Created by Johnny on 2017/1/14.
 */
public class ContactBooksFragment extends BaseFragment implements
        Observer,
        RefreshListView.OnRefreshListener,
        View.OnClickListener {
    private final static String TAG = ContactBooksFragment.class.getSimpleName();
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private TopNavigationBar topNavigationBar;
    private InputMethodManager inputMethodManager;
    private static List<EaseUser> contactList;
    private RelativeLayout rlSearch, rlTag;
    private TextView tvSearch;
    protected ImageButton clearSearch;
    protected EditText query;
    private static EaseBookList contactListLayout;
    protected RefreshListView listView;
    private ImageView iv_add;
    private View viewSearch;
    private View header;
    private RelativeLayout rl_new_friend, rl_chat_group;
    private static TextView tv_new_nums;
    private ImageView iv_avatar;
    private ImageLoader imageLoader;
    private AccountEntity account;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Session.getInstance().addObserver(this);
        Log.i("liywa", "创建了");
        return inflater.inflate(R.layout.fragment_contact_books, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initViews(view);
    }

    private void initViews(View view) {
        Log.i("liywa", "创建了2");
        account = App.getInstance().getAccount();
        imageLoader = ImageLoaderUtils.createImageLoader(getActivity());
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        contactList = new ArrayList<EaseUser>();
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        header = getActivity().getLayoutInflater().inflate(R.layout.item_contact_header, null);
        viewSearch = view.findViewById(R.id.search_bar_view);
        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        rlSearch = (RelativeLayout) view.findViewById(R.id.rl_search);
        rlTag = (RelativeLayout) view.findViewById(R.id.rl_tag);
        tvSearch = (TextView) view.findViewById(R.id.tv_search);
        query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        iv_avatar.setOnClickListener(this);
        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.CONTACTS_REMOVE_CURRETN_ITEM);
        filter.addAction(Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
        filter.addAction(Constants.Action.CHAT_AGREE_FRIENDSHIP);
//        LocalBroadcastManager.getInstance(getActivity())
//                .registerReceiver(mLocalBroadcastReceiver, filter);
        getActivity().registerReceiver(mLocalBroadcastReceiver, filter);
        Log.i("liywa", "创建了3");
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
        viewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getActivity(), BookSearchActivity.class);
                Intent intent = new Intent(getActivity(), SearchByAllNetActivity.class);
                startActivity(intent);
            }
        });
        contactListLayout = (EaseBookList) view.findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
        listView.setOnRefreshListener(this);
        rl_new_friend = (RelativeLayout) header.findViewById(R.id.rl_add_friends);
        rl_chat_group = (RelativeLayout) header.findViewById(R.id.rl_chat_group);
        tv_new_nums = (TextView) header.findViewById(R.id.tv_notify);

        rl_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
                startActivity(intent);
            }
        });

        rl_chat_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                startActivity(intent);
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hindView(rlTag);
                UIUtils.showView(rlSearch);
            }
        });

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
        contactListLayout.init(contactList, header);
//        imageLoader.displayImage(account.getAvatar(), iv_avatar, ImageLoaderUtils.getDisplayImageOptions());
        request();
        requestNewNums();
//        initData(null);
    }

    private void request() {
        showLoadingDialog();
        ContactsRequest request = new ContactsRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<BaseContact>() {
            @Override
            public void onSuccess(BaseContact pojo) {
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

    private void initData(BaseContact baseContact) {
        ArrayList<ContactEntity> list = baseContact.getResult();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                ContactEntity entity = list.get(i);
                EaseUser easeUser = new EaseUser();
                easeUser.setAvatar(entity.getAvatar());
                easeUser.setInitialLetter(entity.getFirst_letter());
                easeUser.setNick(entity.getUser_name());
                easeUser.setUserName(entity.getUser_name());
                easeUser.setUserid(entity.getFriend_id());
                easeUser.setFid(entity.getFid());
//                easeUser.setFriend_id(entity.getFriend_id());
                contactList.add(easeUser);
            }
            App.getInstance().setContacts((ArrayList<EaseUser>) contactList);
        }
//        contactListLayout.init(contactList, header);
        contactListLayout.init(contactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("title", user.getUsername());
//                intent.putExtra("toUid", user.getUserid());
//                intent.putExtra("toAvatar", user.getAvatar());
//                intent.putExtra("isgroup", false);
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), PersonalNearbyDetailActivity.class);
                intent.putExtra("classType", "ContactBooksFragment");
                intent.putExtra("userid", user.getUserid());
                startActivity(intent);
            }
        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final EaseUser user = (EaseUser) listView.getItemAtPosition(position);
//                Intent intent = new Intent(getActivity(), PersonalNearbyDetailActivity.class);
//                intent.putExtra("ease_user", user);
//                startActivity(intent);
//                return true;
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
                EventBus.getDefault().post(new ActionEntity(Constants.Action.MENU_CAHNGE_CURRENT_TAB));
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshPagerDatas();
        requestNewNums();
    }

    private static void requestNewNums() {
        NewFriendNumRequest request = new NewFriendNumRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<FriendState>() {
            @Override
            public void onSuccess(FriendState pojo) {
                bindData(pojo);
            }

            @Override
            public void onFailure(String msg) {
//                showToast(msg, true);
                UIUtils.Toast(App.getInstance().getApplicationContext(), msg);
            }
        });
    }

    private static void bindData(FriendState state) {
        if (state != null) {
            String num = state.getCount();
            if (num != null && !num.equals("0")) {
                UIUtils.showView(tv_new_nums);
                tv_new_nums.setText(num);
            } else {
                UIUtils.hindView(tv_new_nums);
            }
        }
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("info", "onReceiver():action:" + intent.getAction());
            String action = intent.getAction();
            if (Constants.Action.CONTACTS_REMOVE_CURRETN_ITEM.equals(action)) {
                String userid = intent.getStringExtra("userid");
                Log.i("info", "======被删除的userid:" + userid);
                if (userid != null && !userid.equals("")) {
                    App.getInstance().removeItem(userid);
                    contactListLayout.removeItem(userid);
                }
            } else if (Constants.Action.CHAT_NEW_MESSAGE_NOTICE.equals(action)) {//添加好友新消息,更新消息书，及其提示
                Log.i("info", "=============requestNewNums()");
                requestNewNums();
            } else if (Constants.Action.CHAT_AGREE_FRIENDSHIP.equals(action)) {//同意彼此为好友关系，刷新列表数据
                Log.i("info", "=============refreshPagerDatas()");
                refreshPagerDatas();
                requestNewNums();
            }
        }
    }

    private void refreshPagerDatas() {
        ContactsRequest request = new ContactsRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<BaseContact>() {
            @Override
            public void onSuccess(BaseContact pojo) {
                refreshDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
//                showToast(msg, true);
                UIUtils.Toast(App.getInstance().getApplicationContext(), msg);
                listView.refreshComplete();
            }
        });
    }

    private void refreshDatas(BaseContact baseContact) {
        if (contactList != null) {
            contactList.clear();
        }
        ArrayList<ContactEntity> list = baseContact.getResult();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                ContactEntity entity = list.get(i);
                EaseUser easeUser = new EaseUser();
                easeUser.setAvatar(entity.getAvatar());
                easeUser.setInitialLetter(entity.getFirst_letter());
                easeUser.setNick(entity.getUser_name());
                easeUser.setUserName(entity.getUser_name());
                easeUser.setUserid(entity.getFriend_id());
                easeUser.setFid(entity.getFid());
                contactList.add(easeUser);
            }
            App.getInstance().setContacts((ArrayList<EaseUser>) contactList);
//            contactListLayout.notifyDataSetChanged();
            contactListLayout.init(contactList);
        }
        listView.refreshComplete();
    }

    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_CONTACTS_REFRESH_PAGER) {
//            String action = (String) sin.getData();
//            if (action.equals(Constants.Action.CHAT_NEW_MESSAGE_NOTICE)) {
//                Log.i("info", "=============requestNewNums()");
//                requestNewNums();
//            } else if (action.equals(Constants.Action.CHAT_AGREE_FRIENDSHIP)) {
//                Log.i("info", "=============refreshPagerDatas()");
//                refreshPagerDatas();
//                requestNewNums();
//            }
        }
    }


//    @Override
//    public void onMessageReceiver(String action) {
//        android.util.Log.i("info", "==================action");
//        if (action.equals(Constants.Action.CHAT_NEW_MESSAGE_NOTICE)) {
//            Log.i("info", "=============requestNewNums()");
//            requestNewNums();
//        } else if (action.equals(Constants.Action.CHAT_AGREE_FRIENDSHIP)) {
//            Log.i("info", "=============refreshPagerDatas()");
//            refreshPagerDatas();
//            requestNewNums();
//        }
//    }

    public void onChatMessageReceiver(String action) {
        if (action.equals(Constants.Action.CHAT_NEW_MESSAGE_NOTICE)) {
            Log.i("info", "=============requestNewNums()");
            requestNewNums();
        } else if (action.equals(Constants.Action.CHAT_AGREE_FRIENDSHIP)) {
            Log.i("info", "=============refreshPagerDatas()");
            refreshPagerDatas();
            requestNewNums();
        }
    }

    public void onMessage(String action) {
        Log.i("info", "=============onMessage中action:" + action);
        if (action.equals(Constants.Action.CHAT_NEW_MESSAGE_NOTICE)) {
            Log.i("info", "=============requestNewNums()");
            requestNewNums();
        } else if (action.equals(Constants.Action.CHAT_AGREE_FRIENDSHIP)) {
            Log.i("info", "=============refreshPagerDatas()");
            refreshPagerDatas();
            requestNewNums();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        LocalBroadcastManager.getInstance(getActivity()).
//                unregisterReceiver(mLocalBroadcastReceiver);
        getActivity().unregisterReceiver(mLocalBroadcastReceiver);
    }

    @Subscribe
    public void onEvent(MessageEvent msg) {
        String action = msg.getAction();
        if (!action.equals("")) {
            onMessage(action);
        }
    }
}
