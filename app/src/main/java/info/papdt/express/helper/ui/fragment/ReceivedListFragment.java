package info.papdt.express.helper.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class ReceivedListFragment extends BaseHomeFragment {

	private boolean isFirstCreate = true;

	public static ReceivedListFragment newInstance() {
		ReceivedListFragment fragment = new ReceivedListFragment();
		return fragment;
	}

	public ReceivedListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		if (isFirstCreate) {
			isFirstCreate = false;
			mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
		} else {
			setUpAdapter();
		}

		return rootView;
	}

	@Override
	public void setUpAdapter() {
		mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB, HomeCardAdapter.TYPE_RECEIVED);
		mListView.setAdapter(mAdapter);
	}

}