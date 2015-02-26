package info.papdt.express.helper.api;

import android.util.*;
import info.papdt.express.helper.support.*;
import org.json.*;
import java.util.*;

public class KuaiDi100Helper {
	
	private static String myid = "104262",
	mysecret = "2ac58b166085aefdc9c93a3a69010e87";

	private static final String TAG = "KuaiDi100Helper";

	public static String getRequestUrl(String id, String secret, String com,
									  String nu, String encode) {
		StringBuffer resultUrl = new StringBuffer();
		resultUrl.append("http://api.ickd.cn/?id=" + (id != null ? id : myid));
		resultUrl.append("&secret=" + (secret != null ? secret : mysecret));
		resultUrl.append("&com=" + com);
		resultUrl.append("&nu=" + nu);
		resultUrl.append("&encode=" + (encode != null ? encode : "gbk")
						 + "&ord=asc");
		Log.i(TAG, "Request URL:" + resultUrl);
		return resultUrl.toString();
	}
	
	public static ExpressResult buildDataFromResultStr(String jsonStr) {
		ExpressResult result = new ExpressResult();
		try {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			JSONArray array = person.getJSONArray("data");

			String json2 = null;
			Map<String, String> map;

			result.status = person.getInt("status");
			result.errCode = person.getInt("errCode");
			result.message = person.getString("message");
			result.html = person.getString("html");
			result.mailNo = person.getString("mailNo");
			result.expSpellName = person.getString("expSpellName");
			result.expTextName = person.getString("expTextName");
			result.update = person.getInt("update");
			result.cache = person.getInt("cache");
			result.ord = person.getString("ord");

			for (int i = 0; i < array.length(); i++) {
				map = new HashMap<String, String>();
				json2 = array.get(i).toString();
				JSONTokener jsonParser2 = new JSONTokener(json2);
				JSONObject person2 = (JSONObject) jsonParser2.nextValue();
				map.put("time", person2.getString("time"));
				map.put("context", person2.getString("context"));
				result.data.add(map);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			result.status = 0;
			result.message = "JSON String token error!";
			return result;
		}
		return result;
	}
	
}
