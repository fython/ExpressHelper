package info.papdt.express.helper.support;

import java.util.*;

import info.papdt.express.helper.api.KuaiDi100Helper;

public class ExpressResult {
	
		public int status, errCode, update, cache;
		public String message, html, mailNo, expSpellName, expTextName, ord;
		public ArrayList<Map<String, String>> data;

		public ExpressResult() {
			data = new ArrayList<>();
		}
		
		public static ExpressResult buildFromJSON(String jsonStr) {
			return KuaiDi100Helper.buildDataFromResultStr(jsonStr);
		}
		
}
