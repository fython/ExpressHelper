package info.papdt.express.helper.ui.fragment;

import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class UnreceivedListFragment extends BaseHomeFragment {

	private HomeCardAdapter mAdapter;

	public static UnreceivedListFragment newInstance() {
		UnreceivedListFragment fragment = new UnreceivedListFragment();
		return fragment;
	}

	public UnreceivedListFragment() {
	}

	@Override
	public void setUpAdapter() {
		mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB, HomeCardAdapter.TYPE_UNRECEIVED);
		mListView.setAdapter(mAdapter);
	}

}