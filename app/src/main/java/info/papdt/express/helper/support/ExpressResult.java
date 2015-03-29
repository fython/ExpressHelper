package info.papdt.express.helper.support;

import java.util.*;

import info.papdt.express.helper.api.KuaiDi100Helper;

public class ExpressResult {
	
	public int status, errCode, update, cache;
	public String message, html, mailNo, expSpellName, expTextName, ord;
	public ArrayList<Map<String, String>> data;

	public static final int STATUS_FAILED = 0, STATUS_NORMAL = 1, STATUS_ON_THE_WAY = 2,
		STATUS_DELIVERED = 3, STATUS_RETURNED = 4, STATUS_OTHER = 5;

	public ExpressResult() {
			data = new ArrayList<>();
		}

	public static ExpressResult buildFromJSON(String jsonStr) {
		return KuaiDi100Helper.buildDataFromResultStr(jsonStr);
	}

	/** 为了解决奇葩的API把顺丰快递准备签收的状态当成已签收而设 F**k */
	public int getStatus() {
		if (expSpellName.equals("shunfeng")) {
			if (data.get(data.size() - 1).get("context").contains("准备")) {
				return STATUS_ON_THE_WAY;
			} else {
				return status;
			}
		} else {
			return status;
		}
	}
		
}
