package im.boss66.com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.adapter.EaseContactAdapter;
import im.boss66.com.domain.EaseUser;

public class EaseBookList extends RelativeLayout {
    protected static final String TAG = EaseBookList.class.getSimpleName();

    protected Context context;
    protected RefreshListView listView;
    protected EaseContactAdapter adapter;
    protected List<EaseUser> contactList;
    protected EaseSidebar sidebar;

    protected int primaryColor;
    protected int primarySize;
    protected boolean showSiderBar;
    protected Drawable initialLetterBg;

    static final int MSG_UPDATE_LIST = 0;
    static final int MSG_UPDATE_LIST2 = 1;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LIST:
                    if (adapter != null) {
//                        adapter.clear();
//                        adapter.addAll(new ArrayList<EaseUser>(contactList));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_UPDATE_LIST2:
                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(new ArrayList<EaseUser>(contactList));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected int initialLetterColor;


    public EaseBookList(Context context) {
        super(context);
        init(context, null);
    }

    public EaseBookList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseBookList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseContactList);
        primaryColor = ta.getColor(R.styleable.EaseContactList_ctsListPrimaryTextColor, 0);
        primarySize = ta.getDimensionPixelSize(R.styleable.EaseContactList_ctsListPrimaryTextSize, 0);
        showSiderBar = ta.getBoolean(R.styleable.EaseContactList_ctsListShowSiderBar, true);
        initialLetterBg = ta.getDrawable(R.styleable.EaseContactList_ctsListInitialLetterBg);
        initialLetterColor = ta.getColor(R.styleable.EaseContactList_ctsListInitialLetterColor, 0);
        ta.recycle();


        LayoutInflater.from(context).inflate(R.layout.ease_widget_book_list, this);
        listView = (RefreshListView) findViewById(R.id.list);
        sidebar = (EaseSidebar) findViewById(R.id.sidebar);
        if (!showSiderBar)
            sidebar.setVisibility(View.GONE);
    }

    /*
     * init view
     */
    public void init(List<EaseUser> contactList) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);

        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    public void removeItem(String uid) {
        if (adapter != null) {
            List<EaseUser> list = adapter.getUserList();
            Iterator<EaseUser> stringIterator = list.iterator();
            while (stringIterator.hasNext()) {
                EaseUser s = stringIterator.next();
                if (s.getUserid().equals(uid)) {
                    stringIterator.remove();
                    adapter.remove(s);
                }
            }
        }
    }

    public void init(List<EaseUser> contactList, View header) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        if (header != null) {
            listView.addHeaderView(header);
        }
        listView.setAdapter(adapter);
        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    public void init(List<EaseUser> contactList, View header, boolean isshowTag) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setShow(isshowTag);
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        if (header != null) {
            listView.addHeaderView(header);
        }
        listView.setAdapter(adapter);
        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    public void refreshDatas(List<EaseUser> contactList) {
        this.contactList.clear();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(contactList);
        }
    }


    public void init(List<EaseUser> contactList, int resId) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, resId, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);

        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    public void init(List<EaseUser> contactList, int resId, View header) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, resId, new ArrayList<EaseUser>(contactList));
        adapter.setPhoneContact(true);
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        if (header != null) {
            listView.addHeaderView(header);
        }
        listView.setAdapter(adapter);

        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    /*
        * init view
        */
    public void init(List<EaseUser> contactList, boolean isshowTag) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setShow(isshowTag);
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);
        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }

    public void refresh() {
        Log.i("info", "========refresh()");
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void refresh(List<EaseUser> contactList) {
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST2);
        handler.sendMessage(msg);
    }

    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }

    public RefreshListView getListView() {
        return listView;
    }

    public List<EaseUser> getList() {
        return contactList;
    }

    public EaseContactAdapter getAdapter() {
        return adapter;
    }

    public void setShowSiderBar(boolean showSiderBar) {
        if (showSiderBar) {
            sidebar.setVisibility(View.VISIBLE);
        } else {
            sidebar.setVisibility(View.GONE);
        }
    }
}