package info.papdt.express.helper.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import info.papdt.express.helper.dao.ExpressDatabase;

public class AddTrackReceiver extends BroadcastReceiver {

	public static final String EXTRA_ADD_EXPRESS_DATA = "data";

	private String tempData;
	private ExpressDatabase edb;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AddTrackReceiver", "onReceived!");
		if (intent.hasExtra(EXTRA_ADD_EXPRESS_DATA)) {
			tempData = intent.getStringExtra(EXTRA_ADD_EXPRESS_DATA);
			Log.i("AddTrackReceiver", tempData);
			edb = ExpressDatabase.getInstance(context);
			new Thread() {
				@Override
				public void run() {
					try {
						edb.addExpress(Express.buildFromJSONObject(new JSONObject(tempData)).getDataStr(), null);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					try {
						edb.save();
						Log.i("AddTrackReceiver", "Succeed!");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

}