package info.papdt.express.helper.support;

public class Express {

	private String companyCode, mailNumber;
	private String jsonData;

	public Express(String companyCode, String mailNumber){
		this.companyCode = companyCode;
		this.mailNumber = mailNumber;
	}
	
	public String getData() {
		return jsonData;
	}
	
	public void setData(String jsonStr) {
		this.jsonData = jsonStr;
	}

	public String getCompanyCode(){
		return companyCode;
	}

	public String getMailNumber(){
		return mailNumber;
	}

	public void setCompanyCode(String companyCode){
		this.companyCode = companyCode;
	}

	public void setMailNumber(String mailNumber){
		this.mailNumber = mailNumber;
	}

}
