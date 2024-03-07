package kr.co.wisenut.common.filter.custom;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import cyberdigm.blues.IDataTransfer;
import cyberdigm.storage.StorageObject;
import cyberdigm.storage.httpclient.HttpStorageProxy;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

public class CyberDigmFilter2 extends Filter {
	private Properties prop = new Properties();
	private Properties filter = new Properties();
	private String configPrefix;
	// private String mode = "dev";
	private String mode = "prd";

	//private String sf1Home = "C:\\sf1\\sf1-v53\\";
	private String sf1Home = "/app/sf1-v5/";


	//private String url = "";
	//private String user = "";
	//private String password = "";

	//private String[] regular;
	// private ArrayList<CyberDigmPersonalData> detectList = new ArrayList<CyberDigmPersonalData>();

	//private int tempInt = 0;

	public CyberDigmFilter2(Boolean filterDel) {
		super(filterDel);

		try {
			//String configPath = new StringBuffer(sf1Home + "config\\sf1.properties").toString();
			String configPath = new StringBuffer(sf1Home + "config/sf1.properties").toString();
			this.prop.load(new FileInputStream(configPath));
			this.mode = prop.getProperty("mode");
			configPrefix = new StringBuffer(this.getClass().getName()).append(".").append(mode).append(".").toString();

			//String fileallow = new StringBuffer(sf1Home + "config\\file.allow").toString();
			String fileallow = new StringBuffer(sf1Home + "config/file.allow").toString();
			this.filter.load(new FileInputStream(fileallow));

			// set personal inform regular
			/*this.regular = new String[7];
			this.regular[0] = this.prop.getProperty(configPrefix + "regular.PersonalNumber");
			this.regular[1] = this.prop.getProperty(configPrefix + "regular.MobilePhone");
			this.regular[2] = this.prop.getProperty(configPrefix + "regular.PassPort");
			this.regular[3] = this.prop.getProperty(configPrefix + "regular.DriverNumber");
			this.regular[4] = this.prop.getProperty(configPrefix + "regular.CreditCard");
			this.regular[5] = this.prop.getProperty(configPrefix + "regular.AccaountNumber");
			this.regular[6] = this.prop.getProperty(configPrefix + "regular.HealthNumber");*/
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.setProperty("cyberdigm.storage.server.StorageProxy",
		// this.prop.getProperty(configPrefix+"cyberdigm.storage.server.StorageProxy"));
		// System.setProperty("storage.contextpath",
		// this.prop.getProperty(configPrefix+"storage.contextpath"));
		System.setProperty("storage.contextpath", "");
	}

	public void setFilteredTextDir(String filteredTextDir) {
		super.filteredTextDir = filteredTextDir;

	}

	@Override
	public String getFilterData(String[][] sourceFileInfo, FilterSource filter, String charset) {
		// personal infomation custom
		String preFixDir = filter.getDir();
		String retrieve = filter.getRetrival();
		String condition = filter.getCondition();
		// String jungumKey = filter.getJungumKey();
		// String split = filter.getSplit();
		String filterType = filter.getFilterType();
		Object filterSize = this.prop.getProperty(configPrefix + "filterSize");
		long fileMaxSize = Long.parseLong(this.prop.getProperty(configPrefix + "fileMaxSize"));
		StringBuffer result = new StringBuffer();

		filterSize = filterSize != null ? new Integer((String) filterSize) : 1024 * 1024 * 3;
		filterSize = (Integer) filterSize * (charset.equalsIgnoreCase("UTF-8") ? 3 : 1);

		// print debug message
		Log2.debug("[CyberDigmFilter2] [Custum Filter -> kr.co.wisenut.common.filter.custom.CyberDigmFilter2]", 4);
		debug(sourceFileInfo, preFixDir, retrieve, condition, filterType);

		long fileId = 0L;
		File srcFile = null;

		super.filteredTextDir = super.FILTER_ROOT + filter.getDir();
		FileUtil.makeDir(super.filteredTextDir);

		// filefilter setting
		String allow = this.filter.getProperty("allow");
		String[] fileallow = allow.split(",");
//		FileUtil.setFilterExt(fileallow);

		Log2.debug("[CyberDigmFilter2] [FileFiltering start]", 3);
		for (int x = 0; x < sourceFileInfo.length; x++) {
			//boolean detect = false;
			// scdfilter
			String[] scdfilter = sourceFileInfo[x][0].split(",");
			String chk_ext_condition = condition;

			// personal info suctom
			CyberDigmPersonalData ps = new CyberDigmPersonalData();
			ps.setOid(scdfilter[2]);

			if ((scdfilter[0]) != null && ((scdfilter.length > 1) || (scdfilter[0].lastIndexOf(".") == -1))) {
				chk_ext_condition = "none-chk-ext";
			}

			if ((scdfilter.length > 1) && (FileUtil.isFiltered(scdfilter[1]) == false)) {
				result.append(" ");
				Log2.debug(
						"[CyberdigmFilter2] [None Filtered File Ext FileName or Ext Name, scdfilter[1] is FileName or FileExt"
								+ scdfilter[1] + "]",
						3);
			} else {
				try {
					Log2.debug("[CyberDigmFilter2] [FileFiltering index number = " + x + " ]", 4);
					srcFile = new File(new StringBuffer(this.prop.getProperty(configPrefix + "downloadPath"))
							.append("/").append(scdfilter[0]).append(".").append(FileUtil.getFileExt(scdfilter[1]))
							.toString());

					if (!srcFile.exists()) {
						if (!srcFile.createNewFile()) {
							Log2.debug("[CyberdigmFilter2] [TempFile Creation Fail! : " + srcFile + " ]", 1);
						}
					}

					Log2.debug("[CyberdigmFilter2] [CyberdigmFilter] " + srcFile + " exists-" + srcFile.exists() + " ]",
							3);

					// document download from cyberdigm was
					String ip = this.prop.getProperty(configPrefix + "ip");
					int port = Integer.parseInt(this.prop.getProperty(configPrefix + "port"));
					fileId = Long.parseLong(scdfilter[0]);

					Log2.debug("[CyberdigmFilter2] [ IP : " + ip + " , port : " + port + " , fileId : " + fileId + " ]",
							4);

					HttpStorageProxy sp = new HttpStorageProxy(ip, port);
					StorageObject so = new StorageObject(fileId);
					so.setID(fileId);

					IDataTransfer dt = sp.get(srcFile, so);
					if (dt.isComplete(true) == false) {
						Log2.debug("[CyberdigmFilter2] [Invalid File ID : " + fileId + " ]", 2);
					} else {
						Log2.debug("[CyberdigmFilter2] [File ID set Successful]", 3);
					}

					// synap document filter
					if (srcFile.exists()) {
						if (srcFile.length() > fileMaxSize) {
							Log2.debug("[CyberdigmFilter2] [File Too Big to Filtering : " + fileId + " ]", 2);
						} else {
							String targetFile = super.filteredTextDir + StringUtil.getTimeBasedUniqueID() + ".txt";
							if (super.filteringFile(srcFile.getPath(), targetFile, chk_ext_condition)) {
								
								 result.append(super.readTextFile(targetFile, (Integer)filterSize,
								 charset.toLowerCase().equals("utf-8")?"UTF-16":null)); 
								 result.append(" ");
								 

								// personal infomation custom
								/* String bufText = super.readTextFile(targetFile, (Integer) filterSize,
										charset.toLowerCase().equals("utf-8") ? "UTF-16" : null);
								result.append(bufText + " ");

								detect = true;
								ps.detect();

								for (int y = 0; y < regular.length; y++) {
									Pattern patt = Pattern.compile(regular[y]);
									Matcher match = patt.matcher(bufText);

									while (match.find()) {

										tempInt++;
										String text = match.group(1);

										switch (y) {
										case 0:
											ps.findPersonalNumber(text);
											break;
										case 1:
											ps.findMobilePhone(text);
											break;
										case 2:
											ps.findPassPort(text);
											break;
										case 3:
											ps.findDriverNumber(text);
											break;
										case 4:
											ps.findCreditCard(text);
											break;
										case 5:
											ps.findAccountNumber(text);
											break;
										case 6:
											ps.findHealthNumber(text);
											break;
										default:
											break;
										}
										if (match.group(1) == null)
											break;
									}
								} */

							}
						}
						// download document delete
						srcFile.delete();
					}

				} catch (Exception e) {
					StringBuffer exLog = new StringBuffer();
					exLog.append(this.prop.getProperty(configPrefix + "ip")).append(":");
					exLog.append(this.prop.getProperty(configPrefix + "port")).append(", ");
					exLog.append(fileId).append(", ");
					exLog.append(srcFile).append("\n");
					exLog.append(e.getMessage()).append("\n");

					for (StackTraceElement el : e.getStackTrace()) {
						exLog.append(el).append("\n");
					}
					Log2.debug("[Exception FILEID " + fileId + " ]", 2);
					Log2.debug(exLog.toString(), 4);

				}
			}
			// personal info detect
			//if (detect)
			//	detectList.add(ps);

		}

		// Log2.debug("detectList length=" + detectList.size() + "   detect count=" + tempInt, 3);

		/* if (detectList.size() > 0) {
			// insert
			Connection con = null;
			PreparedStatement pstmt = null;
			PreparedStatement prePS = null;

			String driver = "org.mariadb.jdbc.Driver";

			url = this.prop.getProperty("url");
			user = this.prop.getProperty("user");
			password = this.prop.getProperty("password");
			String preSQL = "delete from xprivacy where OID=?";
			String SQL = "insert into xprivacy(OID, DocSecurityType, PrivacyType, PrivacyCount, PersonalNumber, MobilePhone, PassPort, DriverNumber, CreditCard, AccountNumber, HealthNumber) values(?,?,?,?,?,?,?,?,?,?,?)";
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				prePS = con.prepareStatement(preSQL);
				pstmt = con.prepareStatement(SQL);

				int i = 0;
				for (CyberDigmPersonalData psInfo : detectList) {
					i++;
					prePS.setString(1, psInfo.getOid());
					prePS.addBatch();

					pstmt.setString(1, psInfo.getOid());
					pstmt.setString(2, psInfo.getDocSecurityType());
					pstmt.setString(3, psInfo.getPrivacyType());
					pstmt.setString(4, psInfo.getPrivacyCount());
					pstmt.setString(5, psInfo.getPersonalNumber());
					pstmt.setString(6, psInfo.getMobilePhone());
					pstmt.setString(8, psInfo.getPassPort());
					pstmt.setString(9, psInfo.getDriverNumber());
					pstmt.setString(10, psInfo.getCreditCard());
					pstmt.setString(11, psInfo.getAccountNumber());
					pstmt.setString(7, psInfo.getHealthNumber());

					pstmt.addBatch();
					if (i > 10000) {
						prePS.executeUpdate();
						pstmt.executeUpdate();
						i = 0;
					}
				} 

				if (i != 0) {
					prePS.executeUpdate();
					pstmt.executeUpdate();
				}
			} catch (SQLException | ClassNotFoundException e) {
				System.out.println("[SQL Error : " + e.getMessage() + "]");
			} finally {
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						// e.printStackTrace();
					}
				}
				if (prePS != null) {
					try {
						prePS.close();
					} catch (SQLException e) {
						// e.printStackTrace();
					}
				}

				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						// e.printStackTrace();
					}
				}
			}
		} */

		return result.toString();
	}
}