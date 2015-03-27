package info.papdt.express.helper.ui.fragment;

import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class ReceivedListFragment extends BaseHomeFragment {

	private HomeCardAdapter mAdapter;

	public static ReceivedListFragment newInstance() {
		ReceivedListFragment fragment = new ReceivedListFragment();
		return fragment;
	}

	public ReceivedListFragment() {
	}

	@Override
	public void setUpAdapter() {
		mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB, HomeCardAdapter.TYPE_RECEIVED);
		mListView.setAdapter(mAdapter);
	}

}