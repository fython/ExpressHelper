package info.papdt.express.helper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.quinny898.library.persistentsearch.SearchBox;

import info.papdt.express.helper.R;

public class CompanySelectActivity extends AbsActivity {

	private RecyclerView mRecyclerView;

	private SearchBox searchBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_company);

		mToolbar.setTitle("");

		openSearchBox();
	}

	private void openSearchBox() {

	}

	@Override
	protected void setUpViews() {
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_company_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public static void launchActivity(Activity mActivity) {
		Intent intent = new Intent(mActivity, CompanySelectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		mActivity.startActivity(intent);
	}

}
