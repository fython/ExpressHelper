package info.papdt.express.helper.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import info.papdt.express.helper.R;
import info.papdt.express.helper.port.ExpressHelper;
import info.papdt.express.helper.port.support.Express;

public class TestActivity extends AbsActivity {

	private TextView mTextView;

	private Express mExpress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	}

	@Override
	protected void setUpViews() {
		mTextView = (TextView) findViewById(R.id.tv_test);
		findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread() {
					@Override
					public void run() {
						mExpress = ExpressHelper.getExpressInfo("顺丰", "233333333333");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mTextView.setText(mExpress.toJSONObject().toString());
							}
						});
					}
				}.start();
			}
		});
		findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ExpressHelper.addExpressTrackToApp(getApplicationContext(), mExpress)) {
					mTextView.setText("添加成功\n" + mTextView.getText().toString());
				} else {
					mTextView.setText("添加失败，可能水表助手未安装\n" + mTextView.getText().toString());
				}
			}
		});
	}

}
