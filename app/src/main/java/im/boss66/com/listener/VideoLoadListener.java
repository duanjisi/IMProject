package im.boss66.com.listener;

import android.media.MediaPlayer;

import im.boss66.com.widget.TextureVideoView;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public interface VideoLoadListener {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);

}
