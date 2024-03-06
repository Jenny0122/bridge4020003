package kr.co.wisenut.common.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 6. 14
 * Time: 오후 3:41:57
 * To change this template use File | Settings | File Templates.
 */
public class ExtFileNameFilter implements FilenameFilter {
    private String[] m_str;

        public ExtFileNameFilter(String[] str) {
            m_str = str;
        }

        public ExtFileNameFilter(String str){
            m_str = new String[1];
            m_str[0] = str;
        }

        public boolean accept(File dir, String name) {
            if( new File(dir, name).isDirectory()) {
                return false;
            }
            if(m_str != null) {
                name = name.toLowerCase();
                for(int i=0 ; i < m_str.length; i++) {
                    if(name.endsWith(m_str[i])) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }

}