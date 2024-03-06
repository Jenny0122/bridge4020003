package kr.co.wisenut.common.scdreceiver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.msg.BridgeInfoMsg;
import kr.co.wisenut.common.util.BridgeCommonUtil;
import kr.co.wisenut.common.util.ExistCodeConstants;
import kr.co.wisenut.common.util.PidUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 6. 14
 * To change this template use File | Settings | File Templates.
 */
public class SCDReceiver extends Thread {
    private int port;
    private ServerSocket server = null;
    // Create PidUtil Object
    private static PidUtil pidUtil = null;
    private static boolean START = false;
    private static boolean STOP = false;

    public SCDReceiver(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
    }

    public void run() {
        Log2.out("[SCDReceiver] [SCDReceiver Starting port("+port+") ]");
        while( !Thread.interrupted() ) {
            try {
                Socket socket = server.accept();
                new FSListener(socket).start();
                Log2.debug("[SCDReceiver] [SCDReceiver accept clinet" +
                        "("+socket.getInetAddress().toString()+")]", 3);
            } catch (Exception e) {
                Log2.error("[SCDReceiver] [Run()  "
                        +"\n"+ IOUtil.StackTraceToString(e)+"\n]");
            }
        }
        pidUtil.deletePID();	// Normal Exit PidUtil Objecta
    }

    public static void main(String[] args) {
        //Message.hdServer();

        int port = -1;
        int debug = 2;
        String logPath = null;
        String log = "week";
        
        String SF1_HOME = null;
        if(System.getProperty("wcse_home") != null) {
        	SF1_HOME = System.getProperty("wcse_home");
        } else if(System.getProperty("sf1_home") != null) {
        	SF1_HOME = System.getProperty("sf1_home");
        }
        
        if(SF1_HOME == null) {
            System.out.println("[Error] [Missing sf1_home or wcse_home setting in JVM environment");
            System.exit(-1);
        }

        for(int i=0; i < args.length; i++) {
            if(args[i].equals("-port")) {
                if(args[i+1] != null) {
                    try {
                        port = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        System.out.println("Please input -port number");
                    }
                }
            } else if(args[i].equals("-log")) {
                if(args[i+1] != null) {
                    if(args[i+1].equals("day")) {
                        log = "day";
                    } else if(args[i+1].equals("stdout")) {
                        log = "stdout";
                    }
                }
            } else if(args[i].equals("-debug")) {
                if(args[i+1] != null) {
                    try {
                        debug = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        System.out.println("Please input -debug 1~4");
                    }
                }
            } else if (args[i].toLowerCase().equals("-logpath")) {
                if(i+1 < args.length) {
                    if ( !args[i+1].startsWith("-") ) {
                        try{
                            logPath = args[i+1];
                        }catch(Exception e){}
                        i++;
                    }
                }
            } else if(args[i].toLowerCase().equalsIgnoreCase("-start")) {
                START = true;
            } else if(args[i].toLowerCase().equalsIgnoreCase("-stop")) {
                STOP = true;
            } else if(args[i].equals("-help") || args[i].equals("-?")) {
                usage();
                System.exit(0);
            }
        }

        if(port == -1) {
            System.out.println("\n\nPlease input -port <port number>");
            usage();
            System.exit(0);
        }

        boolean verbose = false;
        if(debug > 3) {
            verbose = true;
        }
        
        try {
            if( logPath != null) {
                Log2.setLogger(logPath, log, verbose, debug, verbose,  "SCDReceiver");
            }else{
                Log2.setBridgeLogger(SF1_HOME, log, verbose, debug, verbose,  "SCDReceiver");
            }
		} catch (Exception e) {
			System.out.println("[SCDReceiver] [Set Logger fail. "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
			System.exit(-1);
		}


        // System Exit Code
        int exit_code = ExistCodeConstants.EXIST_CODE_NORMAL;

        if(START) {
            try {
                new SCDReceiver(port).start();
                pidUtil = new PidUtil("SCDReceiver", SF1_HOME);
                try {
                    pidUtil.makePID();
                } catch (IOException e) {
                    Log2.error("[SCDReceiver] [Make PID"+"\n"+IOUtil.StackTraceToString(e)+"\n]");
                }
            } catch (IOException e) {
                Log2.error("[SCDReceiver] [SCDReceiver Starting Error!! "
                        +"\n"+IOUtil.StackTraceToString(e)+"\n]");
                if( pidUtil != null)
                	pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
                exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
                System.exit(exit_code);
            }
        }else if(STOP){
            try {
                FClient client = new FClient("127.0.0.1", port);
                client.command("CMD:SHUTDOWN");
            } catch (IOException e) {
                Log2.error("[SCDReceiver] [Server already  Stop"+"\n"+IOUtil.StackTraceToString(e)+"\n]");
            }
        }else{
            System.exit(-1);
        }

    }

    private static void usage() {
        System.out.println("Usage)java -Dsf1_home=$SF1_HOME or -Dwcse_home=$WCSE_HOME" +
                " kr.co.wisenut.common.scdreceiver.SCDReceiver -port <number> -start | -stop [option]");
        System.out.println("option\t-log <day|stdout>");
        System.out.println("      \t-debug <1~4>");
    }
}
