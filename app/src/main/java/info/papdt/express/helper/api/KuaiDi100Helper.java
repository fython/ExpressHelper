package info.papdt.express.helper.api;

import android.util.*;

import info.papdt.express.helper.api.secret.KuaiDi100;
import info.papdt.express.helper.support.*;
import org.json.*;
import java.util.*;

public class KuaiDi100Helper {
	
	public static String myid = "104262", mysecret = "2ac58b166085aefdc9c93a3a69010e87";
	public static String xfid = "109066", xfsecret = "b1726be0ec9c6a1abe60e3d71ef72603";
	public static String smid = KuaiDi100.SKINME_API_ID, smsecret = KuaiDi100.SKINME_API_SECRET;
	// public static String smid = "", smsecret = "";

	private static final String TAG = "KuaiDi100Helper";

	public static String getRequestUrl(String id, String secret, String com,
									  String number, String encode) {
		StringBuffer resultUrl = new StringBuffer();
		resultUrl.append("http://api.ickd.cn/?id=" + (id != null ? id : xfid));
		resultUrl.append("&secret=" + (secret != null ? secret : xfsecret));
		resultUrl.append("&com=" + com);
		resultUrl.append("&nu=" + number);
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

			String json2;
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
				map = new HashMap<>();
				json2 = array.get(i).toString();
				JSONTokener jsonParser2 = new JSONTokener(json2);
				JSONObject person2 = (JSONObject) jsonParser2.nextValue();
				map.put("time", person2.getString("time"));
				map.put("context", person2.getString("context"));
				if (!person2.getString("context").contains("官网")) {
					result.data.add(map);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			result.status = 0;
			result.message = "JSON String token error!";
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			result.status = 0;
			result.message = "Unknown error!";
		}
		return result;
	}

	public static class CompanyInfo {

