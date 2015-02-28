package info.papdt.express.helper.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Utility;
import info.papdt.express.helper.ui.fragment.settings.SettingsLicense;
import info.papdt.express.helper.ui.fragment.settings.SettingsMain;

public class SettingsActivity extends AbsActivity {

	private Fragment mFragment;
	private int flag = 0;

	public int statusBarHeight = 0;

	public static final String EXTRA_FLAG = "flag";
	public static final int FLAG_MAIN = 0, FLAG_LICENSE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/** 当SDK >= 19 且 不是Chrome浏览器 时启用透明状态栏 */
		if (Build.VERSION.SDK_INT >= 19 && !Utility.isChrome()) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
		}

		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}

		super.onCreate(savedInstanceState);

		/** 获取传入参数 */
		Intent intent = getIntent();
		flag = intent.getIntExtra(EXTRA_FLAG, FLAG_MAIN);

		setContentView(R.layout.activity_settings);

		mActionBar.setDisplayHomeAsUpEnabled(true);

		/** 初始化状态栏高度 */
		View statusBarView = findViewById(R.id.statusHeaderView);
		statusBarView.getLayoutParams().height = statusBarHeight;

	}

	@Override
	public void setUpViews() {
		switch (flag) {
			case FLAG_MAIN:
				mFragment = SettingsMain.newInstance();
				break;
			case FLAG_LICENSE:
				mFragment = new SettingsLicense();
				break;
		}
		getFragmentManager().beginTransaction()
				.replace(R.id.container, mFragment)
				.commit();
	}


	public static void launchActivity(Activity mActivity, int flag) {
		Intent intent = new Intent(mActivity, SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.putExtra(EXTRA_FLAG, flag);
		mActivity.startActivity(intent);
	}

}
