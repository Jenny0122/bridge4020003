package kr.co.wisenut.common.filter.custom;

import SCSL.SLBsUtil;
import SCSL.SLDsFile;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.FilterThread;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

import java.io.File;

public class SoftCampFilterData extends Filter {
    private StringBuffer sbData = new StringBuffer(8192);
    private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
    /**
     * FilterData�쓽 Constructor濡쒖뜥 FilterDelete瑜� �씤�옄濡� 諛쏅뒗�떎.
     * @param filterDel
     */
    public SoftCampFilterData(Boolean filterDel) {
        super(filterDel);
    }
    
    public void setFilteredTextDir(String filteredTextDir) {
    	super.filteredTextDir = filteredTextDir;
    	
    }
    public String getFilterData
    (String[][] sourceFileInfo, FilterSource filter ,String charset) {
    	String preFixDir = filter.getDir();
    	String retrieve = filter.getRetrival();
    	String condition = filter.getCondition();
    	String jungumKey = filter.getJungumKey();
    	String split = filter.getSplit();
    	String filterType = filter.getFilterType();
    	
    	debug(sourceFileInfo, preFixDir, retrieve, condition, filterType, jungumKey);
        String[] srcFile = null;
        String targetFile = "";
        //String filterDir = FileUtil.lastSeparator(m_wcse_home) + "Filter" + FileUtil.getFileSeperator();
        String filterDir = filteredTextDir;
        FileUtil.makeDir(filterDir);

        int sourceLen = sourceFileInfo.length;
        for(int i=0; i < sourceLen; i++) {
            String chk_ext_condition = condition;
            if( sourceFileInfo[i][0] != null
                    && (sourceFileInfo[i].length > 1 || sourceFileInfo[i][0].lastIndexOf(".") == -1)) {
                chk_ext_condition = "none-chk-ext";
            }

            if(retrieve.equals("blob") && condition.equals("source-delete")) {
                chk_ext_condition = "none-chk-ext";
            }
            if(sourceFileInfo[i].length > 1 && !FileUtil.isFiltered(sourceFileInfo[i][1])) {
                sbData.append(" ");
                Log2.debug("[FilterData ] [None Filtered File Ext FileName or Ext Name," +
                        " sourceFileInfo[i][1] is FileName or FileExt" + sourceFileInfo[i][1] + "]", 3);
            }else {
                // attach1 || split|| attach2 AS ATTACH  processing
                if(split.equals("")){
                    srcFile = new String[]{sourceFileInfo[i][0]};
                }else{
                    srcFile = StringUtil.split(sourceFileInfo[i][0], split);
                }
                int size = srcFile.length;
                int fieldNumber = 0;
                if(sourceLen > 1) {
                    fieldNumber = sourceLen;
                }
                if( charset.toLowerCase().equals("utf-8") ) {
                    MAX_FILTERED_LIMIT_SIZE = MAX_FILTERED_LIMIT_SIZE * 3 ;
                }
                int maxLen = MAX_FILTERED_LIMIT_SIZE / (fieldNumber + size);

                if(size > 0){
                    for(int n=0; n < size; n++) {
                        File srcFileTmp;
                        targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
                        if(srcFile[n] == null || srcFile[n].equals(""))  {
                            continue;
                        }

                        if(!preFixDir.equals("")) {
                            srcFileTmp = new File(preFixDir, srcFile[n]);
                        } else {
                            srcFileTmp = new File(srcFile[n]);
                        }
                        if( filteringFile(srcFileTmp.getPath(), targetFile, chk_ext_condition, filterType, jungumKey, charset) ){
                            if(!charset.toLowerCase().equals("utf-8")){
                                sbData.append( readTextFile(targetFile, maxLen)).append(" ");
                            }else{
                                sbData.append( readTextFile(targetFile, maxLen,"UTF-16")).append(" ");
                            }
                        }
                    }
                }
            }

            if(condition.equals("source-delete")) {
                int size = srcFile.length;
                if (size > 0) {
                    for (int n = 0; n < size; n++) {
                        // blob �뜲�씠��媛� 鍮꾩뼱�벝 寃쎌슦 null 泥섎━. by jwlee 20091127
                        if (srcFile[n] != null) {
                            File file = null;
                            if(!preFixDir.equals("")) {
                                file = new File(preFixDir,srcFile[n]);
                            }else{
                                file = new File(srcFile[n]);
                            }

                            FileUtil.delete(file);
                        }
                    }
                }
            }
        }
        String sbString = sbData.toString();
        sbData.setLength(0);
        return sbString;
    }


    public static void main(String[] args) {
        System.out.println( "usage : className=\"kr.co.wisenut.bridge3.job.custom.SoftCampFilterDataData\"");
        if( args.length > 0 ) {
            SoftCampFilterData filter = new SoftCampFilterData(Boolean.FALSE);
            String[][] sourceFileInfo = new String[][] {{"20110325.docx;20110401.docx"}};
            String prefix = "d:\\0_standard\\二쇨컙蹂닿퀬\\";
            String retrive = "";
            String condition = "";
            String filters = "";
            String split = ";";
            //String data = filter.getFilterData(sourceFileInfo,prefix,retrive,condition,filters,split,"utf-8");
//            /System.out.println( "output : " + data);
        }
    }
}
