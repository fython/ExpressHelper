package info.papdt.express.helper.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.quinny898.library.persistentsearch.SearchBox;

import java.util.ArrayList;

import info.papdt.express.helper.R;
import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.ui.adapter.CompanyListRecyclerAdapter;
import info.papdt.express.helper.ui.common.MyRecyclerViewAdapter;

public class CompanySelectActivity extends AbsActivity {

	private SearchBox mSearchBox;
	private ObservableRecyclerView mRecyclerView;
	private CompanyListRecyclerAdapter mCompanyListAdapter;

	public static final int REQUEST_CODE_SELECT = 0x100, RESULT_SELECTED = 0x100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_company);

		mToolbar.setTitle("");

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						openSearchBox();
					}
				});
			}
		}, 100);
	}

	public void openSearchBox() {
		mSearchBox.revealFromMenuItem(R.id.action_select_company, this);
		mSearchBox.setSearchListener(new SearchBox.SearchListener() {
			@Override
			public void onSearchOpened() {
				new SearchCompanyTask().execute();
			}

			@Override
			public void onSearchCleared() {
			}

			@Override
			public void onSearchClosed() {
				close();
			}

			@Override
			public void onSearchTermChanged() {
				new SearchCompanyTask().execute(mSearchBox.getSearchText());
			}

			@Override
			public void onSearch(String result) {
			}

		});
	}

	public void close() {
		mSearchBox.hideCircularly(this);
		hideSoftKeyboard();
		scrollToFinishActivity();
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchBox.getEditTextWindowToken(), 0);
	}

	@Override
	protected void setUpViews() {
		mRecyclerView = (ObservableRecyclerView) findViewById(R.id.company_list);
		mSearchBox = (SearchBox) findViewById(R.id.searchBox);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setHasFixedSize(true);
		mSearchBox.setHintText(getString(R.string.search_hint_company));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_company_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public static void launchActivity(Activity mActivity) {
		Intent intent = new Intent(mActivity, CompanySelectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT);
	}

	public class SearchCompanyTask extends AsyncTask<String, Void, ArrayList<KuaiDi100Helper.CompanyInfo.Company>> {

		@Override
		protected ArrayList<KuaiDi100Helper.CompanyInfo.Company> doInBackground(String... params) {
			ArrayList<KuaiDi100Helper.CompanyInfo.Company> src = new ArrayList<>(KuaiDi100Helper.CompanyInfo.info);
			if (params.length > 0) {
				if (params[0] != null && params[0].trim().length() > 0) {
					for (int i = 0; i < src.size(); i++) {
						if (!src.get(i).name.contains(params[0])) {
							src.remove(i);
							i--;
						}
					}
				}
			}
			return src;
		}

		@Override
		protected void onPostExecute(ArrayList<KuaiDi100Helper.CompanyInfo.Company> result) {
			if (result != null) {
				mCompanyListAdapter = new CompanyListRecyclerAdapter(result);
				mRecyclerView.setAdapter(mCompanyListAdapter);
				mCompanyListAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
					@Override
					public void onItemClicked(int position) {
						Intent intent = new Intent();
						intent.putExtra("company_code", mCompanyListAdapter.getItem(position).code);
						setResult(RESULT_SELECTED, intent);
						close();
					}
				});
			}
		}

	}

	public void mic(View v) {
		mSearchBox.micClick(this);
	}

}
