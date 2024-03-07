package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;

public class ChosungFilter implements ICustom {
	private final static boolean IS_DEBUG = "ChosungFilter".equals(System.getProperty("debug"));
	public final char[] chosung = {'\u3131','\u3132','\u3134','\u3137','\u3138','\u3139','\u3141','\u3142','\u3143','\u3145','\u3146','\u3147','\u3148','\u3149','\u314a','\u314b','\u314c','\u314d','\u314e' };
	private boolean onlyKor = false;

	public String customData(String str) throws CustomException {
		return run(str);
	}

	private String run(String str) {
		String result = "";
		result = toChoSung(str);
		if( IS_DEBUG ){
			System.out.println( "[debug][ChosungFilter]" + str + " : " + result);
		}
		return result;
	}

	public String toChoSung(String s) {
        if (s == null)
            return "";
        if( "".equals(s.trim()))
            return "";
        String t = "";
        String tmp = "";
        int n, n1;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            n = (int)(c & 0xFFFF);
            if (n >= 0xAC00 && n <= 0xD7A3) {
                n = n - 0xAC00;
                n1 = n / (21 * 28);
                tmp = "" + chosung[n1] ;
                t += tmp;
            } else if ( !onlyKor && ((n>=0x41 && n<=0x5A) || ( n>=0x61 && n<=0x7A))){
            	t += (char)n;
            }

        }

        return t;
    }

	public static void main(String[] args) {
		System.out.println( "usage : className=\"kr.co.wisenut.bridge3.job.custom.ChosungFilter\"");
		if( args.length > 0 ) {
			ChosungFilter app = new ChosungFilter();
			System.out.println( "input : " + args[0]);
			System.out.println( "output : " + app.run(args[0]));
		}
	}
}
