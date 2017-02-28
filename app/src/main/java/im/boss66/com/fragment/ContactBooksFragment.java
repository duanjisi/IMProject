package im.boss66.com.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.book.BookSearchActivity;
import im.boss66.com.activity.book.NewFriendsActivity;
import im.boss66.com.activity.im.ChatActivity;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.BaseContact;
import im.boss66.com.entity.ContactEntity;
import im.boss66.com.entity.FriendState;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ContactsRequest;
import im.boss66.com.http.request.NewFriendNumRequest;
import im.boss66.com.widget.EaseContactList;
import im.boss66.com.widget.TopNavigationBar;

/**
 * Created by Johnny on 2017/1/14.
 */
public class ContactBooksFragment extends BaseFragment {
    private final static String TAG = ContactBooksFragment.class.getSimpleName();
    private TopNavigationBar topNavigationBar;
    private InputMethodManager inputMethodManager;
    private List<EaseUser> contactList;
    private RelativeLayout rlSearch, rlTag;
    private TextView tvSearch;
    protected ImageButton clearSearch;
    protected EditText query;
    private EaseContactList contactListLayout;
    protected ListView listView;
    private ImageView iv_add;
    private View viewSearch;
    private View header;
    private RelativeLayout rl_new_friend, rl_chat_group;
    private TextView tv_new_nums;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_books, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
//        topNavigationBar = (TopNavigationBar) view.findViewById(R.id.top_navigation_bar);
//        topNavigationBar.setRightBtnOnClickedListener(this);
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
                Intent intent = new Intent(getActivity(), BookSearchActivity.class);
                startActivity(intent);
            }
        });
        contactListLayout = (EaseContactList) view.findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
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
        request();
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
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("title", user.getUsername());
                intent.putExtra("toUid", user.getUserid());
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

    private void requestNewNums() {
        NewFriendNumRequest request = new NewFriendNumRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<FriendState>() {
            @Override
            public void onSuccess(FriendState pojo) {
                cancelLoadingDialog();
                bindData(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void bindData(FriendState state) {
        if (state != null) {
            String num = state.getCount();
            if (num != null && !num.equals("0")) {
                UIUtils.hindView(tv_new_nums);
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
}
