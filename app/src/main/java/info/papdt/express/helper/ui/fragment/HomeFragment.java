package info.papdt.express.helper.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import org.json.JSONException;

import java.io.IOException;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.ui.DetailsActivity;
import info.papdt.express.helper.ui.MainActivity;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class HomeFragment extends Fragment {

	public ExpressDatabase mDB;

	private SwipeRefreshLayout refreshLayout;
	private ObservableListView mListView;
	private HomeCardAdapter mAdapter;

	private Settings mSets;

	public static final int FLAG_REFRESH_LIST = 0, FLAG_REFRESH_ADAPTER_ONLY = 1;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		if (mSets == null) mSets = Settings.getInstance(getActivity().getApplicationContext());

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

		mListView = (ObservableListView) rootView.findViewById(R.id.scroll);

		Fragment parentFragment = getParentFragment();
		ViewGroup viewGroup = (ViewGroup) parentFragment.getView();
		if (viewGroup != null) {
			mListView.setTouchInterceptionViewGroup((ViewGroup) viewGroup.findViewById(R.id.container));
			if (parentFragment instanceof ObservableScrollViewCallbacks) {
				mListView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
			}
		}
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				intent.putExtra("id", position);
				intent.putExtra("data", mDB.getExpress(position).toJSONObject().toString());
				getActivity().startActivityForResult(intent, MainActivity.REQUEST_DETAILS);
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int realPosition = mDB.findExpress(
						mAdapter.getItem(position).getCompanyCode(),
						mAdapter.getItem(position).getMailNumber()
				);
				showDeleteDialog(realPosition);
				return true;
			}
		});
		if (mSets.getBoolean(Settings.KEY_USE_CARD_LIST, true)) {
			mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		}

		refreshLayout.setColorSchemeResources(R.color.blue_500);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
			}
		});

		mDB = ((MainActivity) getActivity()).mExpressDB;
		setUpAdapter();

		return rootView;
	}

	private void showDeleteDialog(final int realPosition) {
		new MaterialDialog.Builder(getActivity())
				.title(R.string.dialog_delete_title)
				.content(R.string.dialog_delete_msg)
				.positiveText(android.R.string.ok)
				.negativeText(android.R.string.cancel)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						super.onPositive(dialog);
							mDB.deleteExpress(realPosition);
						try {
							mDB.save();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						mAdapter.notifyDataSetChanged();
					}
				})
				.show();
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FLAG_REFRESH_LIST:
					if (!refreshLayout.isRefreshing()) {
						refreshLayout.setRefreshing(true);
					}
					new RefreshTask().execute();
					break;
				case FLAG_REFRESH_ADAPTER_ONLY:
					mDB.init();
					setUpAdapter();
					break;
			}
		}

	};

	public void setUpAdapter() {
		mAdapter = new HomeCardAdapter(getActivity().getApplicationContext(), mDB);
		mListView.setAdapter(mAdapter);
	}

	private class RefreshTask extends AsyncTask<Void, Void, ExpressDatabase> {

		@Override
		protected ExpressDatabase doInBackground(Void... params) {
			try {
				((MainActivity) getActivity()).refreshDatabase(true);
				return ((MainActivity) getActivity()).mExpressDB;
			} catch (Exception e) {
				// failed
				return null;
			}
		}

		@Override
		protected void onPostExecute(ExpressDatabase db) {
			refreshLayout.setRefreshing(false);
			if (db != null) {
				mDB = db;
			} else {
				Toast.makeText(
						getActivity().getApplicationContext(),
						R.string.toast_network_error,
						Toast.LENGTH_SHORT
				).show();
			}
			if (mDB != null) {
				setUpAdapter();
			}
		}

	}

}