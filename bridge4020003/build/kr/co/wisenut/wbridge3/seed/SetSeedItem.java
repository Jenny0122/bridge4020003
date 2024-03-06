package kr.co.wisenut.wbridge3.seed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.wbridge3.config.source.Source;
import kr.co.wisenut.wbridge3.url.ParameterSort;

public class SetSeedItem {
    public SeedItem insertItemBySeed(Source m_source, SeedInfo seedinfo, int sourceCount, String seed_file) {

        SeedItem insertItem = new SeedItem();
        try {
            //load seed file
            File chkFile = new File(seed_file);
            if(!chkFile.exists()) {
                Log2.error("[JOB] [Unable to load the seed file. (" + seed_file +")]");
                return null;
            }
            FileInputStream fis = new FileInputStream(seed_file);
            BufferedReader br ;
            if(m_source.getScdCharSet().toLowerCase().equals("utf-8")){
                br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
            }else{
                br = new BufferedReader(new InputStreamReader(fis));
            }
            Log2.out("[info] [Job] [Set WebBridge seed file info]");
            String uniqueSource = "";
            String  seedurl = "";
            while ((seedurl = br.readLine()) != null) {
                seedurl = seedurl.trim();
                if (seedurl.startsWith("#") || seedurl.length() == 0) {
                    continue;
                }else if (seedurl.startsWith(SeedInfo.SOURCE)) {
                    if (insertItem.getSource().length() > 0) {
                        insertItem.setNth(sourceCount);
                        //unique source file name
                        uniqueSource = insertItem.getSource() + "|" + StringUtil.getTimeBasedUniqueID();
                        if (seedinfo.put(uniqueSource, insertItem)) {
                            sourceCount++;
                            insertItem = new SeedItem();
                        }
                    }
                    insertItem.setSource(seedurl.substring(SeedInfo.SOURCE.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.HOMEURL)) {
                    insertItem.setHomeurl(seedurl.substring(SeedInfo.HOMEURL.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.ALLOWURL)) {
                    String strAllow =  seedurl.substring(SeedInfo.ALLOWURL.length(), seedurl.length()) ;
                    if( !strAllow.equals("")){
                        insertItem.addWriteAllowElement(strAllow);
                    }
                }else if ( seedurl.startsWith(SeedInfo.ONLYVIEWURL))   {
                    String strView = seedurl.substring(SeedInfo.ONLYVIEWURL.length(), seedurl.length());
                    if ( !strView.equals("")) {
                        insertItem.addOnlyViewElement(strView);
                    }
                }else if (seedurl.startsWith(SeedInfo.SKIPURL)) {
                    insertItem.addElement(seedurl.substring(SeedInfo.SKIPURL.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.MAXDEPTH)) {
                    insertItem.setMaxDepth(Integer.parseInt(seedurl.substring(SeedInfo.MAXDEPTH.length(), seedurl.length())));
                }else if (seedurl.startsWith(SeedInfo.MAXURL)) {
                    insertItem.setMaxUrl(Integer.parseInt(seedurl.substring(SeedInfo.MAXURL.length(), seedurl.length())));
                }else if (seedurl.startsWith(SeedInfo.SKIPMENU)) {
                    insertItem.addSkipmenu(seedurl.substring(SeedInfo.SKIPMENU.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.TAILMENU)) {
                    insertItem.addTailmenu(seedurl.substring(SeedInfo.TAILMENU.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.TAILSRC)) {
                    insertItem.setTailsrc(seedurl.substring(SeedInfo.TAILSRC.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.URL)) {
                    insertItem.addUrlElement(ParameterSort.sortUrlParameter(seedurl.substring(SeedInfo.URL.length(), seedurl.length())));
                }else if (seedurl.startsWith(SeedInfo.USERDEFINE)) {
                    insertItem.addUsertag(seedurl.substring(SeedInfo.USERDEFINE.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.SECTION)) {
                    insertItem.setSection(seedurl.substring(SeedInfo.SECTION.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.SECTION1)) {
                	insertItem.setSection1(seedurl.substring(SeedInfo.SECTION1.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.SECTION2)) {
                	insertItem.setSection2(seedurl.substring(SeedInfo.SECTION2.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.SECTION3)) {
                	insertItem.setSection3(seedurl.substring(SeedInfo.SECTION3.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.CUSTOMJS)) {
                    insertItem.setCustomJS(seedurl.substring(SeedInfo.CUSTOMJS.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.LOCALE)) {
                    insertItem.setLocale(seedurl.substring(SeedInfo.LOCALE.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.CHARSET)) {
                    insertItem.setCharset(seedurl.substring(SeedInfo.CHARSET.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.DENYID)) {
                    insertItem.setDenyId(StringUtil.split(seedurl.substring(SeedInfo.DENYID.length(), seedurl.length()), ","));
                }else if (seedurl.startsWith(SeedInfo.DENYCLASS)) {
                    insertItem.setDenyClass(StringUtil.split(seedurl.substring(SeedInfo.DENYCLASS.length(), seedurl.length()), ","));
                }else if (seedurl.startsWith(SeedInfo.ISLIST)) {
                    if(seedurl.substring(SeedInfo.ISLIST.length(), seedurl.length()).equalsIgnoreCase("y")) {
                        insertItem.setList(true);
                    }
                }else if (seedurl.startsWith(SeedInfo.COLLECT_DIVID)) {
                	insertItem.setCollectDivIds(StringUtil.split(seedurl.substring(SeedInfo.COLLECT_DIVID.length(), seedurl.length()), ","));
                }else if (seedurl.startsWith(SeedInfo.COLLECT_CLASS)) {
                	insertItem.setCollectClasses(StringUtil.split(seedurl.substring(SeedInfo.COLLECT_CLASS.length(), seedurl.length()), ","));
                }else if (seedurl.startsWith(SeedInfo.BASICAUTHID)) {
                	insertItem.setBasicAuthID(seedurl.substring(SeedInfo.BASICAUTHID.length(), seedurl.length()));
                }else if (seedurl.startsWith(SeedInfo.BASICAUTHPW)) {
                	insertItem.setBasicAuthPW(seedurl.substring(SeedInfo.BASICAUTHPW.length(), seedurl.length()));
                }
            }   // end while read seedurl
        }catch(IOException e){
            Log2.error("[JOB Crawl Exception: ] [ "
                    +"\n"+ IOUtil.StackTraceToString(e)+"]");
            return null;
        }
        return insertItem;
    }
}
