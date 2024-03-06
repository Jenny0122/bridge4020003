package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.bridge3.config.source.MemorySelect;
import kr.co.wisenut.bridge3.config.source.SubQuery;
import kr.co.wisenut.common.Exception.CustomException;

public class TestSubCustom implements ISubCustom
{
    public String customData( String str, SubQuery subQuery ) throws CustomException
    {
        String sep = subQuery.getSeperator();
        
        String[] strs = str.split( "\\" + sep );
        
        String customValue = "";
        
        for( int idx = 0; idx < strs.length; idx++ )
        {
            customValue += strs[idx];
            customValue += sep;
        }
        
        return customValue;
    }

    public String customData( String str, MemorySelect subMemory ) throws CustomException
    {
        return null;
    }

}
