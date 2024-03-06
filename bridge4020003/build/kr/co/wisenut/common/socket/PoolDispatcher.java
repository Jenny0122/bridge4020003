package kr.co.wisenut.common.socket;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;

import java.net.*;
import java.io.*;

public class PoolDispatcher implements Dispatcher {
    /**
     *
     */
    private String NUMTHREADS = "1";        //  thread pool 媛쒖닔
    /**
     *
     */
    private final String THREADPROP = "FilterThreads";  // thread property �씠由�
    /**
     *
     */
    private int numThreads = 1;           // �깮�꽦�븳 ���뿉�꽌 �벐�젅�뱶�쓽 媛쒖닔

    /**
     *
     */
    public PoolDispatcher() {
        if(System.getProperty("threadno") != null) {
            NUMTHREADS = System.getProperty("threadno");
        } else {
            Log2.out("[PoolDispatcher] [threadno not set : Default -Dthreadno=1]");
        }
        numThreads = Integer.parseInt(System.getProperty(THREADPROP, NUMTHREADS));
    }

    /**
     *
     * @param servSock
     * @param protoFactory
     */
    public void startDispatching(final ServerSocket servSock, final ProtocolFactory protoFactory) {
        // �벐�젅�뱶瑜� N媛쒖뵫 �깮�꽦�븳�떎.
        for (int i = 0; i < (numThreads - 1); i++) {
            Thread thread = new Thread() {
                public void run() {
                    dispatchLoop(servSock, protoFactory);
                }
            };
            thread.start();
            Log2.debug("Created and started Thread = " + thread.getName() , 4);
        }
        Log2.debug("[Iterative server starting in main thread : " + Thread.currentThread().getName() + "]", 4);
        // N 踰덉㎏ �벐�젅�뱶瑜� �궗�슜�븯�옄
        dispatchLoop(servSock, protoFactory);
    }

    /**
     *
     * @param servSock
     * @param protoFactory
     */
    private void dispatchLoop(ServerSocket servSock, ProtocolFactory protoFactory) {
        // Loop 瑜� �룎由곕떦~
        while(true){
            try {
                Socket clntSock = servSock.accept();
                Runnable protocol = protoFactory.createProtocol(clntSock);
                protocol.run();
            } catch (Exception e) {
                Log2.error("[PoolDispatcher] [Exception = " 
                                +IOUtil.StackTraceToString(e)+"\n]");
            }
        }
    }
}
