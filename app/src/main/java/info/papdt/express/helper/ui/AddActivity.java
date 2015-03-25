package info.papdt.express.helper.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Locale;
import java.util.Random;

import info.papdt.express.helper.R;
import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.support.HttpUtils;
import info.papdt.express.helper.support.Settings;

public class AddActivity extends AbsActivity implements OnItemSelectedListener{

	private boolean isChecking = false;

	private MaterialEditText mEditTextSerial, mEditTextName;
	private Spinner mSpinnerCompany;
	private ProgressBar mProgress;
	private View mButtonDone;
	private ArrayAdapter<String> mCompanyAdapter;
	private int mNow = 0;
	
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
		mEditTextSerial = (MaterialEditText) findViewById(R.id.et_number);
		mSpinnerCompany = (Spinner) findViewById(R.id.spinner_company);
		mEditTextName = (MaterialEditText) findViewById(R.id.et_name);
		mButtonDone = findViewById(R.id.btn_done);
		mProgress = (ProgressBar) findViewById(R.id.progressBar);
		mCompanyAdapter = new ArrayAdapter<String>( 
			this,
			android.R.layout.simple_spinner_item,
			KuaiDi100Helper.CompanyInfo.names);
		mCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCompany.setAdapter(mCompanyAdapter);
		mSpinnerCompany.setOnItemSelectedListener(this);
		mSpinnerCompany.setSelection(0,true);
		mSpinnerCompany.setPrompt(getString(R.string.hint_et_company));
		mButtonDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecking) return;
				postData();
			}
		});
	}

	private void postData() {
		
		
		if (TextUtils.isEmpty(mEditTextSerial.getText())) {
			Toast.makeText(
					getApplicationContext(),
					R.string.toast_number_empty,
					Toast.LENGTH_SHORT
			).show();
			return;
		}
		new PostApiTask().execute(KuaiDi100Helper.CompanyInfo.info.get(KuaiDi100Helper.CompanyInfo.names[mNow]), mEditTextSerial.getText().toString());
	}

	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        mNow = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        
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
				FLAG_CLIENT_ERROR = "client_error", FLAG_HAS_BEEN_EXIST = "has_been_exist";

		@Override
		protected void onPreExecute() {
			isChecking = true;
			mProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... src) {
			String[] result = new String[1];
			String companyCode = src[0];
			String mailNumber = src[1];

			ExpressDatabase db = new ExpressDatabase(getApplicationContext());
			db.init();
			if (db.findExpress(companyCode, mailNumber) != -1) {
				return FLAG_HAS_BEEN_EXIST;
			}

			String secret, app_id;

			int choice = mSets.getInt(Settings.KEY_TOKEN_CHOOSE, 0);
			if (choice == 4) {
				Random r = new Random();
				choice = (choice = r.nextInt(2)) == 2 ? choice : 3;
			}
			switch (choice) {
				case 1:
					secret = KuaiDi100Helper.mysecret;
					app_id = KuaiDi100Helper.myid;
					break;
				case 2:
					secret = mSets.getString(Settings.KEY_CUSTOM_SECRET, "error");
					app_id = mSets.getString(Settings.KEY_CUSTOM_ID, "error");
					break;
				case 3:
					secret = KuaiDi100Helper.smsecret;
					app_id = KuaiDi100Helper.smid;
					break;
				case 0:
				default:
					secret = KuaiDi100Helper.xfsecret;
					app_id = KuaiDi100Helper.xfid;
					break;
			}

			int resultCode = HttpUtils.get(KuaiDi100Helper.getRequestUrl(app_id, secret, companyCode, mailNumber, "utf8"), result);
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
			if (result == FLAG_HAS_BEEN_EXIST) {
				Toast.makeText(
						getApplicationContext(),
						R.string.toast_has_been_exist,
						Toast.LENGTH_SHORT
				).show();
				return;
			}
			String name = mEditTextName.getText().toString();
			if (name == null || TextUtils.isEmpty(name)) {
				name = mEditTextSerial.getText().toString();
			}
			receiveData(result, name);
		}

	}

}
