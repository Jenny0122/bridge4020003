package kr.co.wisenut.common.util.io;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 4. 21
 * Time: �삤�썑 9:43:05
 *
 * �뙆�씪紐낆씠 吏��젙�븳 exes 洹몃９�쑝濡� �걹�굹�뒗吏� 寃��궗�븯�뒗 FilenameFilter �겢�옒�뒪瑜� 援ы쁽
 *
 */
public class FilenameFilterWithEndWithExes implements FilenameFilter {
    String [] exes =null;
    public FilenameFilterWithEndWithExes(String[] exes){
        this.exes = exes;
    }

    public FilenameFilterWithEndWithExes(String exe){
        this.exes = new String[]{exe};
    }

    public boolean accept(File dir, String name){
        boolean flag = false;
        boolean onlyFileType = true;
        if(this.exes ==null ||  this.exes.length <= 0 ){
            return true;
        }
        int findex = name.lastIndexOf(".");
        if( findex > -1 )  {
            String fexe = name.substring(findex +1 );
                 
            int sz = exes==null?0:exes.length;
            for(int i=0;i<sz;i++){// �젙�쓽�맂 exes 以� �븯�굹�� �씪移섑븯�뒗吏� 寃��궗
                String compare = exes[i];
                boolean match = fexe.equalsIgnoreCase(compare) ;
                if(!match)
                    continue;
                if( !onlyFileType ) { flag = true;break; }
                else if(onlyFileType) {
                    if( (new File(dir.getAbsolutePath(), name)).isFile() ){// file/dir ���엯以�, file �씤吏� 寃��궗
                        flag = true;
                        break;
                    }
                }
            }// for
        }

        return flag;
    }
}