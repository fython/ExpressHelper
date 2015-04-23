package info.papdt.express.helper.port.support;

import org.json.JSONException;
import org.json.JSONObject;

public class Express {

	public String companyCode, mailNumber;
	private String jsonData = null;
	public int status = STATUS_UNKNOWN_ERROR;

	/** 查询状态：
	 *  -1 未知错误
	 *  0 查询成功
	 *  1 服务器错误
	 *  2 快递公司找不到或不被支持
	 *  3 传入参数错误
	 * */
	public static final int STATUS_UNKNOWN_ERROR = -1, STATUS_NORMAL = 0, STATUS_SERVER_ERROR = 1,
							STATUS_COMPANY_UNSUPPORTED = 2, STATUS_INPUT_ERROR = 3;

	public Express(String companyCode, String mailNumber){
		this(companyCode, mailNumber, mailNumber);
	}

	public Express(String companyCode, String mailNumber, String name){
		this.companyCode = companyCode;
		this.mailNumber = mailNumber;
	}
	
	public String getDataStr() {
		return jsonData;
	}
	
	public void setData(String jsonStr) {
		this.jsonData = jsonStr;
	}

	public ExpressResult getData() {
		return ExpressResult.buildFromJSON(jsonData);
	}

	public JSONObject toJSONObject() {
		JSONObject obj0 = new JSONObject();
		try {
			obj0.put("companyCode", companyCode);
			obj0.put("mailNumber", mailNumber);
			obj0.put("cache", getDataStr());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj0;
	}

}
