package info.papdt.express.helper.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Locale;

import info.papdt.express.helper.R;
import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.support.HttpUtils;

public class AddActivity extends AbsActivity {

	private boolean isChecking = false;

	private MaterialEditText et_company, et_number, et_name;
	private ProgressBar mProgress;
	private View btn_done;

	public static final String TAG = "AddActivity";
	
	//918108247993

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
	}

	@Override
	protected void setUpViews() {
		et_company = (MaterialEditText) findViewById(R.id.et_company);
		et_number = (MaterialEditText) findViewById(R.id.et_number);
		et_name = (MaterialEditText) findViewById(R.id.et_name);
		btn_done = findViewById(R.id.btn_done);
		mProgress = (ProgressBar) findViewById(R.id.progressBar);

		btn_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecking) return;
				postData();
			}
		});
	}

	private void postData() {
		if (TextUtils.isEmpty(et_company.getText())) {
			Toast.makeText(
					getApplicationContext(),
					R.string.toast_company_name_empty,
					Toast.LENGTH_SHORT
			).show();
			return;
		}
		if (TextUtils.isEmpty(et_number.getText())) {
			Toast.makeText(
					getApplicationContext(),
					R.string.toast_number_empty,
					Toast.LENGTH_SHORT
			).show();
			return;
		}
		new PostApiTask().execute(et_company.getText().toString(), et_number.getText().toString());
	}

	private void receiveData(String result, String name) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("result", result);
		intent.putExtra("name", name);
		setResult(MainActivity.RESULT_ADD_FINISH, intent);
		finish();
	}

	private class PostApiTask extends AsyncTask<String, Void, String> {

		public static final String FLAG_COMPANY_NOT_EXIST = "company_null",
				FLAG_NETWORK_ERROR = "network_error", FLAG_UNKNOWN_ERROR = "unknown_error",
				FLAG_CLIENT_ERROR = "client_error";

		@Override
		protected void onPreExecute() {
			isChecking = true;
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... src) {
			String[] result = new String[1];
			String companyCode = null;
			String companyName = src[0];
			String mailNumber = src[1];

			for (int i = 0; i < KuaiDi100Helper.CompanyInfo.info.size(); i++){
				if (companyName.toLowerCase(Locale.getDefault()).contains(KuaiDi100Helper.CompanyInfo.info.get(i).get("name"))){
					companyCode = KuaiDi100Helper.CompanyInfo.info.get(i).get("code");
					Log.i(TAG, "Found " + companyName + " and the code of company is " + companyCode);
					break;
				}
			}
			if (companyCode == null){
				return FLAG_COMPANY_NOT_EXIST;
			}

			int resultCode = HttpUtils.get(KuaiDi100Helper.getRequestUrl(null, null, companyCode, mailNumber, "utf8"), result);
			switch (resultCode) {
				case HttpUtils.CODE_OKAY:
					return result[0];
				case HttpUtils.CODE_NONE_200:
				case HttpUtils.CODE_NETWORK_ERROR:
					return FLAG_NETWORK_ERROR;
				case HttpUtils.CODE_CLIENT_ERROR:
					return FLAG_CLIENT_ERROR;
				default:
					return FLAG_UNKNOWN_ERROR;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			isChecking = false;
			mProgress.setVisibility(View.INVISIBLE);
			if (result == null || result == FLAG_UNKNOWN_ERROR) {
				Toast.makeText(
						getApplicationContext(),
						R.string.toast_unknown_error,
						Toast.LENGTH_SHORT
				).show();
				return;
			}
			if (result == FLAG_COMPANY_NOT_EXIST) {
				Toast.makeText(
						getApplicationContext(),
						R.string.toast_company_not_exist,
						Toast.LENGTH_SHORT
				).show();
				return;
			}
			if (result == FLAG_CLIENT_ERROR) {
				Toast.makeText(
						getApplicationContext(),
						R.string.toast_client_error,
						Toast.LENGTH_SHORT
				).show();
				return;
			}
			String name = et_name.getText().toString();
			if (name == null || TextUtils.isEmpty(name)) {
				name = et_number.getText().toString();
			}
			receiveData(result, name);
		}

	}

}
