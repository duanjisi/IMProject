package im.boss66.com.task;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import im.boss66.com.db.dao.EmoCateHelper;
import im.boss66.com.entity.EmoCate;
import im.boss66.com.entity.EmoClasses;

/**
 * Created by Johnny on 2017/2/21.
 */
public class EmoDbTask extends AsyncTask<Void, Integer, Long> {

    private String filePath;
    private dbTaskCallback callback;

    public EmoDbTask(String filePath, dbTaskCallback callback) {
        super();
        this.callback = callback;
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onPreExecute();
        }
    }

    @Override
    protected Long doInBackground(Void... voids) {
        if (filePath != null && !filePath.equals("")) {
            parseJson(filePath);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        if (callback != null) {
            callback.onComplete();
        }
    }

    private void parseJson(String path) {
        String json = getFileFromSD(path);
        Log.i("info", "=====json:" + json);
        if (!json.equals("")) {
            EmoClasses classes = JSON.parseObject(json, EmoClasses.class);
            Log.i("info", "=====Createtime:" + classes.getCreatetime());
            if (classes != null) {
                ArrayList<EmoCate> cates = classes.getCategory();
                if (cates.size() != 0) {
                    Log.i("info", "=====开始保存:" + classes.getCreatetime());
                    EmoCateHelper.getInstance().save(cates);
                }
            }
        }
    }

    private String getFileFromSD(String path) {
        String result = "";
        try {
            FileInputStream f = new FileInputStream(path);
            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
            String line = "";
            while ((line = bis.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public interface dbTaskCallback {
        void onComplete();

        void onPreExecute();
    }
}
