package info.papdt.express.helper.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.api.secret.KuaiDi100;
import info.papdt.express.helper.ui.adapter.CompanyListRecyclerAdapter;
import info.papdt.express.helper.ui.common.MyRecyclerViewAdapter;
import info.papdt.express.helper.view.SlidingTabLayout;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quinny898.library.persistentsearch.SearchBox;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.ui.adapter.HomePagerAdapter;

public class MainActivity extends AbsActivity implements ObservableScrollViewCallbacks {

	public ExpressDatabase mExpressDB;

	private View mHeaderView;
	private int mBaseTranslationY;

	private ViewPager mPager;
	private SlidingTabLayout mSlidingTab;
	private HomePagerAdapter mPagerAdapter;
	private FloatingActionButton mFAB;

	private SearchBox mSearchBox;
	private View mCompanyListPage, mCompanyListPageBackground;
	private ObservableRecyclerView mCompanyList;

	public static final int REQUEST_ADD = 100, RESULT_ADD_FINISH = 100,
			REQUEST_DETAILS = 101, RESULT_HAS_CHANGED = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setSwipeBackEnable(false);

		/** Init Database */
		mExpressDB = new ExpressDatabase(getApplicationContext());
		refreshDatabase(false);

		/** Init ViewPager */
		mPagerAdapter = new HomePagerAdapter(getApplicationContext(),
				getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mSlidingTab.setViewPager(mPager);
	}

