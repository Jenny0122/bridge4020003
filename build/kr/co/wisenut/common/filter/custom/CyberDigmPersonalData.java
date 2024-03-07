package kr.co.wisenut.common.filter.custom;

public class CyberDigmPersonalData {
	private String oid;					//DOCID PK
	private String docSecurityType;		//諛쒓껄�뿬遺�(�쟾泥�) NULL 誘명깘�깋, N 留덇�異�, S 寃�異�
	private String[] privacyType;		//諛쒓껄�뿬遺�(�쑀�삎蹂�)	NULL 誘명깘�깋, N 誘멸�異�, S 寃�異�
	private int[] privacyCount;			//諛쒓껄媛��닔(�쑀�삎蹂�)
	private String personalNumber;		//二쇰�쇰쾲�샇
	private String mobilePhone;			//�쟾�솕踰덊샇
	private String passPort;			//�뿬沅뚮쾲�샇
	private String driverNumber;		//�슫�쟾硫댄뿀踰덊샇
	private String creditCard;			//�떊�슜移대뱶踰덊샇
	private String accountNumber;		//怨꾩쥖踰덊샇
	private String healthNumber;		//嫄닿컯蹂댄뿕踰덊샇

	private String privacyReason;		//�궗�쑀
	
	public static final int PERSONAL_NUMBER=0;
	public static final int MOBILE_PHONE=1;
	public static final int PASS_PORT=2;
	public static final int DRIVER_NUMBER=3;
	public static final int CREDIT_CARD=4;
	public static final int ACCOUNT_NUMBER=5;
	public static final int HEALTH_NUMBER=6;

	
	CyberDigmPersonalData(){
		oid="";
		docSecurityType="";
		personalNumber="";
		mobilePhone="";
		passPort="";
		driverNumber="";
		creditCard="";
		accountNumber="";
		healthNumber="";
		privacyReason="";
		
		docSecurityType="Null";
		privacyType=new String[]{"Null","Null","Null","Null","Null","Null","Null"};
		privacyCount=new int[] {0,0,0,0,0,0,0};
	}

	public CyberDigmPersonalData(String oid, String docSecurityType, String[] privacyType, int[] privacyCount,
			String personalNumber, String mobilePhone, String passPort, String driverNumber,
			String creditCard, String accountNumber, String healthNumber, String privacyReason) {
		this();
		this.oid = oid;
		this.docSecurityType = docSecurityType;
		this.privacyType = privacyType;
		this.privacyCount = privacyCount;
		this.personalNumber = personalNumber;
		this.mobilePhone = mobilePhone;
		this.passPort = passPort;
		this.driverNumber = driverNumber;
		this.creditCard = creditCard;
		this.accountNumber = accountNumber;
		this.healthNumber = healthNumber;
		this.privacyReason = privacyReason;
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getDocSecurityType() {
		return docSecurityType;
	}

	public void setDocSecurityType(String docSecurityType) {
		this.docSecurityType = docSecurityType;
	}

	public String getPrivacyType() {
		String str="";
		for(int i=0;i<this.privacyType.length;i++) {
			str+=this.privacyType[i];
			if(i<this.privacyType.length-1) {
				str+=";";
			}
		}
		return str;
	}

	public void setPrivacyType(String[] privacyType) {
		this.privacyType = privacyType;
	}

	public String getPrivacyCount() {
		String str="";
		for(int i=0;i<this.privacyCount.length;i++) {
			str+=this.privacyCount[i];
			if(i<this.privacyCount.length-1) {
				str+=";";
			}
		}
		return str;
	}

	public void setPrivacyCount(int[] privacyCount) {
		this.privacyCount = privacyCount;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}

	public void setPersonalNumber(String personalNumber) {
		this.personalNumber = personalNumber;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getPassPort() {
		return passPort;
	}

	public void setPassPort(String passPort) {
		this.passPort = passPort;
	}

	public String getDriverNumber() {
		return driverNumber;
	}

	public void setDriverNumber(String driverNumber) {
		this.driverNumber = driverNumber;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getHealthNumber() {
		return healthNumber;
	}

	public void setHealthNumber(String healthNumber) {
		this.healthNumber = healthNumber;
	}
	
	public String getPrivacyReason() {
		return privacyReason;
	}

	public void setPrivacyReason(String privacyReason) {
		this.privacyReason = privacyReason;
	}	
	
	/*
	 寃��궗 �떆�옉
	 */
	public void detect() {
		docSecurityType="N";
		privacyType=new String[]{"N","N","N","N","N","N","N"};
	}
	
	public void findPersonalNumber(String findText) {
		if(this.personalNumber.length()<3000) {
			if(!this.personalNumber.isEmpty()) this.personalNumber+=";;";
			this.personalNumber+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[PERSONAL_NUMBER]="S";
		this.privacyCount[PERSONAL_NUMBER]+=1;
	}
	public void findMobilePhone(String findText) {
		if(this.mobilePhone.length()<3000) {
			if(!this.mobilePhone.isEmpty()) this.mobilePhone+=";;";
			this.mobilePhone+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[MOBILE_PHONE]="S";
		this.privacyCount[MOBILE_PHONE]+=1;
	}
		public void findPassPort(String findText) {
		if(this.passPort.length()<3000) {
			if(!this.passPort.isEmpty()) this.passPort+=";;";
			this.passPort+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[PASS_PORT]="S";
		this.privacyCount[PASS_PORT]+=1;
	}
	public void findDriverNumber(String findText) {
		if(this.driverNumber.length()<3000) {
			if(!this.driverNumber.isEmpty()) this.driverNumber+=";;";
			this.driverNumber+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[DRIVER_NUMBER]="S";
		this.privacyCount[DRIVER_NUMBER]+=1;
	}
	public void findCreditCard(String findText) {
		if(this.creditCard.length()<3000) {
			if(!this.creditCard.isEmpty()) this.creditCard+=";;";
			this.creditCard+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[CREDIT_CARD]="S";
		this.privacyCount[CREDIT_CARD]+=1;
	}
	public void findAccountNumber(String findText) {
		if(this.accountNumber.length()<3000) {
			if(!this.accountNumber.isEmpty()) this.accountNumber+=";;";
			this.accountNumber+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[ACCOUNT_NUMBER]="S";
		this.privacyCount[ACCOUNT_NUMBER]+=1;
	}
	public void findHealthNumber(String findText) {
		if(this.healthNumber.length()<3000) {
			if(!this.healthNumber.isEmpty()) this.healthNumber+=";;";
			this.healthNumber+=findText;
		}
		this.docSecurityType="S";
		this.privacyType[HEALTH_NUMBER]="S";
		this.privacyCount[HEALTH_NUMBER]+=1;
	}
}
