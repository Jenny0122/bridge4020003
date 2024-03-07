/*
 * @(#)BridgeInfoMsg.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.msg;

import kr.co.wisenut.common.util.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * BridgeInfoMsg
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class BridgeInfoMsg {

    private final static String version = "4.0.2";
    private final static String build = "0003";
    private final static String buildName = "released";
    private final static String copyright = "Copyright 2000-2023 WISEnut, Inc. All Rights Reserved.";
    private final static int JBRIDGE = 0;
    private final static int WBRIDGE = 1;
    private final static int FBRIDGE = 2;
    private final static int FTSERVER = 3 ;
    private final static int FILESERVER = 4;
    /**
     * Print Bridge message
     */
    public static void header(int nType) {
        String msgBridge = "";
        switch(nType) {
            case JBRIDGE: msgBridge = "DB Bridge"; break;
            case WBRIDGE: msgBridge = "WEB Bridge";  break;
            case FBRIDGE: msgBridge = "FILE Bridge";  break;
            case FTSERVER: msgBridge = "FtServer";  break;
            case FILESERVER: msgBridge = "FileServer";  break;
            default: msgBridge = ""; break;
        }
        String buildTime = getCompileTimeStamp();

        System.out.println(new StringBuffer().append("\nSearch Formula-1 ")
                .append(msgBridge).append(" v").append(version).append(" (Build ").append(build)
                .append(" ").append("- ").append(buildName).append("), ")
                .append(buildTime).toString());

        System.out.println(copyright);
    }
    
    public static String getVersion() {
    	return "v" + version + "-bld" + build + " " + buildName + ", " + getCompileTimeStamp();  
    }

    public static void usage(int nType) {
        String msgClassName = "";
        String strOption = "";
        switch(nType) {
            case JBRIDGE:
                msgClassName = "kr.co.wisenut.bridge3.JBridge";
                strOption += option("-mode dynamic", "Bridge run mode dynamic");
                strOption += option("-mode replace", "Bridge run mode replace");
                //strOption += option("-mode delete", "Bridge run mode delete");
                break;
            case WBRIDGE: msgClassName = "kr.co.wisenut.wbridge3.WBridge";  break;
            case FBRIDGE:
                msgClassName = "kr.co.wisenut.fbridge3.FBridge";
                strOption += option("-mode dynamic", "Bridge run mode dynamic");
                break;
            default: msgClassName = ""; break;
        }
        String usage = "\n";
        usage += "Usage : java -Dsf1_home=<SF1_HOME> -Dsf1.ver=<4 or 5 or 6 or 7> "+ msgClassName +"\n";
        usage += StringUtil.padLeft("-conf <file path> -srcid <srcid> -mode <mode> \n\n", ' ', 80);
        usage += "mode include:\n";
        usage += option("-mode init", "Bridge run mode init");
        usage += option("-mode test", "Bridge run mode test");
        usage += option("-mode static", "Bridge run mode static");
        usage += strOption;

        usage += "\n";
        usage += "Options include:\n";
        usage += option("-log <stdout|day>", "");
        usage += option("-verbose", "Crawl Data View Mode [default:false]");
        usage += option("-filterdel <true|false>", "Filtered text file delete option [default:true]");
        usage += option("-indexdir", "Make SCD File Directory is index [default:false]");
        usage += option("-debug <1~4>", "Debug Mode Run [default: -debug 2]");
        usage += desc("1 : ERROR 2: WARNING\n");
        usage += desc("3: INFO 4 : DEBUG\n");
        usage += option("-help", "help");
        System.out.println(usage);
    }

    public static void usageSF1_HOME() {
        String usage = "*** Error sf1_home ***\n";
        usage += "use -D<name>=<value> option in java\n";
        usage += "usage: java -Dsf1_home=<sf1_home>";
        System.out.println(usage);
    }
    
	public static void usageSF1_VER() {
        String usage = "*** Error sf1.ver ***\n";
        usage += "usage: java -Dsf1.ver=<4|5|6>\n";
        usage += "       value 4 : sf-1 v4.x or lower\n";
        usage += "       value 5 : sf-1 v5.x or more";
        usage += "       value 6 : sf-1 v6.x or more";
        System.out.println(usage);
	}    

    public static void ImvalidArg(String arg) {
        System.out.println("*** Invalid Run-time arguments check arguments" +
                " ***\n>> argument name: " + arg);
    }

    public static String option(String oName, String desc){
        String option = StringUtil.padRight("    "+oName, ' ', 20);
        option += desc+"\n";
        return option;
    }

    public static String desc(String desc){
        return StringUtil.padRight("", ' ', 20) + desc;
    }

    public static String getCompileTimeStamp( ) {
        return "Oct 31 2023 16:28:20";
    }
}
