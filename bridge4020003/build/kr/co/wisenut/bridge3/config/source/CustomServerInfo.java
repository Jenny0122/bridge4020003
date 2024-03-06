package kr.co.wisenut.bridge3.config.source;

public class CustomServerInfo {
    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private int portNumber = -1;
    private String serverName;
}
