package info.papdt.express.helper.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Utility;

public abstract class AbsActivity extends ActionBarActivity {

	protected Toolbar mToolbar;
	protected ActionBar mActionBar;

	protected int statusBarHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/** Set up translucent status bar */
		if (Build.VERSION.SDK_INT >= 19 && !Utility.isChrome()) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
		}

		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}

		super.onCreate(savedInstanceState);
	}

	protected abstract void setUpViews();

	@Override
	public void setContentView(@LayoutRes int layoutResId) {
		super.setContentView(layoutResId);

		try {
			View statusHeaderView = findViewById(R.id.statusHeaderView);
			statusHeaderView.getLayoutParams().height = statusBarHeight;
		} catch (NullPointerException e) {

		}

		try {
			mToolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(mToolbar);
		} catch (Exception e) {

		}
		mActionBar = getSupportActionBar();

		setUpViews();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			super.onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
