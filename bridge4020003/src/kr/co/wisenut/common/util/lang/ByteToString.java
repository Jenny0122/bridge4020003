package kr.co.wisenut.common.util.lang;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 4. 19
 * Time: 오후 1:35:15
 * To change this template use File | Settings | File Templates.
 */
public class ByteToString {

    /**
     * 바이트단위를 적절한 최대단위까지 연산하여 자리수를 줄려서 문자열로 돌려준다.
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