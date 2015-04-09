package info.papdt.express.helper.dao;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;
import info.papdt.express.helper.support.HttpUtils;
import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.support.Utility;

public class ExpressDatabase {

	private ArrayList<Express> mExpressArray, array_ur, array_ok;
	private Context context;
	private Settings mSets;

	private final static String TAG = "ExpressDatabase";

	public ExpressDatabase(Context context) {
		this.context = context;
		this.mSets = Settings.getInstance(context);
		mExpressArray = new ArrayList<>();
	}

	public static ExpressDatabase getInstance(Context context) {
		ExpressDatabase db = new ExpressDatabase(context);
		db.init();
		return db;
	}

	public void addExpress(String jsonStr, String name) {
		ExpressResult res = ExpressResult.buildFromJSON(jsonStr);
		Express exp;
		if (name != null) {
			exp = new Express(res.expSpellName, res.mailNo, name);
		} else {
			exp = new Express(res.expSpellName, res.mailNo);
		}
		exp.setData(jsonStr);
		this.addExpress(exp);
	}

	public void addExpress(Express express) {
		mExpressArray.add(express);
	}

	public Express getExpress(int index){
		return mExpressArray.get(index);
	}

	public void deleteExpress(int position){
		mExpressArray.remove(position);
	}

	public int size() {
		return mExpressArray.size();
	}

	public ArrayList<Express> getUnreceivedArray() {
		return array_ur;
	}

	public ArrayList<Express> getReceivedArray() {
		return array_ok;
	}

	public int urSize() {
		return array_ur.size();
	}

	public int okSize() {
		return array_ok.size();
	}

	public void calcExpress() {
		array_ok = new ArrayList<>();
		array_ur = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			Express exp = getExpress(i);
			ExpressResult cache = exp.getData();
			if (cache.getTrueStatus() == 3) {
				array_ok.add(exp);
			} else {
				array_ur.add(exp);
			}
		}
	}

	public int findExpress(String companyCode, String mailNumber){
		boolean ok = false;
		int i;

		for (i = 0; i < mExpressArray.size(); i++){
			if (mExpressArray.get(i).getCompanyCode().equals(companyCode)
					&& mExpressArray.get(i).getMailNumber().equals(mailNumber)){
				ok = true;
				break;
			}
		}

		if (ok) return i; else return -1;
	}

	public void init(){
		mExpressArray = new ArrayList<>();
		String jsonData;
		try {
			jsonData = Utility.readFile(context, "data.json");
		} catch (IOException e) {
			jsonData = "{\"data\":[]}";
			Log.i(TAG, "文件不存在,初始化新的文件.");
			e.printStackTrace();
		}
		Log.i(TAG, "读入json数据结果:");
		Log.i(TAG, jsonData);
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;

		try {
			jsonObj = new JSONObject(jsonData);
		} catch (JSONException e) {
			Log.e(TAG, "无法解析json");
			e.printStackTrace();
			return ;
		}

		try {
			jsonArray = jsonObj.getJSONArray("data");
		} catch (JSONException e) {
			Log.e(TAG, "数据格式丢失, 缺少 data 数组");
			e.printStackTrace();
			return ;
		}

		Express newExpress;
		for (int i = 0; i < jsonArray.length(); i++){
			try {
				JSONObject obj = jsonArray.getJSONObject(i);
				newExpress = new Express(obj.getString("companyCode"), obj.getString("mailNumber"));
				String name;
				try {
					name = obj.getString("name");
				} catch (Exception e) {
					name = newExpress.getMailNumber();
				}
				newExpress.setName(name);
				try {
					newExpress.setData(obj.getString("cache"));
				} catch (Exception e) {
					newExpress.setData(null);
				}

				try {
					newExpress.setLastData(obj.getString("lastCache"));
				} catch (Exception e) {
					newExpress.setLastData(null);
				}

				try {
					newExpress.shouldPush = obj.getBoolean("shouldPush");
					newExpress.needPush = obj.getBoolean("needPush");
				} catch (Exception e) {

				}

				mExpressArray.add(newExpress);
			} catch (JSONException e){
				Log.e(TAG, "第"+i+"组数据格式出现错误");
				e.printStackTrace();
			}
		}
		calcExpress();
	}

	public void save() throws IOException, JSONException{
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		for (Express nowExp : mExpressArray) {
			array.put(nowExp.toJSONObject());
		}
		obj.put("data", array);
		Utility.saveFile(context, "data.json", obj.toString());
	}

	public void pullNewDataFromNetwork(boolean refreshDelivered) {
		for (Express nowExp : mExpressArray) {
			if (!refreshDelivered && nowExp.getData().getTrueStatus() == ExpressResult.STATUS_DELIVERED) {
				continue;
			}
			String result = getDataFromNetwork(nowExp.getCompanyCode(), nowExp.getMailNumber());
			if (result != null) {
				nowExp.setData(result);
			}
		}
	}

	public String getDataFromNetwork(String companyCode, String mailNumber) {
		String[] result = new String[1];

		String secret, app_id;

		int choice = mSets.getInt(Settings.KEY_TOKEN_CHOOSE, 0);
		if (choice == 4) {
			Random r = new Random();
			choice = (choice = r.nextInt(2)) == 2 ? choice : 3;
		}
		switch (choice) {
			case 1:
				secret = KuaiDi100Helper.mysecret;
				app_id = KuaiDi100Helper.myid;
				break;
			case 2:
				secret = mSets.getString(Settings.KEY_CUSTOM_SECRET, "error");
				app_id = mSets.getString(Settings.KEY_CUSTOM_ID, "error");
				break;
			case 3:
				secret = KuaiDi100Helper.smsecret;
				app_id = KuaiDi100Helper.smid;
				break;
			case 0:
			default:
				secret = KuaiDi100Helper.xfsecret;
				app_id = KuaiDi100Helper.xfid;
				break;
		}

		int resultCode = HttpUtils.get(KuaiDi100Helper.getRequestUrl(app_id, secret, companyCode, mailNumber, "utf8"), result);
		switch (resultCode) {
			case HttpUtils.CODE_OKAY:
				return result[0];
			default:
				return null;
		}
	}

}
