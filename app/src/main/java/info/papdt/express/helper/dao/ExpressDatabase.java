package info.papdt.express.helper.dao;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import info.papdt.express.helper.api.KuaiDi100Helper;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;
import info.papdt.express.helper.support.HttpUtils;
import info.papdt.express.helper.support.Utility;

public class ExpressDatabase {

	private ArrayList<Express> mExpressArray, array_ur, array_ok;
	private Context context;

	private final static String TAG = "ExpressDatabase";

	public ExpressDatabase(Context context) {
		this.context = context;
		mExpressArray = new ArrayList<>();
	}

	public void addExpress(String jsonStr) {
		ExpressResult res = ExpressResult.buildFromJSON(jsonStr);
		Express exp = new Express(res.expSpellName, res.mailNo);
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
			ExpressResult cache = ExpressResult.buildFromJSON(exp.getData());
			if (cache.status == 3) {
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
				newExpress = new Express(jsonArray.getJSONObject(i).getString("companyCode"),
						jsonArray.getJSONObject(i).getString("mailNumber"));
				try {
					newExpress.setData(jsonArray.getJSONObject(i).getString("cache"));
				} catch (Exception e) {
					newExpress.setData(null);
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

	public void pullNewDataFromNetwork() {
		for (Express nowExp : mExpressArray) {
			String result = getDataFromNetwork(nowExp.getCompanyCode(), nowExp.getMailNumber());
			if (result != null) {
				nowExp.setData(result);
			}
		}
	}

	public String getDataFromNetwork(String companyCode, String mailNumber) {
		String[] result = new String[1];

		int resultCode = HttpUtils.get(KuaiDi100Helper.getRequestUrl(null, null, companyCode, mailNumber, "utf8"), result);
		switch (resultCode) {
			case HttpUtils.CODE_OKAY:
				return result[0];
			default:
				return null;
		}
	}

}
