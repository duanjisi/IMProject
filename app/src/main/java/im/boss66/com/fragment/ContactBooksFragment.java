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

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.book.BookSearchActivity;
import im.boss66.com.activity.book.NewFriendsActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.activity.im.ChatActivity;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.BaseContact;
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
//        request();
        initData(null);
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
//        ArrayList<ContactEntity> list = baseContact.getResult();
//        if (list != null && list.size() != 0) {
//            for (int i = 0; i < list.size(); i++) {
//                ContactEntity entity = list.get(i);
//                EaseUser easeUser = new EaseUser();
//                easeUser.setAvatar(entity.getAvatar());
//                easeUser.setInitialLetter(entity.getFirst_letter());
//                easeUser.setNick(entity.getUser_name());
//                easeUser.setUserName(entity.getUser_name());
//                easeUser.setUserid(entity.getFriend_id());
//                easeUser.setFid(entity.getFid());
////                easeUser.setFriend_id(entity.getFriend_id());
//                contactList.add(easeUser);
//            }
//        }
        testDatas();
        contactListLayout.init(contactList, header);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), PersonalNearbyDetailActivity.class);
                intent.putExtra("ease_user", user);
                startActivity(intent);
                return true;
            }
        });
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

    private void testDatas() {
        EaseUser easeUser = new EaseUser();
        easeUser.setAvatar("http://img3.imgtn.bdimg.com/it/u=2885730817,284862392&fm=11&gp=0.jpg");
        easeUser.setInitialLetter("A");
        easeUser.setNick("阿猫");
        easeUser.setUserName("阿猫");
        easeUser.setUserid("1001");
        easeUser.setFid("01");

        EaseUser user4 = new EaseUser();
        user4.setAvatar("http://img5.imgtn.bdimg.com/it/u=1907434450,3070417799&fm=23&gp=0.jpg");
        user4.setInitialLetter("C");
        user4.setNick("陈明仁");
        user4.setUserName("陈明仁");
        user4.setUserid("1005");
        user4.setFid("05");

        EaseUser user7 = new EaseUser();
        user7.setAvatar("http://img3.imgtn.bdimg.com/it/u=2885730817,284862392&fm=11&gp=0.jpg");
        user7.setInitialLetter("C");
        user7.setNick("陈杰");
        user7.setUserName("陈杰");
        user7.setUserid("1008");
        user7.setFid("08");

        EaseUser user3 = new EaseUser();
        user3.setAvatar("http://img3.imgtn.bdimg.com/it/u=3138129898,245903813&fm=11&gp=0.jpg");
        user3.setInitialLetter("H");
        user3.setNick("胡宗南");
        user3.setUserName("胡宗南");
        user3.setUserid("1004");
        user3.setFid("04");

        EaseUser user1 = new EaseUser();
        user1.setAvatar("http://img2.imgtn.bdimg.com/it/u=203482105,2168976613&fm=23&gp=0.jpg");
        user1.setInitialLetter("L");
        user1.setNick("李华");
        user1.setUserName("李华");
        user1.setUserid("1002");
        user1.setFid("02");

        EaseUser user2 = new EaseUser();
        user2.setAvatar("http://img5.imgtn.bdimg.com/it/u=2933466676,571425337&fm=11&gp=0.jpg");
        user2.setInitialLetter("Z");
        user2.setNick("张灵埔");
        user2.setUserName("张灵埔");
        user2.setUserid("1003");
        user2.setFid("03");

        EaseUser user5 = new EaseUser();
        user5.setAvatar("http://img3.imgtn.bdimg.com/it/u=3138129898,245903813&fm=11&gp=0.jpg");
        user5.setInitialLetter("Z");
        user5.setNick("张小华");
        user5.setUserName("张小华");
        user5.setUserid("1006");
        user5.setFid("06");

        EaseUser user6 = new EaseUser();
        user6.setAvatar("http://img2.imgtn.bdimg.com/it/u=203482105,2168976613&fm=23&gp=0.jpg");
        user6.setInitialLetter("Z");
        user6.setNick("张学良");
        user6.setUserName("张学良");
        user6.setUserid("1007");
        user6.setFid("07");

        contactList.add(easeUser);
        contactList.add(user1);
        contactList.add(user4);
        contactList.add(user7);
        contactList.add(user3);
        contactList.add(user2);
        contactList.add(user5);
        contactList.add(user6);
    }
}
