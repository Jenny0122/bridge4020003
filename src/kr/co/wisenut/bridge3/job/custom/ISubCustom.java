package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.bridge3.config.source.MemorySelect;
import kr.co.wisenut.bridge3.config.source.SubQuery;
import kr.co.wisenut.common.Exception.CustomException;

public interface ISubCustom
{
    public String customData( String str, SubQuery subQuery ) throws CustomException;
    public String customData( String str, MemorySelect subMemory ) throws CustomException;
}