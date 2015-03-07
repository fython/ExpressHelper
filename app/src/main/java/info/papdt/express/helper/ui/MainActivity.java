package info.papdt.express.helper.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.ui.fragment.HomeFragment;
import info.papdt.express.helper.ui.fragment.NavigationDrawerFragment;
import info.papdt.express.helper.ui.fragment.ReceivedListFragment;
import info.papdt.express.helper.ui.fragment.UnreceivedListFragment;

public class MainActivity extends AbsActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	public ExpressDatabase mExpressDB;

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private FloatingActionButton mFAB;

	private ActionBarHelper mActionBar;

	private HomeFragment fragmentHome;
	private ReceivedListFragment fragmentOK;
	private UnreceivedListFragment fragmentUR;
	private int mCurrent;

	public static final int REQUEST_ADD = 100, RESULT_ADD_FINISH = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** Init Navigation Drawer */
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setHeaderHeight(statusBarHeight);
		mActionBar = new ActionBarHelper();
		mActionBar.init();

		/** Init Database */
		mExpressDB = new ExpressDatabase(getApplicationContext());
		refreshDatabase(false);

		setUpDrawer();
		mCurrent = 0;
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

	private void setUpDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerLayout.setDrawerListener(new MyDrawerListener());
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case REQUEST_ADD:
				if (resultCode == RESULT_ADD_FINISH) {
					String jsonStr = intent.getStringExtra("result");
					mExpressDB.addExpress(jsonStr);
					try {
						mExpressDB.save();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					fragmentHome.mDB = mExpressDB;
					fragmentHome.setUpAdapter();
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
	public boolean onNavigationDrawerItemSelected(int position) {
        if (position != 3) {
            mCurrent = position;
        }
		try {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		} catch (NullPointerException e) {

		}

		FragmentManager fragmentManager = getFragmentManager();
		switch (position) {
			case 0:
				if (fragmentHome == null) {
					fragmentHome = HomeFragment.newInstance();
				}
				fragmentManager
						.beginTransaction()
						.replace(R.id.container, fragmentHome)
						.commit();
                setTitle(R.string.app_name);
				return true;
			case 1:
				if (fragmentUR == null) {
					fragmentUR = UnreceivedListFragment.newInstance();
				}
				fragmentManager
						.beginTransaction()
						.replace(R.id.container, fragmentUR)
						.commit();
                setTitle(R.string.title_section_2);
				return true;
			case 2:
				if (fragmentOK == null) {
					fragmentOK = ReceivedListFragment.newInstance();
				}
				fragmentManager
						.beginTransaction()
						.replace(R.id.container, fragmentOK)
						.commit();
                setTitle(R.string.title_section_3);
				return true;
			case 3:
				SettingsActivity.launchActivity(this, SettingsActivity.FLAG_MAIN);
				return false;
			default:
				fragmentManager
						.beginTransaction()
						.replace(R.id.container, PlaceholderFragment.newInstance())
						.commit();
                setTitle(R.string.app_name);
				return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		if (!isDrawerOpen()) {
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
		else if (mCurrent != 0) {
            mNavigationDrawerFragment.selectItem(0);
		} else {
			super.onBackPressed();
		}
	}

	public static class PlaceholderFragment extends Fragment {

		public static PlaceholderFragment newInstance() {
			PlaceholderFragment fragment = new PlaceholderFragment();
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
			return rootView;
		}

	}

	private class MyDrawerListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
			mActionBar.onDrawerOpened();
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
			mActionBar.onDrawerClosed();
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}

	private class ActionBarHelper {
		private final ActionBar mActionBar;
		private CharSequence mDrawerTitle;
		private CharSequence mTitle;

		ActionBarHelper() {
			mActionBar = getSupportActionBar();
		}

		public void init() {
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setDisplayShowHomeEnabled(false);
			mTitle = mDrawerTitle = getTitle();
		}

		public void onDrawerClosed() {
			mActionBar.setTitle(mTitle);
		}

		public void onDrawerOpened() {
			mActionBar.setTitle(mDrawerTitle);
		}

	}

}
