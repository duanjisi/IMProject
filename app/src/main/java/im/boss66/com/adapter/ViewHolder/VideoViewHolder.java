package im.boss66.com.adapter.ViewHolder;

import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;

/**
 * Created by suneee on 2016/8/16.
 */
public class VideoViewHolder extends CircleViewHolder {

    public ImageView iv_video_play;
    public ImageView iv_video_bg;
    public FrameLayout fl_video;
    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.viewstub_videobody_circle);
        View subView = viewStub.inflate();
        fl_video = (FrameLayout) subView.findViewById(R.id.fl_video);
        ImageView videoBody = (ImageView) subView.findViewById(R.id.iv_video_play);
        ImageView iv_video_bg = (ImageView) subView.findViewById(R.id.iv_video_bg);

        if(videoBody!=null){
            this.iv_video_play = videoBody;
        }
        if (iv_video_bg != null){
            this.iv_video_bg = iv_video_bg;
        }
    }
}
