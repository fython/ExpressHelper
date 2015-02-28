package info.papdt.express.helper.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import info.papdt.express.helper.R;
import info.papdt.express.helper.ui.adapter.DrawerListItemAdapter;

public class NavigationDrawerFragment extends Fragment {

	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	private NavigationDrawerCallbacks mCallbacks;

	private ListView mDrawerListView;
	private View statusHeaderView;
	private DrawerListItemAdapter mAdapter;

	private int mCurrentSelectedPosition = 0;
	private int statusHeaderHeight = 0;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
		}

		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_drawer,container, false);

		statusHeaderView = v.findViewById(R.id.statusHeaderView);
		statusHeaderView.getLayoutParams().height = statusHeaderHeight;

		mDrawerListView = (ListView) v.findViewById(R.id.drawer_list);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});

		ArrayList<DrawerListItemAdapter.Item> data = new ArrayList<>();

		data.add(new DrawerListItemAdapter.Item(
						getString(R.string.title_section_1),
						R.drawable.ic_home_white_24dp)
		);
		data.add(new DrawerListItemAdapter.Item(
						getString(R.string.title_section_2),
						R.drawable.ic_assignment_returned_white_24dp)
		);
		data.add(new DrawerListItemAdapter.Item(
						getString(R.string.title_section_3),
						R.drawable.ic_assignment_turned_in_white_24dp)
		);
		data.add(new DrawerListItemAdapter.Item(
						getString(R.string.title_settings),
						R.drawable.ic_settings_white_24dp)
		);

		mAdapter = new DrawerListItemAdapter(getActivity().getApplicationContext(),
				data, R.color.drawer_list_item_normal, R.color.drawer_list_item_highlight);
		mDrawerListView.setAdapter(mAdapter);
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return v;
	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mCallbacks != null) {
			if (mCallbacks.onNavigationDrawerItemSelected(position)) {
				try {
					mAdapter.setSelectedPosition(position);
				} catch (Exception e) {

				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public void setHeaderHeight(int height) {
		statusHeaderHeight = height;
		if (statusHeaderView != null) {
			statusHeaderView.getLayoutParams().height = height;
		}
	}

	public static interface NavigationDrawerCallbacks {
		boolean onNavigationDrawerItemSelected(int position);
	}

}
