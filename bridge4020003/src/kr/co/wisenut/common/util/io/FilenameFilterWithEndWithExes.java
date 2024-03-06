package kr.co.wisenut.common.util.io;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 4. 21
 * Time: 오후 9:43:05
 *
 * 파일명이 지정한 exes 그룹으로 끝나는지 검사하는 FilenameFilter 클래스를 구현
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
            for(int i=0;i<sz;i++){// 정의된 exes 중 하나와 일치하는지 검사
                String compare = exes[i];
                boolean match = fexe.equalsIgnoreCase(compare) ;
                if(!match)
                    continue;
                if( !onlyFileType ) { flag = true;break; }
                else if(onlyFileType) {
                    if( (new File(dir.getAbsolutePath(), name)).isFile() ){// file/dir 타입중, file 인지 검사
                        flag = true;
                        break;
                    }
                }
            }// for
        }

        return flag;
    }
}