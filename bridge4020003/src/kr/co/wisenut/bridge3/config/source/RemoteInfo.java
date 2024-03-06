package kr.co.wisenut.bridge3.config.source;

/**
 * Date: 2010. 6. 14
 */
public class RemoteInfo {
    private String ip ;
    private int  port ;
    private boolean isDeleteSCD = false;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDeleteSCD() {
        return isDeleteSCD;
    }

    public void setDeleteSCD(boolean deleteSCD) {
        isDeleteSCD = deleteSCD;
    }
}
