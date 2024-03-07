package kr.co.wisenut.common.filter.custom.softforum;

import java.io.*;
import java.net.*;
import java.util.Properties;

import kr.co.wisenut.common.constants.SF1Constants;
import kr.co.wisenut.common.logger.Log2;

import com.sf.xc3.*;

public class DocEncrypt 
{
	private String targetFile = "";	
	private static String CONF = "";
	private static String PASS = "";

	XC_v3 xc_server = null;
	XC_v3 xc_client = null;
	
	public static void main(String args[])
	{
		DocEncrypt doc = new DocEncrypt("hello softforum");
		
		if(doc.init())
		{
			String str = doc.WNEncode();
			
			System.out.println(str);
		}
		doc.end();
	}
	
	public DocEncrypt(String targetFile)
	{
		String X3_Home 	= System.getProperty("xc3_home");
		CONF 			= X3_Home + "/conf/xc_conf.txt";
		String sf1_home = System.getProperty("sf1_home");
		getProperty(sf1_home);
		
		this.targetFile = targetFile;
		
		Log2.debug("[DocEncrypt] SF1_HOME path : " + sf1_home, SF1Constants.DEBUG_DETAIL);
		Log2.debug("[DocEncrypt] Configuration file path : " + CONF, SF1Constants.DEBUG_DETAIL);
		Log2.debug("[DocEncrypt] XC3_HOME paht : " + X3_Home, SF1Constants.DEBUG_DETAIL);
	}
	
	private void getProperty(String sf1_home)
	{
		Properties props = new Properties();
		FileInputStream fis;
		String filepath = sf1_home + "/bridge/softforum.properties";
		try 
		{
			fis = new FileInputStream(filepath);
			Log2.debug("[DocEncrypt] [getProperty] Properties file path : " + filepath, SF1Constants.DEBUG_DETAIL);
			props.load(new java.io.BufferedInputStream(fis));
			
			PASS = props.getProperty("password").trim();
	        fis.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean init()
	{
		if(init_server())
		{
			return init_client();
		}
		else
		{
			return false;
		}
	}
	
	private boolean init_server()
	{
		boolean connYN = false;
		xc_server = new XC_v3();
		
		try 
		{
			int ret = xc_server.init(PASS, CONF, SF1Constants.SOFTFORUM_SERVER_MODE);
			
			if(ret < 0)
			{
				Log2.error("[DocEncrypt] [init_server] init failed [" + ret + "]");
				xc_server.close();
			}
			else
			{
				Log2.debug("[DocEncrypt] [init_server] Server Connection Success", SF1Constants.DEBUG_DETAIL);
				connYN = true;
			}
		} 
		catch (XCException e) 
		{
			e.printStackTrace();
		}
		
		return connYN;
	}
	
	private boolean init_client()
	{
		boolean connYN = false;
		xc_client = new XC_v3();
		
		try 
		{
			xc_client.init("", CONF, SF1Constants.SOFTFORUM_CLIENT_MODE);
			
			String sendKey = xc_client.keyInit();
			
			String rcvKey = getMessage(sendKey);
			
			int ret = xc_client.keyFinal(rcvKey);
			
			if(ret < 0)
			{
				Log2.error("[DocEncrypt] [init_client] init failed [" + ret + "]");
				xc_server.close();
			}
			else
			{
				Log2.debug("[DocEncrypt] [init_client] Client Connection Success", SF1Constants.DEBUG_DETAIL);
				connYN = true;
			}
		} 
		catch (XCException e) 
		{
			e.printStackTrace();
		}
		
		return connYN;
	}
	
	public String WNEncode()
	{
		try 
		{
			xc_client.msgLong();
			targetFile = xc_client.encode(targetFile);
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (XCException e) 
		{
			e.printStackTrace();
		}
		String str = getMessage(targetFile, SF1Constants.SOFTFORUM_ENCODE);
		return str;
	}
	
	public String WNDecode()
	{
		String str = getMessage(targetFile, SF1Constants.SOFTFORUM_DECODE);
		
		return str;
	}

	private String getMessage(String str)
	{
		return getMessage(str, 0);
	}
	
	private String getMessage(String str, int type)
	{
		String msg = "";
		
		byte[] input = Base64.decode(str);
		
		try 
		{
			switch (input[0]) 
			{
				case SF1Constants.SOFTFORUM_SESSION_KEY:
					msg = xc_server.keyFinal(input, SF1Constants.SOFTFORUM_SERVER_MODE);
					break;
				case SF1Constants.SOFTFORUM_CIPHER:
					if(type == SF1Constants.SOFTFORUM_DECODE)
					{
						msg = xc_server.decode(input, false);
					}
					else if(type == SF1Constants.SOFTFORUM_ENCODE)
					{
						msg = xc_server.encode(str);
					}
				default:
					break;
			}
		} 
		catch (XCException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		return msg;
	}
	
	public void end()
	{
		xc_server.close();
		xc_client.close();
	}
}