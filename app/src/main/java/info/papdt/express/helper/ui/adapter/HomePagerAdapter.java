package info.papdt.express.helper.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;

import info.papdt.express.helper.R;
import info.papdt.express.helper.ui.fragment.BaseHomeFragment;
import info.papdt.express.helper.ui.fragment.HomeFragment;
import info.papdt.express.helper.ui.fragment.ReceivedListFragment;
import info.papdt.express.helper.ui.fragment.UnreceivedListFragment;

public class HomePagerAdapter extends CacheFragmentStatePagerAdapter {

	private static String[] TITLES;

	private int mScrollY;

	public HomePagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		TITLES = context.getResources().getStringArray(R.array.title_sections);
	}

	public void setScrollY(int scrollY) {
		mScrollY = scrollY;
	}

	@Override
	protected android.support.v4.app.Fragment createItem(int position) {
		Fragment f = null;
		switch (position) {
			case 0:
				f = HomeFragment.newInstance();
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt(BaseHomeFragment.ARG_INITIAL_POSITION, 1);
					f.setArguments(args);
				}
				break;
			case 1:
				f = UnreceivedListFragment.newInstance();
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt(BaseHomeFragment.ARG_INITIAL_POSITION, 1);
					f.setArguments(args);
				}
				break;
			case 2:
				f = ReceivedListFragment.newInstance();
				if (0 < mScrollY) {
					Bundle args = new Bundle();
					args.putInt(BaseHomeFragment.ARG_INITIAL_POSITION, 1);
					f.setArguments(args);
				}
				break;
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
