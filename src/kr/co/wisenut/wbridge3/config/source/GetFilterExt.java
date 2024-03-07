package kr.co.wisenut.wbridge3.config.source;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.XmlUtil;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

public class GetFilterExt extends XmlUtil {
    private Element rootElement;

    public GetFilterExt(String path) throws ConfigException {
        super(path);
        rootElement = getRootElement();
    }

    public void getFilterExtInfo() throws ConfigException {
        int size = 0;
        List lds = null;
        if(rootElement.getChild("FilterExt") != null){
            lds = rootElement.getChild("FilterExt").getChildren("ext");
            size = lds.size();
        }

        ArrayList list = new ArrayList(100);
        String text = "";
        String[] extList = null;
        for(int i=0; i<size;i++) {
            Element element = (Element) lds.get(i);
            text = element.getText();
            extList = StringUtil.split(text, ",");
            for(int k=0; k < extList.length; k++) {
                list.add(extList[k].trim());
            }
//            System.out.println("text=" + text);
        }
        if(list.size() > 0) {
            extList = new String[list.size()];
            for(int si = 0; si < list.size(); si++) {
                extList[si] = (String) list.get(si);
            }
            FileUtil.setFilterExt(extList);
        }
    }
}