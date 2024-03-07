package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.common.util.HtmlUtil;

public class ConvertHtmlEntity implements ICustom {
    public String customData(String str) throws CustomException {
        str = HtmlUtil.convertFormattedTextToPlaintext(str);
        return str;
    }
}
