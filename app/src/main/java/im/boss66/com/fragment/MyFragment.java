package im.boss66.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.paging.gridview.PagingGridView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.im.EmojiEditActivity;
import im.boss66.com.adapter.EmojiAdapter;
import im.boss66.com.entity.BaseEditEmo;
import im.boss66.com.entity.BaseEmo;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EditEmosAllRequest;
import im.boss66.com.http.request.EditEmosCateRequest;
import im.boss66.com.http.request.EmosSearchRequest;

public class MyFragment extends BaseFragment {
    private static final String TAG = MyFragment.class.getSimpleName();
    private String title, cateid;
    private int pager = 1;
    private int pagerNum = 20;
    private View view;
    private RelativeLayout rlNotify;
    private Button btnRefresh;
    private PagingGridView gridView;
    private EmojiAdapter adapter;
    private boolean isCompleted = false;
    private String keywords = "";

    public void setCateid(String cateid) {
        this.cateid = cateid;
    }

    public static MyFragment newInstance(String title, String cateid) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("cateid", cateid);
        fragment.setArguments(args);
        return fragment;
    }

    public void search(String key) {
        Log.i("info", "===================search.key:" + key);
        this.keywords = key;
        pager = 1;
        showLoadingDialog();
        EmosSearchRequest request = new EmosSearchRequest(TAG, key, cateid, "" + pager, "" + pagerNum);
        request.send(new BaseDataRequest.RequestCallback<BaseEmo>() {
            @Override
            public void onSuccess(BaseEmo pojo) {
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

    private void bindDatas(BaseEmo baseEmo) {
        if (baseEmo != null) {
//            initGridView();
            adapter.removeAllItems();
            ArrayList<EmoEntity> entities = baseEmo.getResult();
            Log.i("info", "=============entities.size():" + entities.size());
            if (entities != null && entities.size() != 0) {
                pager++;
                if (entities.size() < pagerNum) {
                    isCompleted = true;
                    gridView.onFinishLoading(false, null);
                }
                adapter.addMoreItems(entities);
            } else {
                isCompleted = true;
                gridView.onFinishLoading(false, null);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title", "");
            cateid = bundle.getString("cateid", "");
            Log.i("info", "=======title:" + title + "\n" + "cateid:" + cateid);
        }
//        title = getArguments() != null ? getArguments().getString("title") : "";
//        cateid = getArguments() != null ? getArguments().getString("cateid") : "";
    }

    /**
     * 为Fragment加载布局时调用
     **/
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_pager_list, null);
            initViews(view);
        }
        return view;
    }

    private void initViews(View view) {
        rlNotify = (RelativeLayout) view.findViewById(R.id.rl_notify);
        btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        gridView = (PagingGridView) view.findViewById(R.id.listView);
        adapter = new EmojiAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        gridView.setHasMoreItems(true);
        gridView.setPagingableListener(new PagingGridView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (isCompleted) {
                    gridView.onFinishLoading(false, null);
                } else {
                    if (TextUtils.isEmpty(keywords)) {
                        loadMore();
                    } else {
                        loadSearchMore();
                    }
                }
            }
        });
        requestEmos();
    }

    private void initGridView() {
        gridView = (PagingGridView) view.findViewById(R.id.listView);
        adapter = new EmojiAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        gridView.setHasMoreItems(true);
        gridView.setPagingableListener(new PagingGridView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (isCompleted) {
                    gridView.onFinishLoading(false, null);
                } else {
                    if (TextUtils.isEmpty(keywords)) {
                        loadMore();
                    } else {
                        loadSearchMore();
                    }
                }
            }
        });
    }

    private void loadSearchMore() {
        EmosSearchRequest request = new EmosSearchRequest(TAG, keywords, cateid, "" + pager, "" + pagerNum);
        request.send(new BaseDataRequest.RequestCallback<BaseEmo>() {
            @Override
            public void onSuccess(BaseEmo pojo) {
                cancelLoadingDialog();
                refreshSearchDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void refreshSearchDatas(BaseEmo entity) {
        ArrayList<EmoEntity> entities = entity.getResult();
        if (entities != null && entities.size() != 0) {
            pager++;
            if (entities.size() < pagerNum) {
                isCompleted = true;
                gridView.onFinishLoading(false, null);
            } else {
                gridView.onFinishLoading(true, entities);
            }
        } else {
            isCompleted = true;
            gridView.onFinishLoading(false, null);
        }
    }

    private void loadMore() {
        Log.i("info", "=================loadMore.title:" + title);
        if (TextUtils.isEmpty(title)) {
            return;
        }
        BaseDataRequest request;
        if (title.equals("全部")) {
            request = new EditEmosAllRequest(TAG, "" + pager, "" + pagerNum);
        } else {
            request = new EditEmosCateRequest(TAG, cateid, "" + pager, "" + pagerNum);
        }
        request.send(new BaseDataRequest.RequestCallback<BaseEditEmo>() {
            @Override
            public void onSuccess(BaseEditEmo pojo) {
                refreshDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void refreshDatas(BaseEditEmo entity) {
        ArrayList<EmoEntity> entities = entity.getEmo();
        if (entities != null && entities.size() != 0) {
            pager++;
            if (entities.size() < pagerNum) {
                isCompleted = true;
                gridView.onFinishLoading(false, null);
            } else {
                gridView.onFinishLoading(true, entities);
            }
        } else {
            isCompleted = true;
            gridView.onFinishLoading(false, null);
        }
    }

    public void requestEmos() {
        Log.i("info", "=================requestEmos.title:" + title);
        if (TextUtils.isEmpty(title)) {
            return;
        }
        keywords = "";
        pager = 1;
        BaseDataRequest request;
        if (title.equals("全部")) {
            request = new EditEmosAllRequest(TAG, "" + pager, "" + pagerNum);
        } else {
            request = new EditEmosCateRequest(TAG, cateid, "" + pager, "" + pagerNum);
        }
        showLoadingDialog();
        request.send(new BaseDataRequest.RequestCallback<BaseEditEmo>() {
            @Override
            public void onSuccess(BaseEditEmo pojo) {
                cancelLoadingDialog();
                initDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void initDatas(BaseEditEmo entity) {
//        initGridView();
        ArrayList<EmoEntity> entities = entity.getEmo();
        adapter.removeAllItems();
        if (entities != null && entities.size() != 0) {
            pager++;
            if (entities.size() < pagerNum) {
                isCompleted = true;
                gridView.onFinishLoading(false, null);
            }
            adapter.addMoreItems(entities);
        } else {
            isCompleted = true;
            gridView.onFinishLoading(false, null);
        }
    }

//    private void requestEmos() {
//        if (TextUtils.isEmpty(title)) {
//            return;
//        }
//        BaseDataRequest request;
//        if (title.equals("全部")) {
//            request = new EditEmosAllRequest(TAG, "1", "20");
//        } else {
//            request = new EditEmosCateRequest(TAG, cateid, "1", "20");
//        }
//        showLoadingDialog();
//        request.send(new BaseDataRequest.RequestCallback<BaseEditEmo>() {
//            @Override
//            public void onSuccess(BaseEditEmo pojo) {
//                cancelLoadingDialog();
//                bindDatas(pojo);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                cancelLoadingDialog();
//                showToast(msg, true);
//            }
//        });
//    }
//
//    private void bindDatas(BaseEditEmo baseEditEmo) {
//        if (baseEditEmo != null) {
//            ArrayList<EmoEntity> jokes = baseEditEmo.getEmo();
//            if (jokes != null && jokes.size() != 0) {
//                UIUtils.hindView(rlNotify);
//                UIUtils.showView(gridView);
//                adapter.initData(jokes);
//            } else {
//                UIUtils.hindView(gridView);
//                UIUtils.showView(rlNotify);
//            }
//        }
//    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoEntity entity = (EmoEntity) adapterView.getItemAtPosition(i);
//            intent.putExtra("camera_path", "http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
            String fromat = entity.getEmo_format();
            if (!fromat.equals("gif")) {
                Intent intent = new Intent(getActivity(), EmojiEditActivity.class);
                intent.putExtra("camera_path", entity.getEmo_url());
                startActivity(intent);
            } else {
                showToast("暂不支持gif图编辑", true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }
}
