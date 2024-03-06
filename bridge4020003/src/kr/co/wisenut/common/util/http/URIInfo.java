package kr.co.wisenut.common.util.http;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009. 8. 21
 * Time: 오후 1:20:30
 * To change this template use File | Settings | File Templates.
 */
public class URIInfo {
    private String domain;
    private int port = 80;
    private String prarameter = "/";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrarameter() {
        return prarameter;
    }

    public void setPrarameter(String prarameter) {
        this.prarameter = prarameter;
    }
}
