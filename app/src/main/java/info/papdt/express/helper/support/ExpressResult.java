package info.papdt.express.helper.support;

import java.util.*;

public class ExpressResult {
	
		public int status, errCode, update, cache;
		public String message, html, mailNo, expSpellName, expTextName, ord;
		public ArrayList<Map<String, String>> data;

		public ExpressResult() {
			data = new ArrayList<Map<String, String>>();
		}
		
		public static ExpressResult buildFromJSON(String jsonStr) {
			ExpressResult result = new ExpressResult();
			
			return result;
		}
		
}
