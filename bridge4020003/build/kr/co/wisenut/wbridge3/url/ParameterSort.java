package kr.co.wisenut.wbridge3.url;

import kr.co.wisenut.common.util.StringUtil;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009. 7. 2
 * Time: �삤�쟾 11:22:18
 * To change this template use File | Settings | File Templates.
 */
public class ParameterSort {
    private static StringBuffer buf = new StringBuffer(1024);

    // 2009-07-01 seedurl �뿉�꽌 Pramater瑜� 遺꾨━�븯�뿬 �젙�젹 �썑 ���옣�븳�떎.
    public static String sortUrlParameter(String seedurl) {
        String[] urlArray = StringUtil.split(seedurl, "&");
        List list = new ArrayList();
        for(int s = 1; urlArray.length > 1 && s < urlArray.length; s++) {
            list.add(urlArray[s]);
        }
        buf.setLength(0);
        buf.append(urlArray[0]);
        List sortList = StringUtil.stringListSort(list);
        for(int i=0;i<sortList.size();i++){
            String string = (String)sortList.get(i);
            buf.append("&").append(string);
        }
        return buf.toString();
    }
}
