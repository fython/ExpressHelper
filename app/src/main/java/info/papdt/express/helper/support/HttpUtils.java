package info.papdt.express.helper.support;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {

	public static final String TAG = "HttpUtils";
	public static final int CODE_OKAY = 0, CODE_NETWORK_ERROR = -1, CODE_CLIENT_ERROR = -2,
			CODE_NONE_200 = 1;

	public static int get(String url, String[] result) {
		HttpResponse httpResponse;
		try {
			Log.v(TAG, "HTTP请求:" + url);
			HttpGet httpGet = new HttpGet(url);
			httpResponse = new DefaultHttpClient().execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result[0] = EntityUtils.toString(httpResponse.getEntity());
				Log.v(TAG, "返回结果为" + result[0]);
				return CODE_OKAY;
			} else {
				return CODE_NONE_200;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return CODE_CLIENT_ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			return CODE_NETWORK_ERROR;
		}
	}

}
