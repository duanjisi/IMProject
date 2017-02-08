package im.boss66.com.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.ChatActivity;
import im.boss66.com.activity.im.TestActivity;
import im.boss66.com.adapter.ConversationAdapter;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.entity.BaseConversation;

/**
 * Created by Johnny on 2017/1/14.
 */
public class HomePagerFragment extends BaseFragment implements Observer, View.OnClickListener {
    private ListView listView;
    private ConversationAdapter adapter;
    private PopupWindow popupWindow;
    private ImageView ivAdd;
    private RelativeLayout rl_top_bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Session.getInstance().addObserver(this);
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        ivAdd = (ImageView) view.findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ConversationAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListner());
        initDatas();
    }

    private void initDatas() {
        List<BaseConversation> list = ConversationHelper.getInstance().query();
        if (list != null && list.size() != 0) {
            adapter.initData(list);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                if (popupWindow == null) {
                    showPop(getActivity(), rl_top_bar);
                } else {
                    if (!popupWindow.isShowing()) {
                        showPop(getActivity(), rl_top_bar);
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private class ItemClickListner implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BaseConversation entity = (BaseConversation) adapterView.getItemAtPosition(i);
            String key = PrefKey.UN_READ_NEWS_KEY + "/" + entity.getAvatar();
            PreferenceUtils.putInt(getActivity(), key, 0);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            if (entity.getNewest_msg_type().equals("group")) {
                intent.putExtra("isgroup", true);
            } else {
                intent.putExtra("isgroup", true);
            }
            intent.putExtra("uid1", getArguments().getString("userid", ""));
            intent.putExtra("uid2", entity.getUser_id());
            intent.putExtra("title", entity.getUser_name());
            startActivity(intent);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_REFRSH_CONVERSATION_PAGE) {
            MycsLog.i("info", "===================update()");
            freshPager();
        }
    }

    private void freshPager() {
        List<BaseConversation> list = ConversationHelper.getInstance().query();
        MycsLog.i("info", "list:" + list + "list.size()" + "" + list.size());
        if (list != null && list.size() != 0) {
            adapter.initData(list);
        }
    }

    private void showPop(final Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.popwindow_item_select, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim1);
        view.findViewById(R.id.tv_group_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("uid1", userid);
//                intent.putExtra("uid2", uid);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.tv_add_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Intent intent = new Intent(context, TestActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.tv_scanning).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        popupWindow.showAsDropDown(parent);
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }
}
