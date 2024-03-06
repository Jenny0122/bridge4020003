package kr.co.wisenut.common.util.lang;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 4. 19
 * Time: �삤�썑 1:35:15
 * To change this template use File | Settings | File Templates.
 */
public class ByteToString {

    /**
     * 諛붿씠�듃�떒�쐞瑜� �쟻�젅�븳 理쒕��떒�쐞源뚯� �뿰�궛�븯�뿬 �옄由ъ닔瑜� 以꾨젮�꽌 臾몄옄�뿴濡� �룎�젮以��떎.
     */
    static public String byteToString(long bnum){
        String result=bnum+" Byte";
        try {
            long mod=0;
            if(bnum>(1024*1024*1024)) {
                mod=1024*1024*1024;
                result= ""+bnum/mod+"."+bnum%mod+" GB";
            }else  if(bnum>(1024*1024)) {
                mod=1024*1024;
                result= ""+bnum/mod+"."+bnum%mod+" MB";
            } else  if(bnum>(1024)) {
                mod=1024;
                result= ""+bnum/mod+"."+bnum%mod+" KB";
            }
        }catch(Exception e){}
        return result;
    }

}