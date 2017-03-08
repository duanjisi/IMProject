package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/6.
 */
public class BaseEditEmo {
    private ArrayList<EmoEntity> emo = new ArrayList<>();

    public ArrayList<EmoEntity> getEmo() {
        return emo;
    }

    public void setEmo(ArrayList<EmoEntity> emo) {
        this.emo = emo;
    }
}
