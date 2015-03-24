package info.papdt.express.helper.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import info.papdt.express.helper.ui.fragment.HomeFragment;
import info.papdt.express.helper.ui.fragment.ReceivedListFragment;
import info.papdt.express.helper.ui.fragment.UnreceivedListFragment;

public class HomePagerAdapter extends CacheFragmentStatePagerAdapter {

	private static final String[] TITLES = new String[] {"All", "Unreceived", "Received"};

	private int mScrollY;

	private Fragment mHomeFragment, mUnreceivedFragment, mReceivedFragment;

	public HomePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setScrollY(int scrollY) {
		mScrollY = scrollY;
	}

	@Override
	protected android.support.v4.app.Fragment createItem(int position) {
		Fragment f = null;
		switch (position) {
			case 0:
				if (mHomeFragment == null) {
					mHomeFragment = HomeFragment.newInstance();
				}
				f = mHomeFragment;
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt("initial_position", 1);
					f.setArguments(args);
				}
			case 1:
				if (mUnreceivedFragment == null) {
					mUnreceivedFragment = UnreceivedListFragment.newInstance();
				}
				f = mUnreceivedFragment;
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt("initial_position", 1);
					f.setArguments(args);
				}
			case 2:
				if (mReceivedFragment == null) {
					mReceivedFragment = ReceivedListFragment.newInstance();
				}
				f = mReceivedFragment;
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt("initial_position", 1);
					f.setArguments(args);
				}
		}
		return f;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

}
