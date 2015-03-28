package info.papdt.express.helper.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;
import info.papdt.express.helper.ui.adapter.HomeCardRecyclerAdapter;

public class HomeFragment extends BaseHomeFragment {

	private HomeCardRecyclerAdapter mAdapter;

	private boolean isFirstCreate = true;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		if (isFirstCreate && mSets.getBoolean(Settings.KEY_AUTO_REFRESH_FIRST, false)) {
			isFirstCreate = false;
			mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
		}

		return rootView;
	}

	@Override
	public void setUpAdapter() {
		mAdapter = new HomeCardRecyclerAdapter(getActivity().getApplicationContext(), mDB, headerView);
		mRecyclerView.setAdapter(mAdapter);
	}

}