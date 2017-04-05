package im.boss66.com.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.book.SelectContactsActivity;
import im.boss66.com.activity.im.ChatActivity;
import im.boss66.com.activity.treasure.MainTreasureActivity;
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
    private TextView tv_treasure;
    private RelativeLayout rl_top_bar;
    private Handler handler = new Handler();
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Session.getInstance().addObserver(this);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_homepage, null);
//            initRootView();
            initViews(view);
        }
        return view;
//        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
//    }

    private void initViews(View view) {
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        ivAdd = (ImageView) view.findViewById(R.id.iv_add);
        tv_treasure = (TextView) view.findViewById(R.id.tv_treasure);
        ivAdd.setOnClickListener(this);
        tv_treasure.setOnClickListener(this);

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ConversationAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListner());
        listView.setOnItemLongClickListener(new ItemLongClickListener());
        initDatas();
    }

    private void initDatas() {
        ArrayList<BaseConversation> list = (ArrayList<BaseConversation>) ConversationHelper.getInstance().query();
        Log.i("info", "======list.size():" + list.size());
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
            case R.id.tv_treasure:
                openActivity(MainTreasureActivity.class, null);
                break;
        }
    }

    private void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("info", "===============HomePager中onResume()");
    }


    @Override
    public void onPause() {
        Log.i("info", "===============HomePager中onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private class ItemClickListner implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BaseConversation entity = (BaseConversation) adapterView.getItemAtPosition(i);
            String key = PrefKey.UN_READ_NEWS_KEY + "/" + entity.getConversation_id();
            PreferenceUtils.putInt(getActivity(), key, 0);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            if (entity.getNewest_msg_type().equals("group")) {
                intent.putExtra("isgroup", true);
            } else {
                intent.putExtra("isgroup", false);
            }
//            intent.putExtra("uid1", getArguments().getString("userid", ""));
            intent.putExtra("toUid", entity.getConversation_id());
            intent.putExtra("title", entity.getUser_name());
            intent.putExtra("toAvatar", entity.getAvatar());
            startActivity(intent);
        }
    }

    private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            BaseConversation entity = (BaseConversation) adapterView.getItemAtPosition(i);
            if (entity != null) {
                showDeleteDialog(entity);
            }
            return true;
        }
    }

    private void showDeleteDialog(final BaseConversation conversation) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.item_dialog_notification, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ((TextView) view.findViewById(R.id.title)).setText("提示");
        ((TextView) view.findViewById(R.id.message)).setText("确定删除该条聊天记录吗?");
        view.findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(conversation);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteItem(BaseConversation conversation) {
        ConversationHelper.getInstance().deleteByConversationId(conversation.getConversation_id());
        ArrayList<BaseConversation> conversations = (ArrayList<BaseConversation>) adapter.getData();
        for (BaseConversation mode : conversations) {
            if (mode.getConversation_id().equals(conversation.getConversation_id())) {
                adapter.remove(mode);
                break;
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_REFRSH_CONVERSATION_PAGE) {
            MycsLog.i("info", "===================update()");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    freshPager();
                }
            });
        }
    }

    private void freshPager() {
        ArrayList<BaseConversation> list = (ArrayList<BaseConversation>) ConversationHelper.getInstance().query();
        Log.i("info", "list:" + list + "list.size()" + "" + list.size());
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
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("uid1", userid);
//                intent.putExtra("uid2", uid);
//                startActivity(intent);
                Intent intent = new Intent(context, SelectContactsActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.tv_add_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
//                Intent intent = new Intent(context, TestActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(context, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.tv_scanning).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Intent intent = new Intent(context, CaptureActivity.class);
                startActivity(intent);
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
