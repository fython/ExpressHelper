package me.imid.swipebacklayout.lib.app;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * @author Yrom
 * @author PeterCxy
 */
public class SwipeBackActivityHelper {
    protected ActionBarActivity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(ActionBarActivity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mSwipeBackLayout = new SwipeBackLayout(mActivity, getGlobalContext());
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }
	
	protected Context getGlobalContext() {
        return mActivity;
    }
}
