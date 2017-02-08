package im.boss66.com.listener;

import android.view.View;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public interface CircleListItemListener {

    /**
     * When view visibility become bigger than "current active" view visibility then the new view becomes active.
     * This method is called
     */
    void setActive(View newActiveView, int newActiveViewPosition);

    /**
     * There might be a case when not only new view becomes active, but also when no view is active.
     * When view should stop being active this method is called
     */
    void deactivate(View currentView, int position);

}
