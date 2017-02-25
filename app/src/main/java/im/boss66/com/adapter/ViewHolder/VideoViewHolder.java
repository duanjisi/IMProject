package im.boss66.com.adapter.ViewHolder;

import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;

import im.boss66.com.R;

/**
 * Created by suneee on 2016/8/16.
 */
public class VideoViewHolder extends CircleViewHolder {

    public ImageView iv_video_play;
    public ImageView iv_video_bg;
    private int sceenw;
    public VideoViewHolder(View itemView,int sceenw){
        super(itemView, TYPE_VIDEO);
        this.sceenw = sceenw;
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.viewstub_videobody_circle);
        View subView = viewStub.inflate();

        ImageView videoBody = (ImageView) subView.findViewById(R.id.iv_video_play);
        ImageView iv_video_bg = (ImageView) subView.findViewById(R.id.iv_video_bg);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv_video_bg.getLayoutParams();
        params.width = sceenw/4;
        params.height = sceenw/2;
        iv_video_bg.setLayoutParams(params);

        FrameLayout.LayoutParams params_p = (FrameLayout.LayoutParams) videoBody.getLayoutParams();
        params_p.width = sceenw/8;
        params_p.height = sceenw/8;
        videoBody.setLayoutParams(params_p);

        if(videoBody!=null){
            this.iv_video_play = videoBody;
        }
        if (iv_video_bg != null){
            this.iv_video_bg = iv_video_bg;
        }
    }
}
