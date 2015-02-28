package info.papdt.express.helper.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;

public class DetailsActivity extends AbsActivity {

	private ListView mListView;
	
	private Express express;
	private ExpressResult cache;

	private ArrayList<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		mActionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		int id = intent.getIntExtra("id", 0);
		try {
			JSONObject obj = new JSONObject(intent.getStringExtra("data"));
			express = new Express(obj.getString("companyCode"),
					obj.getString("mailNumber"));
			express.setData(obj.getString("cache"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		cache = ExpressResult.buildFromJSON(express.getData());

		setUpListView();
	}

	@Override
	protected void setUpViews() {
		mListView = (ListView) findViewById(R.id.listView);

	}

	private void setUpListView() {
		list = new ArrayList<>();
		list.add(produce(getString(R.string.item_status), getResources().getStringArray(R.array.status)[cache.status]));
		if (cache.errCode != 0){
			list.add(produce(getString(R.string.item_errorcode), getResources().getStringArray(R.array.errCode)[cache.errCode]));
			list.add(produce(getString(R.string.item_errormessage), cache.message));
		}
		list.add(produce(getString(R.string.item_companyname), cache.expTextName));
		list.add(produce(getString(R.string.item_mailno), cache.mailNo));

		for (int i = cache.data.size() - 1; i >= 0; i--){
			list.add(produce(
					cache.data.get(i).get("time"),
					cache.data.get(i).get("context")
			));
		}

		mListView.setAdapter(new SimpleAdapter(
				getApplicationContext(),
				list,
				R.layout.simple_list_item,
				new String[]{"0", "1"},
				new int[]{android.R.id.text1, android.R.id.text2}
		));
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				setClipboard(list.get(position).get("0") + ": " + list.get(position).get("1"));
				Toast.makeText(
						getApplicationContext(),
						R.string.details_has_copied,
						Toast.LENGTH_SHORT
				).show();
				return true;
			}
		});
	}

	private Map<String, String> produce(String t1, String t2){
		Map<String, String> map = new HashMap<>();
		map.put("0", t1);
		map.put("1", t2);
		return map;
	}

	private void setClipboard(String text) {
		ClipboardManager clipMan = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipMan.setPrimaryClip(ClipData.newPlainText(null, text));
	}

}