	public void refreshDatabase(boolean pullNewData) {
		mExpressDB.init();
		if (pullNewData) {
			mExpressDB.pullNewDataFromNetwork();
			try {
				mExpressDB.save();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void setUpViews() {
		View statusHeaderView1 = findViewById(R.id.statusHeaderView1);
		statusHeaderView1.getLayoutParams().height = statusBarHeight;

		mSearchBox = (SearchBox) findViewById(R.id.searchBox);
		mCompanyListPage = findViewById(R.id.company_list_page);
		mCompanyListPageBackground = findViewById(R.id.company_list_page_background);
		mCompanyList = (ObservableRecyclerView) mCompanyListPage.findViewById(R.id.recycler_view);

		mSearchBox.setLogoText("");
		mSearchBox.setHintText(getString(R.string.search_hint_company));
		mCompanyList.setLayoutManager(new LinearLayoutManager(this));
		mCompanyList.setHasFixedSize(true);

		mHeaderView = findViewById(R.id.header);
		ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
		mPager = (ViewPager) findViewById(R.id.pager);

		/** Set up SlidingTabLayout */
		mSlidingTab = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mSlidingTab.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		mSlidingTab.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));
		mSlidingTab.setDistributeEvenly(true);

		// When the page is selected, other fragments' scrollY should be adjusted
		// according to the toolbar status(shown/hidden)
		mSlidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
			}

			@Override
			public void onPageSelected(int i) {
				propagateToolbarState(toolbarIsShown());
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		/** Set up FloatingActionButton */
		mFAB = (FloatingActionButton) findViewById(R.id.fab);
		mFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivityForResult(intent, REQUEST_ADD);
			}
		});
	}

	public void openCompanyList() {
		mCompanyListPage.setVisibility(View.VISIBLE);
		AlphaAnimation anim = new AlphaAnimation(0f, 1f);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setFillAfter(true);
		anim.setDuration(250);
		mCompanyListPageBackground.startAnimation(anim);

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
				closeCompanyList();
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

	public void closeCompanyList() {
		mSearchBox.hideCircularly(this);

		AlphaAnimation anim = new AlphaAnimation(1f, 0f);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setFillAfter(true);
		anim.setDuration(250);
		mCompanyListPageBackground.startAnimation(anim);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mCompanyListPage.setVisibility(View.GONE);
					}
				});
			}
		}, 250);
	}

	@Override
	public void onBackPressed() {
		if (mSearchBox.isSearchOpened()) {
			closeCompanyList();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		if (dragging) {
			int toolbarHeight = mToolbar.getHeight();
			float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
			if (firstScroll) {
				if (-toolbarHeight < currentHeaderTranslationY) {
					mBaseTranslationY = scrollY;
				}
			}
			float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
		}
	}

	@Override
	public void onDownMotionEvent() {
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		mBaseTranslationY = 0;

		Fragment fragment = getCurrentFragment();
		if (fragment == null) {
			return;
		}
		View view = fragment.getView();
		if (view == null) {
			return;
		}

		// ObservableXxxViews have same API
		// but currently they don't have any common interfaces.
		adjustToolbar(scrollState, view);
	}

	private Fragment getCurrentFragment() {
		return mPagerAdapter.getItemAt(mPager.getCurrentItem());
	}

	private void adjustToolbar(ScrollState scrollState, View view) {
		int toolbarHeight = mToolbar.getHeight();
		final Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
		if (scrollView == null) {
			return;
		}
		int scrollY = scrollView.getCurrentScrollY();
		if (scrollState == ScrollState.DOWN) {
			showToolbar();
		} else if (scrollState == ScrollState.UP) {
			if (toolbarHeight <= scrollY) {
				hideToolbar();
			} else {
				showToolbar();
			}
		} else {
			// Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
			if (toolbarIsShown() || toolbarIsHidden()) {
				// Toolbar is completely moved, so just keep its state
				// and propagate it to other pages
				propagateToolbarState(toolbarIsShown());
			} else {
				// Toolbar is moving but doesn't know which to move:
				// you can change this to hideToolbar()
				showToolbar();
			}
		}
	}

	private void propagateToolbarState(boolean isShown) {
		int toolbarHeight = mToolbar.getHeight();

		// Set scrollY for the fragments that are not created yet
		mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

		// Set scrollY for the active fragments
		for (int i = 0; i < mPagerAdapter.getCount(); i++) {
			// Skip current item
			if (i == mPager.getCurrentItem()) {
				continue;
			}

			// Skip destroyed or not created item
			Fragment f = mPagerAdapter.getItemAt(i);
			if (f == null) {
				continue;
			}

			View view = f.getView();
			if (view == null) {
				continue;
			}
			propagateToolbarState(isShown, view, toolbarHeight);
		}
	}

	private void propagateToolbarState(boolean isShown, View view, int toolbarHeight) {
		Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
		if (scrollView == null) {
			return;
		}
		if (isShown) {
			// Scroll up
			if (0 < scrollView.getCurrentScrollY()) {
				scrollView.scrollVerticallyTo(0);
			}
		} else {
			// Scroll down (to hide padding)
			if (scrollView.getCurrentScrollY() < toolbarHeight) {
				scrollView.scrollVerticallyTo(toolbarHeight);
			}
		}
	}

	private boolean toolbarIsShown() {
		return ViewHelper.getTranslationY(mHeaderView) == 0;
	}

	private boolean toolbarIsHidden() {
		return ViewHelper.getTranslationY(mHeaderView) == - mToolbar.getHeight();
	}

	private void showToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		if (headerTranslationY != 0) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
		}
		propagateToolbarState(true);
	}

	private void hideToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		int toolbarHeight = mToolbar.getHeight();
		if (headerTranslationY != -toolbarHeight) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
		}
		propagateToolbarState(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case REQUEST_ADD:
				if (resultCode == RESULT_ADD_FINISH) {
					String jsonStr = intent.getStringExtra("result");
					String name = intent.getStringExtra("name");
					mExpressDB.addExpress(jsonStr, name);
					try {
						mExpressDB.save();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mPagerAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_DETAILS:
				if (resultCode == RESULT_HAS_CHANGED) {
					mPagerAdapter.notifyDataSetChanged();
				}
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			mExpressDB.save();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			SettingsActivity.launchActivity(this, SettingsActivity.FLAG_MAIN);
			return true;
		}
		if (id == R.id.action_select_company) {
			openCompanyList();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void mic(View v) {
		mSearchBox.micClick(this);
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
				CompanyListRecyclerAdapter adapter = new CompanyListRecyclerAdapter(result);
				mCompanyList.setAdapter(adapter);
				adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
					@Override
					public void onItemClicked(int position) {

					}
				});
			}
		}

	}

}
