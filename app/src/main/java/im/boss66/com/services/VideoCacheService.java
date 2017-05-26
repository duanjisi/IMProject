package im.boss66.com.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import im.boss66.com.Constants;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.util.Utils;

/**
 * Created by Johnny on 2017/5/17.
 */
public class VideoCacheService extends Service {
    private static final String VIDEO_CACHE_PATH = Constants.VIDEO_CACHE_PATH;
    private HttpHandler handler = null;
    private String videoPath;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            videoPath = intent.getExtras().getString("video_path", "");
            if (!TextUtils.isEmpty(videoPath)) {
                LoadFileTask();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void LoadFileTask() {
        if (!TextUtils.isEmpty(videoPath)) {
            HttpUtils httpUtils = new HttpUtils();
            final String fileName = FileUtils.getFileNameFromPath(videoPath);
            final String target = VIDEO_CACHE_PATH + fileName;
            handler = httpUtils.download(videoPath, target, true, false,
                    new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            // TODO Auto-generated method stub
                            Utils.saveVideoFile(VideoCacheService.this, target, System.currentTimeMillis());
                            cancelLoading();
                            EventBus.getDefault().post(new ActionEntity(Constants.Action.VIDEO_CACHE_SUCCESSED));
                            stopCurrentService(getBaseContext());
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            cancelLoading();
                            EventBus.getDefault().post(new ActionEntity(Constants.Action.VIDEO_CACHE_FAILE));
//                            LoadFileTask();
                        }
                    });
        }
    }

    private void cancelLoading() {
        if (handler != null) {
            handler.cancel(true);
            handler = null;
        }
    }


    public static void stopCurrentService(Context context) {
        Intent iService = new Intent(context, VideoCacheService.class);
        iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.stopService(iService);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
