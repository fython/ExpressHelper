package info.papdt.express.helper.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;

public class DetailsActivity extends AbsActivity {

	private ListView mListView;
	private EditText mEditTextName;
	private TextView tv_company, tv_mail_no, tv_status, tv_round_center;
	private CircleImageView iv_round;

	private int eid;
	private Express express;
	private ExpressResult cache;
	private ExpressDatabase edb;

	private ArrayList<Map<String, String>> list;

	private boolean isEditingTitle = false;

	private static final int MENU_ITEM_DONE = 0x00, MENU_ITEM_EDIT = 0x01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		Intent intent = getIntent();
		eid = intent.getIntExtra("id", 0);
		try {
			JSONObject obj = new JSONObject(intent.getStringExtra("data"));
			express = new Express(obj.getString("companyCode"),
					obj.getString("mailNumber"),
					obj.getString("name"));
			express.setData(obj.getString("cache"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		cache = ExpressResult.buildFromJSON(express.getData());

		edb = new ExpressDatabase(getApplicationContext());

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(express.getName());

		setUpHeaderView();
		setUpListView();
	}

	@Override
	protected void setUpViews() {
		mListView = (ListView) findViewById(R.id.listView);
		tv_company = (TextView) findViewById(R.id.tv_express_company);
		tv_mail_no = (TextView) findViewById(R.id.tv_mail_no);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_round_center = (TextView) findViewById(R.id.center_text);
		iv_round = (CircleImageView) findViewById(R.id.iv_round);

		mEditTextName = new EditText(mActionBar.getThemedContext());
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
		mActionBar.setCustomView(mEditTextName, lp);
		mActionBar.setDisplayShowCustomEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(true);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem item;
		if (isEditingTitle) {
			item = menu.add(0, MENU_ITEM_DONE, 0, R.string.action_done)
					.setIcon(R.drawable.ic_done_white_24dp);
			MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		} else {
			item = menu.add(0, MENU_ITEM_EDIT, 0, R.string.action_edit)
					.setIcon(R.drawable.ic_mode_edit_white_24dp);
			MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == MENU_ITEM_DONE) {
			isEditingTitle = false;
			new Thread() {
				@Override
				public void run() {
					edb.init();
					edb.getExpress(eid).setName(mEditTextName.getText().toString().trim());
					try {
						edb.save();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mActionBar.setTitle(edb.getExpress(eid).getName());
							setResult(MainActivity.RESULT_HAS_CHANGED);
						}
					});
				}
			}.start();
			hideSoftKeyboard();
			syncActionBarCustomView();
			invalidateOptionsMenu();
			return true;
		} else if (id == MENU_ITEM_EDIT) {
			isEditingTitle = true;
			mEditTextName.setText(mActionBar.getTitle());
			syncActionBarCustomView();
			invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (isEditingTitle) {
			isEditingTitle = false;
			hideSoftKeyboard();
			syncActionBarCustomView();
			invalidateOptionsMenu();
		} else {
			finish();
		}
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextName.getWindowToken(), 0);
	}

	private void syncActionBarCustomView() {
		mActionBar.setDisplayShowCustomEnabled(isEditingTitle);
		mActionBar.setDisplayShowTitleEnabled(!isEditingTitle);
	}

	private void setUpHeaderView() {
		tv_status.setText(getResources().getStringArray(R.array.status)[cache.status]);
		tv_company.setText(cache.expTextName);
		tv_round_center.setText(cache.expTextName.substring(0, 1));
		tv_mail_no.setText(cache.mailNo);

		ColorDrawable drawable = new ColorDrawable(getResources().getIntArray(R.array.statusColor) [cache.status]);
		iv_round.setImageDrawable(drawable);
	}

	private void setUpListView() {
		list = new ArrayList<>();

		if (cache.errCode != 0){
			list.add(produce(getString(R.string.item_errorcode), getResources().getStringArray(R.array.errCode)[cache.errCode]));
			list.add(produce(getString(R.string.item_errormessage), cache.message));
		}

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
