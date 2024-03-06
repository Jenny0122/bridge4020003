package kr.co.wisenut.common.util;

import kr.co.wisenut.common.logger.ILogger;

/**
 * bridge 에서 공통적으로 사용되는 util class.
 *
 */
public class BridgeCommonUtil {

	/**
	 * sf1.ver 환경변수 값 반환.
	 * 
	 * @return
	 * @throws Exception
	 */
	 /*
	public static String getSf1VersionType() throws Exception {
		String sf1VersionType = System.getProperty("sf1.ver");

		if (sf1VersionType == null) {
			throw new Exception("sf1.ver is null.");

		}
		if (!sf1VersionType.equals(ILogger.SF1_VERSION_TYPE_4) && !sf1VersionType.equals(ILogger.SF1_VERSION_TYPE_5) && !sf1VersionType.equals(ILogger.SF1_VERSION_TYPE_6)) {
			throw new Exception("sf1.ver invalid.");
		}

		return sf1VersionType;
	}
	*/

}
