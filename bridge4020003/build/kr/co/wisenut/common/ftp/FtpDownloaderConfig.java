package kr.co.wisenut.common.ftp;

import java.io.InputStream;
import java.util.Properties;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.util.StringUtil;

public class FtpDownloaderConfig {
	public String ip;
	public int port;
	public String userId;
	public String passwd;
	public boolean binary;
	public boolean passive;

	public FtpDownloaderConfig() throws ConfigException {
		try {
			Properties props = new Properties();

			InputStream is = getClass().getResourceAsStream("/ftp.conf");
			if(is == null) {
				throw new Exception("ftp.conf is not found.");
			}
			props.load(is);
			ip = getRequiredPropertyString("ftp.ip", props);
			port = getRequiredPropertyInt("ftp.port", props);
			userId = getRequiredPropertyString("ftp.userid", props);
			passwd = getRequiredPropertyString("ftp.passwd", props);
			binary = getRequiredPropertyBoolean("ftp.binary", props);
			passive = getRequiredPropertyBoolean("ftp.passive", props);

		} catch (Exception e) {
			throw new ConfigException("FtpDownloaderConfig fail. : " +  e.getMessage() );
		}
	}

	private String[] getPropertyArray(String propertyName, Properties props) throws ConfigException {
		String value = getPropertyString(propertyName, props, "");
		return StringUtil.split(value, ",", true, true);
	}

	private boolean getRequiredPropertyBoolean(String propertyName, Properties props) throws ConfigException {
		String value = getRequiredPropertyString(propertyName, props);
		if (value.equalsIgnoreCase("y")) {
			return true;
		} else {
			return false;
		}
	}

	private String getPropertyString(String propertyName, Properties props, String replace) throws ConfigException {
		String value = props.getProperty(propertyName);
		if (value == null) {
			throw new ConfigException(propertyName + " field is not found.");
		}
		value = value.trim();
		if (value.equals("")) {
			return replace;
		}
		return value;
	}

	private String getRequiredPropertyString(String propertyName, Properties props) throws ConfigException {
		String value = props.getProperty(propertyName);
		if (value == null) {
			throw new ConfigException(propertyName + " field is not found.");
		}
		value = value.trim();
		if (value.equals("")) {
			throw new ConfigException(propertyName + " value is not found.");
		}
		return value;
	}

	private int getRequiredPropertyInt(String propertyName, Properties props) throws ConfigException {
		String value = getRequiredPropertyString(propertyName, props);
		int result = 0;
		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new ConfigException(propertyName + " is not integer.");
		}
		return result;
	}

}
