package kr.co.wisenut.bridge3.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sleepycat.je.config.ConfigParam;

public class DTDChecker {
	private String configPath = "";
	private String errMessage = "";
	private int errNum = 0;	// 0:Normal,1:Warning,2:Error,3:FatalError
	
	/**
	 * 
	 * Constructor of DTDChecker
	 * @param configPath bridge configuration file path
	 */
	public DTDChecker(String configPath){
		this.configPath = configPath;
	}
	
	private ErrorHandler getErrorHandler() {
		ErrorHandler dtdErrorHandler = new ErrorHandler() {
			
			public void warning(SAXParseException exception) throws SAXException {
				errNum = 1;
				errMessage = exception.toString();
			}
			
			public void error(SAXParseException exception) throws SAXException {
				errNum = 2;
				errMessage = exception.toString();
			}
			
			public void fatalError(SAXParseException exception) throws SAXException {
				errNum = 3;
				errMessage = exception.toString();
			}
		};
		
		return dtdErrorHandler;
	}
	
	/**
	 * XML DTD Check
	 * 
	 * @return true/false
	 */
	public boolean isValidXML() {
		boolean isValid = true;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		dbf.setValidating(true);
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			db.setErrorHandler(getErrorHandler());
			
			db.parse(new File(this.configPath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			isValid = false;
		} catch (SAXException e) {
			e.printStackTrace();
			isValid = false;
		} catch (IOException e) {
			e.printStackTrace();
			isValid =  false;
		}
		
		return isValid;
	}
	
	/**
	 * XML DTD Check Result Message
	 * 
	 * @return message
	 */
	public String getErrorMessage() {
		switch(errNum) {
			case 0 :
				return "DTD Check Complete";
			case 1 :
				return "[warning] " + errMessage;
			case 2 :
				return "[error] " + errMessage;
			case 3 :
				return "[fatalerror] " + errMessage;
		}
		
		return "";
	}
}
