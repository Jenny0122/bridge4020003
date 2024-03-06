package kr.co.wisenut.common.util.http;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009. 8. 21
 * Time: 오후 1:21:15
 * To change this template use File | Settings | File Templates.
 */
public class URLUtil {
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";

    public static URIInfo getUriInfo(String url) {
        URIInfo uriInfo = new URIInfo();
        if(url.indexOf(HTTP) != -1 || url.toLowerCase().indexOf(HTTP) != -1) {
            url = url.substring(HTTP.length());
        } else if(url.indexOf(HTTPS) != -1 || url.toLowerCase().indexOf(HTTPS) != -1) {
            url = url.substring(HTTPS.length());
        }
        int idx = url.indexOf("/");
        int idxPort = url.indexOf(":");
        if(idxPort != -1 && idx > idxPort) {
            if(idx != -1) {
                uriInfo.setDomain(url.substring(0, url.indexOf(":")));
            } else {
                uriInfo.setDomain(url);
            }
        } else {
            if(idx != -1) {
                uriInfo.setDomain(url.substring(0, idx));
            } else {
                uriInfo.setDomain(url);
            }
        }


        if(idxPort != -1 && idxPort < idx) {
            uriInfo.setPort(Integer.parseInt(url.substring(idxPort+1, idx)));
        }
        if(url.length() > idx && idx > 0) {
            uriInfo.setPrarameter(url.substring(idx));
        }
        return uriInfo;
    }
}
