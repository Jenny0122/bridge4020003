package kr.co.wisenut.bridge3.job;

import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Detector {
   // private final Map<String, DetectTarget> targets = new HashMap<>();
    private final Map<String, String> rgxMap;

    public Detector(Map<String, String> rgxMap) {
        this.rgxMap = rgxMap;
    }

    /**
	 * xml 정의된 정규식으로 Alias의 태그 정보와 개인정보 검출 수의 문자열을 만든다.
	 * Ex) JUMIN/1|PHONE/3
	 * @param str
	 * @return
	 */
	public String makeAliasTagWithCount(String str) {
		
		int count = 0;
		StringJoiner sj = new StringJoiner("|");
		for(String id : rgxMap.keySet()){
			String rgx = rgxMap.get(id);
			count = getMatchCount(str, rgx);

			if(count > 0) {
				sj.add(id + "/" + count );
			}
		}
		
		return sj.toString();
	}
	
	/**
	 * 정규식에 매칭되는 그룹의 건수를 반환한다. 
	 * @param strSource
	 * @param strRegExPattern
	 * @return
	 */
	private static int getMatchCount(String strSource, String strRegExPattern) {
        
		int count = 0;
		
		Matcher m =  Pattern.compile(strRegExPattern).matcher(strSource);
        while(m.find()) {
        	//System.out.println(m.group());
        	count++;
        }
       
        return count;
    }
	
	/**
	 * 태그 정보를 카테고리 문자열로 변환한다.
	 * @param str
	 * @return
	 */
	public String makeAliasCategory(String str) {
		String res = str;
		if("".equals(res) || null == res) {
			return "";
		}
		
		StringJoiner sj = new StringJoiner(",");
		String [] strArr = str.split("\\|");
		
		for(String s : strArr) {
			sj.add(s.substring(0, s.indexOf("/")));
		}
		
		return sj.toString();
	}
   
}
