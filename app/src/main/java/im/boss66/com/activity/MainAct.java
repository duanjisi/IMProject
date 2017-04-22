package im.boss66.com.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.db.dao.EmoLoveHelper;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.entity.BaseEmoCollection;
import im.boss66.com.entity.EmoLove;
import im.boss66.com.entity.MyInfo;
import im.boss66.com.entity.UpdateInfoEntity;
import im.boss66.com.fragment.ContactBooksFragment;
import im.boss66.com.fragment.ContactsFragment;
import im.boss66.com.fragment.DiscoverFragment;
import im.boss66.com.fragment.HomePagerFragment;
import im.boss66.com.fragment.MainTreasureFragment;
import im.boss66.com.fragment.MineFragment;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.CheckUpdateRequest;
import im.boss66.com.http.request.EmoCollectionsRequest;
import im.boss66.com.http.request.MyInfoRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.services.ChatServices;
import im.boss66.com.services.MyPushIntentService;
import im.boss66.com.task.EmoDbTask;
import im.boss66.com.task.ZipExtractorTask;
import im.boss66.com.util.AutoUpdateUtil;
import im.boss66.com.widget.CircleImageView;
import im.boss66.com.widget.SlidingMenu;
import im.boss66.com.widget.dialog.PeopleDataDialog;

/**
 * Created by Johnny on 2017/4/20.
 */
