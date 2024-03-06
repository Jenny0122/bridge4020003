package kr.co.wisenut.common.scdreceiver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.scdreceiver.stream.MessageInputStream;
import kr.co.wisenut.common.scdreceiver.stream.MessageOutputStream;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 6. 14
 * To change this template use File | Settings | File Templates.
 */
public class FSListener extends Thread {
    public static String CMD_DISCONNECT = "CMD:DISCONNECT";
    public static String CMD_SHUTDOWN = "CMD:SHUTDOWN";
    public static String CMD_WRITE_EOF = "CMD:WRITE_EOF";
    public static String CMD_WRITE_NONE_EOF = "CMD:WRITE_NONE_EOF";
    public static String CMD_WRITE_OK = "CMD:WRITE_OK";
    public static String CMD_WRITE_EXP = "CMD:WRITE_EXP";

    private String clientIP;
    private MessageOutputStream messageOut;
    private MessageInputStream messageIn;
    private Socket socket;

    public FSListener(Socket socket) throws IOException {
        this.socket = socket;
        clientIP = socket.getInetAddress().toString();
        messageIn = new MessageInputStream(socket.getInputStream());
        messageOut = new MessageOutputStream(socket.getOutputStream());
    }

    public void run() {
        try {
            while( !Thread.interrupted() ) {
                 messageIn.receive();
                String msg = messageIn.readUTF();
                Log2.debug("Cient Msg> " + msg, 3);
                if(msg.equals(CMD_DISCONNECT)) { // FtServer Thread stop
                    Log2.debug("SCDReceiver : Disconnect FtClient ClientIP("+clientIP+")", 3);
                    this.interrupt();
                } else if(msg.equals(CMD_SHUTDOWN)) {
                    Log2.out("SCDReceiver : Stop server");

                    this.interrupt();

                    messageIn.close();
                    messageOut.close();
                    socket.close();

                    System.exit(0);

                }else {
                    receiveFile(msg);
                }
            }
        } catch (Exception e) {
            Log2.error("[FSListener] [Run() "
                    +"\n"+ IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            try {

                messageIn.close();
                messageOut.close();
                socket.close();
            } catch (IOException e) {
                Log2.error("[FSListener] [Run():message "
                        +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            }
        }
    }

    private void receiveFile(String path) {
    	RandomAccessFile raf = null;
        try {
            byte[] buf = null;
            boolean  EOF = false;
            FileUtil.makeDir(new File(path).getParent());
            raf = new RandomAccessFile(path, "rw");

            messageIn.receive();
            
            String cmdMsg = messageIn.readUTF();
            
            if(cmdMsg.equals(CMD_WRITE_EOF)) {
            	EOF = true;
            	
            	messageIn.receive();
            	
            	buf = new byte[messageIn.available()];
            	messageIn.read(buf);
            	raf.write(buf);
            	messageOut.writeUTF(CMD_WRITE_OK);
            	messageOut.send();
            } else  if(cmdMsg.equals(CMD_WRITE_NONE_EOF)) {
            	while ( true ) {
            		messageIn.receive();
                    buf = new byte[messageIn.available()];
                    messageIn.read(buf);
                    raf.write(buf);
                    messageOut.writeUTF(CMD_WRITE_OK);
                    messageOut.send();
            		
            		messageIn.receive();
                    
                    if(messageIn.readUTF().equals(CMD_WRITE_EOF)) {
                    	// if (CMD:WRITE_EOF) then RandomAccess Stop
                        break;
                    }
            	}
            }
            
            raf.close();
            rename(path);
        } catch (Exception e) {
            Log2.error("[FSListener] [Revice File Error: " + path+"]");
            Log2.error("[FSListener] [ReceiveFile() "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            try {
            	raf.close();
                messageOut.writeUTF(CMD_WRITE_EXP);
                messageOut.send();
            } catch (IOException ie) {
                Log2.error("[FSListener] [ReceiveFile():messageOut  "
                        +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            }
        }
    }

    private void rename(String path) {
        int idx = path.lastIndexOf(".");
        String newFileName = path.substring(0, idx - 1) + "C.SCD";
        File oldFile = new File(path);

        while (!oldFile.canWrite()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
        File newFile = new File(newFileName);
        if (oldFile.exists()) {
            if (oldFile.length() == 0) {
                oldFile.delete();
            } else {
                oldFile.renameTo(newFile);
                Log2.debug("[FSListener] [Received SCDFile Name" + newFileName+"]", 4);
            }
        }
    }
}
