package kr.co.wisenut.common.scdreceiver;

import java.io.IOException;
import java.net.Socket;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.scdreceiver.stream.MessageInputStream;
import kr.co.wisenut.common.scdreceiver.stream.MessageOutputStream;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 6. 14
 * Time: 오후 3:30:59
 * To change this template use File | Settings | File Templates.
 */
public class FClient {
    public String CMD_DIR = "CMD:DIRECTORY";
    public String CMD_DISCONNECT = "CMD:DISCONNECT";
    public String CMD_SHUTDOWN = "CMD:SHUTDOWN";
    private Socket socket;
    private MessageInputStream messageIn;
    private MessageOutputStream messageOut;

    public FClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        //System.out.println(socket.getKeepAlive());
        messageIn = new MessageInputStream(socket.getInputStream());
        messageOut = new MessageOutputStream(socket.getOutputStream());
    }

    public void command(String msg) throws IOException {
        if(msg.equals(CMD_DISCONNECT)) {
            send(CMD_DISCONNECT);
            try {
                Thread.sleep(1000*2);
            } catch (InterruptedException e) {
                Log2.error("[FClient] [command()  "+"\n"+ IOUtil.StackTraceToString(e)+"\n]");
            }
            messageIn.close();
            messageOut.close();
            socket.close();
        }else if(msg.equals(CMD_SHUTDOWN)) {
           send(CMD_SHUTDOWN);
            try {
                Thread.sleep(1000*2);
            } catch (InterruptedException e) {
                Log2.error("[FClient] [command()  "+"\n"+ IOUtil.StackTraceToString(e)+"\n]");
            }
            messageIn.close();
            messageOut.close();
            socket.close();
        } else {
            send(msg);      // send to message
        }
    }

    public void writeBuffer(byte[] buf) throws IOException {
        synchronized(messageOut) {
            messageOut.write(buf);
            messageOut.send();
        }
    }

    public String receive() throws IOException {
        messageIn.receive();
        return messageIn.readUTF();
    }

    public void send(String str) throws IOException {
        synchronized(messageOut) {
            messageOut.writeUTF(str);
            messageOut.send();
        }
    }

	public void close() throws IOException {
		try {
			command(FSListener.CMD_DISCONNECT);
		}finally{
			socket.close();
		}
	}
}
