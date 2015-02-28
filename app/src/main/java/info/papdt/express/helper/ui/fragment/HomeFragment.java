package info.papdt.express.helper.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.ui.DetailsActivity;
import info.papdt.express.helper.ui.MainActivity;
import info.papdt.express.helper.ui.adapter.HomeCardAdapter;

public class HomeFragment extends Fragment {

	private ExpressDatabase mDB;

	private SwipeRefreshLayout refreshLayout;
	private ListView mListView;
	private HomeCardAdapter mAdapter;

	private boolean isFirstCreate = true;

	public static final int FLAG_REFRESH_LIST = 0;

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

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
		mListView = (ListView) rootView.findViewById(R.id.listView);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				intent.putExtra("data", mDB.getExpress(position).toJSONObject().toString());
				startActivity(intent);
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int realPosition = mDB.findExpress(
						mAdapter.getItem(position).getCompanyCode(),
						mAdapter.getItem(position).getMailNumber()
				);
				mDB.deleteExpress(realPosition);
				try {
					mDB.save();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mAdapter.notifyDataSetChanged();
				return true;
			}
		});

		refreshLayout.setColorSchemeResources(R.color.blue_500);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
			}
		});
		if (isFirstCreate) {
			isFirstCreate = false;
			mHandler.sendEmptyMessage(FLAG_REFRESH_LIST);
		} else {
			setUpAdapter();
		}

		return rootView;
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
			}
		}

	};

	private void setUpAdapter() {
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
			}
			if (mDB != null) {
				setUpAdapter();
			}
		}

	}

}