		public static Map<String, String> info;
		public static String[] names;
		static {
			info = new LinkedHashMap<String, String>();
			info.put("顺丰快递","shunfeng");
			info.put("申通快递（可能存在延迟）","shentong");
			info.put("中通快递","zhongtong");
			info.put("圆通快递","yuantong");
			info.put("韵达快递","yunda");
			info.put("天天快递","tiantian");
			info.put("德邦物流","debang");
			info.put("UPS快递","ups");
			info.put("EMS快递","ems");
			info.put("AAE快递","aae");
			info.put("安捷快递","anjie");
			info.put("安能物流","anneng");
			info.put("安迅物流","anxun");
			info.put("奥硕物流","aoshuo");
			info.put("Aramex国际快递","aramex");
			info.put("百千诚国际物流","baiqian");
			info.put("巴伦支","balunzhi");
			info.put("宝通达","baotongda");
			info.put("成都奔腾国际快递","benteng");
			info.put("长通物流","changtong");
			info.put("程光快递","chengguang");
			info.put("城际快递","chengji");
			info.put("城市100","chengshi100");
			info.put("传喜快递","chuanxi");
			info.put("传志快递","chuanzhi");
			info.put("出口易物流","chukouyi");
			info.put("CityLinkExpress","citylink");
			info.put("东方快递","coe");
			info.put("中国远洋运输(COSCON)","coscon");
			info.put("城市之星","cszx");
			info.put("大达物流","dada");
			info.put("大金物流","dajin");
			info.put("大田物流","datian");
			info.put("大洋物流快递","dayang");
			info.put("DHL快递","dhl");
			info.put("店通快递","diantong");
			info.put("递四方速递","disifang");
			info.put("DPEX快递","dpex");
			info.put("D速快递","dsu");
			info.put("百福东方物流","ees");
			info.put("E邮宝","eyoubao");
			info.put("凡宇快递","fanyu");
			info.put("Fardar","fardar");
			info.put("国际Fedex","fedex");
			info.put("Fedex国内","fedexcn");
			info.put("飞豹快递","feibao");
			info.put("原飞航物流","feihang");
			info.put("飞特物流","feite");
			info.put("飞洋快递","feiyang");
			info.put("飞远物流","feiyuan");
			info.put("丰达快递","fengda");
			info.put("港快速递","gangkuai");
			info.put("高铁快递","gaotie");
			info.put("广东邮政物流","gdyz");
			info.put("邮政小包","gnxb");
			info.put("共速达物流|快递","gongsuda");
			info.put("冠达快递","guanda");
			info.put("国通快递","guotong");
			info.put("山东海红快递","haihong");
			info.put("好来运快递","haolaiyun");
			info.put("昊盛物流","haosheng");
			info.put("河北建华快递","hebeijianhua");
			info.put("恒路物流","henglu");
			info.put("恒宇运通","hengyu");
			info.put("香港邮政","hkpost");
			info.put("华诚物流","huacheng");
			info.put("华翰物流","huahan");
			info.put("华航快递","huahang");
			info.put("黄马甲快递","huangmajia");
			info.put("华企快递","huaqi");
			info.put("华宇物流","huayu");
			info.put("汇通快递","huitong");
			info.put("户通物流","hutong");
			info.put("海外环球快递","hwhq");
			info.put("国际邮政快递","intmail");
			info.put("佳惠尔快递","jiahuier");
			info.put("佳吉快运","jiaji");
			info.put("佳怡物流","jiayi");
			info.put("佳宇物流","jiayu");
			info.put("加运美快递","jiayunmei");
			info.put("捷特快递","jiete");
			info.put("金大物流","jinda");
			info.put("京东快递","jingdong");
			info.put("京广快递","jingguang");
			info.put("晋越快递","jinyue");
			info.put("久易快递","jiuyi");
			info.put("急先达物流","jixianda");
			info.put("嘉里大通物流","jldt");
			info.put("日本邮政","jppost");
			info.put("康力物流","kangli");
			info.put("顺鑫(KCS)快递","kcs");
			info.put("快捷快递","kuaijie");
			info.put("快淘速递","kuaitao");
			info.put("快优达速递","kuaiyouda");
			info.put("宽容物流","kuanrong");
			info.put("跨越快递","kuayue");
			info.put("蓝弧快递","lanhu");
			info.put("乐捷递快递","lejiedi");
			info.put("联昊通快递","lianhaotong");
			info.put("成都立即送快递","lijisong");
			info.put("上海林道货运","lindao");
			info.put("龙邦快递","longbang");
			info.put("门对门快递","menduimen");
			info.put("蒙速快递","mengsu");
			info.put("民邦快递","minbang");
			info.put("明亮物流","mingliang");
			info.put("闽盛快递","minsheng");
			info.put("南北快递","nanbei");
			info.put("尼尔快递","nell");
			info.put("能达快递","nengda");
			info.put("新顺丰（NSF）快递","nsf");
			info.put("OCS快递","ocs");
			info.put("陪行物流","peixing");
			info.put("平安达","pinganda");
			info.put("中国邮政","pingyou");
			info.put("贝邮宝","ppbyb");
			info.put("全晨快递","quanchen");
			info.put("全峰快递","quanfeng");
			info.put("全日通快递","quanritong");
			info.put("全一快递","quanyi");
			info.put("日日顺物流","ririshun");
			info.put("日昱物流","riyu");
			info.put("RPX保时达","rpx");
			info.put("如风达快递","rufeng");
			info.put("瑞丰速递","ruifeng");
			info.put("赛澳递","saiaodi");
			info.put("三态速递","santai");
			info.put("伟邦(SCS)快递","scs");
			info.put("圣安物流","shengan");
			info.put("晟邦物流","shengbang");
			info.put("盛丰物流","shengfeng");
			info.put("盛辉物流","shenghui");
			info.put("世运快递","shiyun");
			info.put("思迈快递","simai");
			info.put("新加坡邮政","singpost");
			info.put("速呈宅配","suchengzhaipei");
			info.put("穗佳物流","suijia");
			info.put("速尔快递","sure");
			info.put("速腾快递","suteng");
			info.put("速通物流","sutong");
			info.put("TNT快递","tnt");
			info.put("高考录取通知书","tongzhishu");
			info.put("合众速递","ucs");
			info.put("USPS快递","usps");
			info.put("万博快递","wanbo");
			info.put("万家物流","wanjia");
			info.put("万象物流","wanxiang");
			info.put("微特派","weitepai");
			info.put("五环速递","wuhuan");
			info.put("祥龙运通快递","xianglong");
			info.put("新邦物流","xinbang");
			info.put("信丰快递","xinfeng");
			info.put("星程宅配快递","xingchengzhaipei");
			info.put("希优特快递","xiyoute");
			info.put("源安达快递","yad");
			info.put("亚风快递","yafeng");
			info.put("燕文物流","yanwen");
			info.put("一邦快递","yibang");
			info.put("银捷快递","yinjie");
			info.put("亿顺航快递","yishunhang");
			info.put("优速快递","yousu");
			info.put("北京一统飞鸿快递","ytfh");
			info.put("远成物流","yuancheng");
			info.put("越丰快递","yuefeng");
			info.put("宇宏物流","yuhong");
			info.put("誉美捷快递","yumeijie");
			info.put("运通中港快递","yuntong");
			info.put("增益快递","zengyi");
			info.put("宅急送快递","zhaijisong");
			info.put("郑州建华快递","zhengzhoujianhua");
			info.put("芝麻开门快递","zhima");
			info.put("济南中天万运","zhongtian");
			info.put("中铁快运","zhongtie");
			info.put("忠信达快递","zhongxinda");
			info.put("中邮物流","zhongyou");
			info.put("纵行物流","zongxing");
			info.put("中铁物流","ztwl");
			info.put("佐川急便","zuochuan");
			names=info.keySet().toArray(new String[0]);
		}

	}

}
