package im.boss66.com.activity.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.EmoStoreAdapter;
import im.boss66.com.entity.BaseEmoStore;
import im.boss66.com.entity.EmoStore;
import im.boss66.com.entity.HotWordEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoStoreRequest;
import im.boss66.com.http.request.HotWordsRequest;

/**
 * Created by Johnny on 2017/2/20.
 * 表情包商店搜索
 */
public class EmojiStoreSearchActivity extends BaseActivity {
    private final static String TAG = EmojiStoreSearchActivity.class.getSimpleName();
    private int pager = 1;
    private int pagerNum = 10;
    private TextView tvBack;
    private EditText editText;
    private ListView lvHot, lvEmo;
    private HistoryAdapter historyAdapter;
    private EmoStoreAdapter storeAdapter;
    /**
     * 设置布局显示目标最大化
     */
    private LinearLayout.LayoutParams WClayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
    private ProgressBar progressBar;
    private View rootView;

    private void initRootView() {
        LinearLayout layout = new LinearLayout(context);
        //设置布局 水平方向
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //进度条
        progressBar = new ProgressBar(context);
        //进度条显示位置
        progressBar.setPadding(0, 0, 15, 0);
        layout.addView(progressBar, WClayoutParams);

        TextView textView = new TextView(context);
        textView.setText("加载中...");
        textView.setGravity(Gravity.CENTER_VERTICAL);

        layout.addView(textView, FFlayoutParams);
        layout.setGravity(Gravity.CENTER);

        LinearLayout loadingLayout = new LinearLayout(context);
        loadingLayout.addView(layout, WClayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        rootView = loadingLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emo_store_search);
        initRootView();
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        editText = (EditText) findViewById(R.id.et_keyword);
        lvHot = (ListView) findViewById(R.id.lv_hot);
        lvEmo = (ListView) findViewById(R.id.lv_emo);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String keyWords = getText(editText);
                    if (keyWords != null && !keyWords.equals("")) {
                        requestSearchEmos(keyWords);
                    }
                    return true;
                }
                return false;
            }
        });

        historyAdapter = new HistoryAdapter(context);
        lvHot.setAdapter(historyAdapter);
        lvHot.setOnItemClickListener(new ItemHotClickListener());

        storeAdapter = new EmoStoreAdapter(context);
        lvEmo.setAdapter(storeAdapter);
        lvEmo.setOnItemClickListener(new ItemEmoClickListener());
        lvEmo.setOnScrollListener(new scrollListener());
        reuqestHotWords();
    }


    private void reuqestHotWords() {
        showLoadingDialog();
        HotWordsRequest request = new HotWordsRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<HotWordEntity>() {
            @Override
            public void onSuccess(HotWordEntity pojo) {
                cancelLoadingDialog();
                initHot(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void initHot(HotWordEntity hotWordEntity) {
        if (hotWordEntity != null) {
            ArrayList<String> list = hotWordEntity.getResult();
            if (list.size() != 0) {
                historyAdapter.addDatas(list);
            }
        }
    }

    private class ItemHotClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String key = (String) adapterView.getItemAtPosition(i);
            requestSearchEmos(key);
        }
    }

    private class ItemEmoClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoStore store = (EmoStore) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(context, EmojiGroupDetailsActivity.class);
            intent.putExtra("packid", store.getGroup_id());
            startActivity(intent);
        }
    }


    private class scrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    //加载更多功能的代码
                    requestNextPager();
                }
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    }

    private class HistoryAdapter extends BaseAdapter {
        private List<String> items;
        private Context mContext;
        private LayoutInflater mInflater;

        public HistoryAdapter(Context mContext) {
            this.mContext = mContext;
            mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            if (items == null) {
                items = new ArrayList<>();
            }
        }

        public void addDatas(List<String> entities) {
            if (items != null) {
                items.clear();
                items.addAll(entities);
            }
            notifyDataSetChanged();
        }


        public void clearAllDatas() {
            if (items.size() != 0) {
                items.clear();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_hot_cell, null);
                holder.name = (TextView) view.findViewById(R.id.title_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(items.get(i));
            return view;
        }

        private class ViewHolder {
            TextView name;
        }
    }

    private void requestSearchEmos(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            showToast("关键字为空!", true);
            return;
        }
        showLoadingDialog();
        EmoStoreRequest request = new EmoStoreRequest(TAG, keyWord, "" + pager, "" + pagerNum);
        request.send(new BaseDataRequest.RequestCallback<BaseEmoStore>() {
            @Override
            public void onSuccess(BaseEmoStore pojo) {
                cancelLoadingDialog();
                initEmoStores(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }


    private void initEmoStores(BaseEmoStore baseEmoStore) {
        if (baseEmoStore != null) {
            ArrayList<EmoStore> stores = baseEmoStore.getResult();
            if (stores.size() != 0) {
                UIUtils.hindView(lvHot);
                UIUtils.showView(lvEmo);
                pager++;
                storeAdapter.initData(stores);
                lvEmo.addFooterView(rootView);
            }
        }
    }

    private void requestNextPager() {
        EmoStoreRequest request = new EmoStoreRequest(TAG, "" + pager, pagerNum);
        request.send(new BaseDataRequest.RequestCallback<BaseEmoStore>() {
            @Override
            public void onSuccess(BaseEmoStore pojo) {
                nextPager(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void nextPager(BaseEmoStore emoStore) {
        ArrayList<EmoStore> bags = emoStore.getResult();
        if (bags != null && bags.size() != 0) {
            pager++;
            storeAdapter.addData(bags);
        } else {
            lvEmo.removeFooterView(rootView);
            showToast("数据全部加载完!", true);
        }
    }
}
