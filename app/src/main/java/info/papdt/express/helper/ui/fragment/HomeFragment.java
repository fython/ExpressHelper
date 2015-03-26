package info.papdt.express.helper.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class HomeFragment extends BaseHomeFragment {

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

		if (isFirstCreate && mSets.getBoolean(Settings.KEY_AUTO_REFRESH_FIRST, true)) {
			isFirstCreate = false;
			mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
		}

		return rootView;
	}

	@Override
	public void setUpAdapter() {
		mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB);
		mListView.setAdapter(mAdapter);
	}

}