public class MainAct extends BaseActivity implements CompoundButton.OnCheckedChangeListener, Observer {
    private final static String TAG = MainAct.class.getSimpleName();
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private SlidingMenu slidingMenu;
    private CircleImageView iv_avatar;
    private TextView tv_name;
    private ImageLoader imageLoader;
    private MainTreasureFragment mainTreasureFragment;
    private HomePagerFragment homePagerFragment;
    private ContactBooksFragment contactBooksFragment;
    private ContactsFragment contactsFragment;
    private DiscoverFragment discoverFragment;
    private MineFragment mineFragment;
    private AccountEntity account;
    private RadioGroup mRadioGroup;
    private RadioButton rbTreasure, rbHomePager, rbBooks, rbContacts, rbDiscover, rbMine;
    private int mCheckedId = R.id.rb_treasure_pager;
    private PeopleDataDialog peopleDataDialog;
    private PushAgent mPushAgent;
    private MyInfo myInfo;
    private List<MyInfo.ResultBean.SchoolListBean> school_list; //学校
    private List<MyInfo.ResultBean.SchoolListBean> hometown_list; //家乡
    private PermissionListener permissionListener;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (school_list != null && school_list.size() > 0 && hometown_list != null && hometown_list.size() > 0) {
                        SharedPreferencesMgr.setBoolean("setSuccess2", true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session.getInstance().addObserver(this);
        Fresco.initialize(context);//注册，在setContentView之前。
        App.getInstance().addTempActivity(this);
        setContentView(R.layout.act_main);
        EventBus.getDefault().register(this);
        initViews();
    }

    private void initViews() {
        currentTabIndex = 2;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        account = App.getInstance().getAccount();
        iv_avatar = (CircleImageView) findViewById(R.id.iv_avatar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        slidingMenu = (SlidingMenu) findViewById(R.id.id_menu);
        mRadioGroup = (RadioGroup) findViewById(R.id.bottom_navigation_rg);
        rbTreasure = (RadioButton) findViewById(R.id.rb_treasure_pager);
        rbHomePager = (RadioButton) findViewById(R.id.rb_home_pager);
        rbBooks = (RadioButton) findViewById(R.id.rb_contact_book);
        rbDiscover = (RadioButton) findViewById(R.id.rb_discover);
        rbMine = (RadioButton) findViewById(R.id.rb_mine);
        rbContacts = (RadioButton) findViewById(R.id.rb_contact);

        rbTreasure.setOnCheckedChangeListener(this);
        rbMine.setOnCheckedChangeListener(this);
        rbHomePager.setOnCheckedChangeListener(this);
        rbBooks.setOnCheckedChangeListener(this);
        rbDiscover.setOnCheckedChangeListener(this);
        rbContacts.setOnCheckedChangeListener(this);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.replaced_layout);
        if (fragment == null) {
            initFragment();
        }
        checkFragment();

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable(mRegisterCallback);
        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

        ChatServices.startChatService(context);
        getPermission(PermissionUtil.PERMISSIONS_SD_READ_WRITE);
        requestLoveStore();
        checkUpdate();
        tv_name.setText(account.getUser_name());
        imageLoader.displayImage(account.getAvatar(), iv_avatar, ImageLoaderUtils.getDisplayImageOptions());

        if (SharedPreferencesMgr.getBoolean("setSuccess2", false)) {
            return;
        }
        initData();


    }

    private void initFragment() {
        mainTreasureFragment = new MainTreasureFragment();
        homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userid", account.getUser_id());
        homePagerFragment.setArguments(bundle);
        contactBooksFragment = new ContactBooksFragment();
        contactsFragment = new ContactsFragment();
        discoverFragment = new DiscoverFragment();
        mineFragment = new MineFragment();

        App.getInstance().setFragment(contactBooksFragment);
        mFragments.add(mainTreasureFragment);
        mFragments.add(homePagerFragment);
        mFragments.add(contactBooksFragment);
        mFragments.add(contactsFragment);
        mFragments.add(discoverFragment);
        mFragments.add(mineFragment);
    }

    private int index;
    private int currentTabIndex;

    private void checkFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFragments.size() == 0) {
            initFragment();
        }
        if (currentTabIndex != index) {
            transaction.hide(mFragments.get(currentTabIndex));
            if (!mFragments.get(index).isAdded()) {
                transaction.add(R.id.replaced_layout, mFragments.get(index));
            }
            transaction.show(mFragments.get(index)).commit();
        }
        currentTabIndex = index;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        int id = buttonView.getId();
        switch (id) {
            case R.id.rb_treasure_pager:
                mCheckedId = id;
                index = 0;
                checkFragment();
                break;
            case R.id.rb_home_pager:
                mCheckedId = id;
                index = 1;
                checkFragment();
                break;
            case R.id.rb_contact_book:
                mCheckedId = id;
                index = 2;
                checkFragment();
                break;
            case R.id.rb_contact:
                mCheckedId = id;
                index = 3;
                checkFragment();
//                设置成功不弹窗
                if (SharedPreferencesMgr.getBoolean("setSuccess2", false)) {
                    slidingMenu.toggle();
                    return;
                }
                if (peopleDataDialog == null) {
                    peopleDataDialog = new PeopleDataDialog(context);
                    peopleDataDialog.show();
                } else {
                    if (!peopleDataDialog.isShowing()) {
                        peopleDataDialog.show();
                    }
                }
                break;
            case R.id.rb_discover:
                mCheckedId = id;
                index = 4;
                checkFragment();
                break;
            case R.id.rb_mine:
                mCheckedId = id;
                index = 5;
                checkFragment();
                break;
        }
        slidingMenu.toggle();
    }

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            slidingMenu.toggle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mFragments != null) {
            mFragments.clear();
            mFragments = null;
        }
        mainTreasureFragment = null;
        homePagerFragment = null;
        contactsFragment = null;
        contactBooksFragment = null;
        discoverFragment = null;
        mineFragment = null;
        if (mPushAgent.isEnabled() || UmengRegistrar.isRegistered(context)) {
            //开启推送并设置注册的回调处理
            mPushAgent.disable(mUnregisterCallback);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        SessionInfo sin = (SessionInfo) data;
        if (sin.getAction() == Session.ACTION_APPLICATION_EXIT) {
            finish();
        } else if (sin.getAction() == Session.ACTION_CONTACTS_REFRESH_PAGER) {
            Log.i("info", "==========================来新消息了");
        }
    }

    private void initData() {
        MyInfoRequest request = new MyInfoRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String str) {
                myInfo = JSON.parseObject(str, MyInfo.class);
                hometown_list = myInfo.getResult().getHometown_list();
                school_list = myInfo.getResult().getSchool_list();
                handler.obtainMessage(1).sendToTarget();

            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);

            }
        });
    }

    private void checkUpdate() {
        CheckUpdateRequest request = new CheckUpdateRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<UpdateInfoEntity>() {
            @Override
            public void onSuccess(UpdateInfoEntity response) {
                AutoUpdateUtil.update(response.getVersion(), context, response.getApk_url(),
                        response.getMsg(), response.getForce() != 0, false);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void requestLoveStore() {
        EmoCollectionsRequest request = new EmoCollectionsRequest(TAG, "", "", "1");
        request.send(new BaseDataRequest.RequestCallback<BaseEmoCollection>() {
            @Override
            public void onSuccess(BaseEmoCollection pojo) {
                saveToDb(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void saveToDb(BaseEmoCollection pojo) {
        if (pojo != null) {
            ArrayList<EmoLove> loves = pojo.getResult();
            if (loves != null && loves.size() != 0) {
                EmoLoveHelper.getInstance().save(loves);
            }
        }
    }

    private void getPermission(String[] permissions) {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                boolean isInitEmo = PreferenceUtils.getBoolean(context, PrefKey.EMO_SYSTEM_INIT, true);
                if (isInitEmo) {
                    initSystemEmos();
                }
            }

            @Override
            public void onRequestPermissionError() {
                showToast(getString(R.string.giving_system_permissions), true);
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        permissions//相机权限
                ).request(permissionListener);
    }

    private void initSystemEmos() {
        String savePath = Constants.EMO_DIR_PATH;
        FileUtils.getInstance(context).copyAssetsToSD("emo", savePath).
                setFileOperateCallback(new FileUtils.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i("info", "====系统表情已拷贝至Sd卡");
                        ZipTask();
                    }

                    @Override
                    public void onFailed(String error) {
                        showToast("初始化系统表情失败!", true);
                    }
                });
    }

    private void ZipTask() {
        final String fileName = assetsEmos();
        if (fileName != null && !fileName.equals("")) {
            final File file = new File(Constants.EMO_DIR_PATH, fileName);
            ZipExtractorTask task = new ZipExtractorTask(file.getPath(), Constants.EMO_DIR_PATH,
                    context, true, new ZipExtractorTask.Callback() {
                @Override
                public void onPreExecute() {

                }

                @Override
                public void onComplete() {//解压完成，删除压缩包文件
                    FileUtil.deleteFile(file.getPath().toString());
                    String filepath = Constants.EMO_DIR_PATH + File.separator + fileName.replace(".zip", ".json");
                    EmoDbTask dbTask = new EmoDbTask(filepath, new EmoDbTask.dbTaskCallback() {//解析.json文件。保持db数据
                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onComplete() {
                            PreferenceUtils.putBoolean(context, PrefKey.EMO_SYSTEM_INIT, false);
                            Log.i("info", "====系统表情初始化成功!");
                        }
                    });
                    dbTask.execute();
                }
            });
            task.execute();
        }
    }


    private String assetsEmos() {
        try {
            return this.getAssets().list("emo")[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
        @Override
        public void onRegistered(final String registrationId) {
            // TODO Auto-generated method stub
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
//                    updateStatus();
                    Log.i("info", "registrationId:" + registrationId);
                    Log.i("info", "deviceToken:" + mPushAgent.getRegistrationId());
                    new AddAliasTask(account.getUser_id(), "QQ").execute();
                }
            });
        }
    };

    public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {
        @Override
        public void onUnregistered(String registrationId) {
            // TODO Auto-generated method stub
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
//                    updateStatus();
                }
            }, 2000);
        }
    };

    class AddAliasTask extends AsyncTask<Void, Void, Boolean> {
        String alias;
        String aliasType;

        public AddAliasTask(String aliasString, String aliasTypeString) {
            // TODO Auto-generated constructor stub
            this.alias = aliasString;
            this.aliasType = aliasTypeString;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                return mPushAgent.addAlias(alias, aliasType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (Boolean.TRUE.equals(result)) {
                Log.i(TAG, "alias was set successfully.");
            }
        }
    }
}
