package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;

public class Test implements ICustom {

	public String customData(String str) throws CustomException {
		return str + "111";
	}

}
