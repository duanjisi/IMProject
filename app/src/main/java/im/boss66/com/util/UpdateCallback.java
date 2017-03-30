package im.boss66.com.util;

import android.view.View;

import im.boss66.com.entity.EmoBagEntity;

/**
 * Created by Johnny on 2017/3/27.
 */
public interface UpdateCallback {
    void startProgress(EmoBagEntity model, int position);

    void freshProgress(View view, EmoBagEntity model, int position);
}
