/*
* @(#)PidUtil.java   3.8.1 2009/03/11
*
*/
package kr.co.wisenut.common.util;

import kr.co.wisenut.common.util.dynrun.DynRun;

import java.io.*;

/**
 *
 * PidUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class PidUtil {
    private String m_srcid;
    private String m_pid_path;

    private String DEF_PID_VAL_INIT="0";
    private String DEF_PID_VAL_ERROR="-1";

    public PidUtil(String srcid, String sf1_home) {
        m_srcid = srcid;
        sf1_home = sf1_home.trim();
        String baseDir = sf1_home;
        if( !sf1_home.endsWith(FileUtil.fileseperator) ){
            baseDir = sf1_home + FileUtil.fileseperator;
        }
        m_pid_path = baseDir + "pid" + FileUtil.fileseperator;
        FileUtil.makeDir(m_pid_path);
    }

    public void makePID() throws IOException {
        makePID(getName());
    }

    public String getPID() throws IOException{
        String pid = null;
        try {
            boolean isWindow = System.getProperty("os.name").startsWith("Windows");
            if((pid = getPIDByDynamicClassRun())==null){ // JAVA 濡� PID �뼸湲�
                if(isWindow)  pid = ""+GetPidUtil.getPid();   // window �씪硫� dll�씪�씠釉뚮윭由щ줈 PID �뼸湲�
                else          pid = getPidByPerScript();      // �럡�뒪�겕由쏀듃濡� PID �뼸湲�.
            }
        }catch(Exception e){
            System.out.println("[error] [Make PID] ["+e.getMessage()+"]");
        }
        if(pid==null)
            throw new IOException("Fail getPID. pid is null");
        return pid;

    }

    public void makePID(String name) throws IOException {
        String pid=null;
        try { pid = getPID();}catch(Exception e){
            pid = DEF_PID_VAL_INIT;
            System.out.println("[error] [Make PID] ["+e.getMessage()+"]");
        }

        // �뙆�씪 �옉�꽦
        writePID(getFile(name), pid);
    }

    public void deletePID() { deletePID( getName() ); }

    public void deletePID(String name) {  FileUtil.delete(getFile(name)); }

    /**
     * �뿉�윭�슜 PID �뙆�씪�쓣 �궓寃⑤몦�떎.
     */
    public void leaveErrorPID() { leaveErrorPID(getName()); }

    /**
     * �뿉�윭�슜 PID �뙆�씪�쓣 �궓寃⑤몦�떎.
     * @param name 釉뚮┸吏��슜 pid �뙆�씪紐� �쟾移섏궗
     */
    public void leaveErrorPID(String name) {
        try {
            writePID( getFile(name), DEF_PID_VAL_ERROR);
        } catch(IOException e){
            System.out.println("[error] [Make PID] [PID of Process is make error PID]");
        }
    }

    public boolean existsPidFile() throws IOException{
        boolean isRet = false;
        //pid�뙆�씪�씠 議댁옱�븯�뒗吏� 泥댄겕�븳�떎.
        //�뙆�씪�씠 議댁옱�븷 寃쎌슦 pid �젙蹂대�� �씫�뒗�떎.
        //pid媛� -1�씤 寃쎌슦 �떎�뙣�븳 寃쎌슦�씠誘�濡� pid�뙆�씪�씠 議댁옱�븯吏� �븡�뒗�떎�뒗 寃곌낵瑜� 諛섑솚�븳�떎.
        //pid媛� -1�씠 �븘�땶 寃쎌슦 windows 2000�씠硫� tlist濡� �빐�떦 pid瑜� 泥댄겕�븯怨� windows 2000�씠�긽�씠硫� tasklist濡� 泥댄겕�븳�떎.
        if(getFile(getName()).exists()) {
            String pid = FileUtil.readFile((getFile(getName())));
            if(!pid.equals("-1")) {
                String winOsName = System.getProperty("os.name");
                boolean isWindow = winOsName.startsWith("Windows");
                if(isWindow) {  //windows
                    if(winOsName.indexOf("2000") > -1){  //greater than windows 2000
                        if(hasBridgeProcess(pid, "tlist")) {
                            isRet = true;
                        }
                    }else{  //windows xp, windows windows 2003, windows 2008, etc ...
                        if(hasBridgeProcess(pid, "tasklist")) {
                            isRet = true;
                        }
                    }
                } else { //linux & unix
                    if(hasBridgeProcess(pid, "ps -p " + pid)) {
                        isRet = true;
                    }
                }
            }
        }
        return isRet;
    }

    /**
     * birdge �봽濡쒖꽭�뒪媛� �떎�뻾以묒씤吏� 泥댄겕�븯�뒗 硫붿냼�뱶
     * @param pid  bridge process id
     * @param checkCMD check program
     * @return true/false.
     * @throws IOException runtime exception
     */
    private boolean hasBridgeProcess(String pid, String checkCMD) throws IOException{
        boolean isRet = false;
        Process p_start = Runtime.getRuntime().exec(checkCMD);
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p_start.getInputStream()));
        String output;
        while ( (output = stdout.readLine()) != null) {
            if(output.indexOf(pid) > -1 && ( output.startsWith("java") ||  output.indexOf("java") > -1)) {
                isRet = true;
                break;
            }
        }
        p_start.destroy();
        return isRet;
    }

    private File getFile(String name){
        return new File( m_pid_path + name+ ".pid" );
    }

    private String getName(){
        return "bridge-" + m_srcid;
    }

    private void writePID(File file, String pid) throws IOException {
        System.out.println("[info] [Make PID] [PID of Process is " + pid + "]");
        FileWriter fw = new FileWriter(file);
        fw.write(pid);
        fw.close();
    }

    /**
     * jvm 踰꾩쟾�씠 1.5 �씠�긽�씠�씪硫� true 瑜� 由ы꽩�븳�떎.
     */
    private boolean isJdk15Up(){
        String version = System.getProperty("java.specification.version");
        float nVer = Float.parseFloat(version);
        return nVer >= 1.5;
    }

    /**
     * �쐢�룄�슦媛� �븘�땶 os �씪硫� -  �럡�뒪�겕由쏀듃瑜� �씠�슜�빐�꽌 PID 瑜� 由ы꽩�븳�떎.
     */
    private String getPidByPerScript() throws IOException {
        boolean isWindow = System.getProperty("os.name").startsWith("Windows");
        String pid = null;

        if ( isWindow )
            return pid;

        try {
            String[] cmd = new String[]{"perl", "-e", "print getppid(). \"\n\";"};
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            pid = br.readLine();
        }catch(IOException e){
            throw e;
        }

        return pid;
    }

    /**
     * jvm1.5 �씠�긽�씠�씪硫� - java management �뙣�궎吏�瑜� �씠�슜�빐�꽌 PID 瑜� 由ы꽩�븳�떎.
     * wise.util.jvm15-1.0.0.jar 媛� �븘�슂�븳�떎.
     * @throws Exception 留ㅼ냼�뱶 �떎�뻾 �떎�뙣�떆 �삁�쇅 諛쒖깮
     */
    private String getPIDByDynamicClassRun() throws Exception {
        String pid = null;

        if( !isJdk15Up() )
            return null;

        String classNameString = "com.wisenut.util.getpid.GetpidManagement";
        String methodNameString = "getPID";
        try {
            pid = ""+DynRun.run(classNameString, methodNameString);
        }catch(Exception e){
            throw new Exception( "Fail DynRun. Class:"+classNameString+", method:"+ methodNameString+", Message:"+e.getMessage());
        }
        return pid;
    }
}
