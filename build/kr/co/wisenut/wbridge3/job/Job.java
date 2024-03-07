/*
 * @(#)Job.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.wbridge3.job;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.common.filter.FilterData;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.scdreceiver.SCDTransmit;
import kr.co.wisenut.common.util.DateUtil;
import kr.co.wisenut.common.util.EncryptUtil;
import kr.co.wisenut.common.util.FileManager;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.HtmlUtil;
import kr.co.wisenut.common.util.SCDManager;
import kr.co.wisenut.common.util.JSONManager;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.wbridge3.bdb.RichURL;
import kr.co.wisenut.wbridge3.bdb.URLDB;
import kr.co.wisenut.wbridge3.config.Config;
import kr.co.wisenut.wbridge3.config.catalogInfo.InfoSet;
import kr.co.wisenut.wbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.wbridge3.config.source.Source;
import kr.co.wisenut.wbridge3.html.HtmlDocument;
import kr.co.wisenut.wbridge3.html.PlainText;
import kr.co.wisenut.wbridge3.job.idn.IDNA;
import kr.co.wisenut.wbridge3.job.idn.IDNAException;
import kr.co.wisenut.wbridge3.seed.SeedInfo;
import kr.co.wisenut.wbridge3.seed.SeedItem;
import kr.co.wisenut.wbridge3.seed.SetSeedItem;
import kr.co.wisenut.wbridge3.url.DuplicateMenu;
import kr.co.wisenut.wbridge3.url.ParameterSort;

import com.sun.jimi.core.JimiException;

/**
 * 
 * Job
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */

public class Job {
	private SeedInfo seedinfo;
	private SeedItem curItem;
	private URLDB urldb;
	private Source m_source;
	private FilterData filter = null;
	private Mapping mapping;
	private static final int INIT = 0;
	private static final int STATIC = 1;
	private static final int TEST = 2;
	private String wcse_home = "";
	private int bodylength = 0;
	private int totalCount = 0;
	private int writeCount = 0;
	private int sourceCount = 0;
	private boolean verb = false;
	private boolean isFilterDel = false;
	private boolean isTest = false;
	private String DOCUMENT_CHARSET = "utf-8";
	private String USER_CHARSET = "utf-8";
	private int m_mode = -1;
	private int maxDepthCnt = 0;
	private int maxDepth = 0;
	private String srcId;
	private boolean notExtractURL = false;
	private boolean isIndexDir;
	private String scdRcvMode = "all";

	private FileManager fileManager;
	protected LinkedHashMap<String, String> data = new LinkedHashMap<>();

	/**
	 * Job Class Constructor.
	 * 
	 * @param whome
	 *            WCSE_HOME
	 * @param config
	 *            Config Class
	 * @param mmode
	 *            RunTime Mode
	 * @param verbose
	 *            isVerbose?
	 * @param filterdel
	 *            isFiltered File Delete?
	 */
	public Job(String whome, Config config, int mmode, boolean verbose, boolean filterdel) {
		this.wcse_home = whome;
		this.m_source = config.getSource();
		this.mapping = config.getCollection();
		this.srcId = config.getSrcid();
		this.verb = verbose;
		this.isFilterDel = filterdel;

		File indexDir;
		if (config.getArgs().isUserIndexDir()) {
			indexDir = new File(m_source.getScdDir().getPath(), "index");
		} else if (mmode == STATIC) {
			indexDir = new File(m_source.getScdDir().getPath(), "static");
		} else {
			indexDir = new File(m_source.getScdDir().getPath());
		}

		this.scdRcvMode = config.getArgs().getScdRcvMode();
		// set default for json
		this.fileManager = new JSONManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());

		if (m_source.getExtension().equalsIgnoreCase("scd")) {
			this.fileManager = new SCDManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());
		}

		this.m_mode = mmode;

		String filteredTextDir = FileUtil.lastSeparator(config.getArgs().getSf1_home()) + "Filter"
				+ FileUtil.getFileSeperator() + config.getSrcid() + FileUtil.getFileSeperator();
		this.filter = new FilterData(new Boolean(this.isFilterDel));
		this.filter.setFilteredTextDir(filteredTextDir);
		this.isIndexDir = config.getArgs().isUserIndexDir();
	}

	/**
	 * WiseCrawler Main Process Function
	 * 
	 * @return true/false : Job Success or Fail
	 */
	public boolean runCrawl() {
		System.setProperty("java.awt.headless", "true");
		// add handler for SSL
		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

		Log2.out("[info] [Job] [WebBridge Process: Run]");
		String urldbPath = m_source.getDbFile();
		String seed_file = m_source.getSeedfile();
		curItem = new SeedItem();
		seedinfo = new SeedInfo();
		urldb = new URLDB();

		FileUtil.makeDir(urldbPath);
		urldb.init(urldbPath, true); // URL DB Initialize
		try {
			fileManager.insert(new LinkedHashMap<String, String>()); // SCD File Data Initialize
		} catch (IOException e) {
			Log2.error("[Bridge][SCD Init I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "]");
			return false;
		}

		if (m_mode == INIT) {
			boolean isSucess = false;

			Log2.out("[info] [Job Init] [Delete SCD File]");

			if (fileManager.delete()) {
				Log2.debug("[Job Init] [Successful]");
				isSucess = true;
			} else {
				Log2.debug("[Job Init] [Failure]");
			}

			return isSucess;
		}

		try {
			int startSource = 0;
			FileUtil.makeDir(wcse_home + FileUtil.getFileSeperator() + "Filter" + FileUtil.getFileSeperator());

			Log2.out("[info] [Job] [URL DB environment init completed]");

			// init item to seed info
			SeedItem insertItem = new SetSeedItem().insertItemBySeed(m_source, seedinfo, sourceCount, seed_file);

			insertItem.setNth(sourceCount);
			seedinfo.put(insertItem.getSource(), insertItem);

			sourceCount = seedinfo.size();

			// get seed info (currItem�뿉 assign �븳�떎.)
			getSeedData(startSource);
			System.out.print("[" + totalCount + "]");

			if (!curItem.getLocale().equals("")) {
				setLocale(curItem.getLocale());
			}

			int maxCount;
			if (m_mode == TEST) {
				maxCount = 10;
				isTest = true;
			} else {
				// maxCount = curItem.getMaxUrl() ;
				maxCount = 0;
			}

			// start crawling ...
			// dataCrawlByItemInfo(startSource, maxCount, urldbPath, insertItem);
			dataCrawlByItemInfo(startSource, maxCount, urldbPath);

			Log2.out("\n[info] [Job][crawling total count : " + totalCount + "]");
		} catch (Exception e) {
			Log2.error("[Bridge][exception: " + "\n" + IOUtil.StackTraceToString(e) + "]");
			return false;
		} finally {
			try {
				urldb.deinit();
				fileManager.close(isTest);
				deleteThumbNail();
			} catch (IOException e) {
				Log2.error("[Bridge][SCD close I/O exception: " + "\n" + IOUtil.StackTraceToString(e) + "]");
			}

			if (m_source.getRemoteinfo().getIp() != null) {
				// scd receiver 濡� scd �쟾�넚
				try {
					SCDTransmit trans = new SCDTransmit(m_source.getRemoteinfo().getIp(), m_source.getRemoteinfo().getPort(), scdRcvMode);
					Log2.debug("[Job ] [SCD File TransMit" + m_source.getRemoteinfo().getIp() + "]", 3);
					if (isIndexDir) {
						trans.sendSCD(
							m_source.getScdDir() + "/index",
							m_source.getRemoteDir() + "/index",
							new File(m_source.getScdDir().getPath(), "backup").getPath(),
							m_source.getRemoteinfo().isDeleteSCD()
						); // setting path..
					} else if (m_mode == STATIC) {
						trans.sendSCD(
							m_source.getScdDir().getPath() + "/static",
							m_source.getRemoteDir() + "/static",
							new File(m_source.getScdDir().getPath(), "backup").getPath(),
							m_source.getRemoteinfo().isDeleteSCD()
						);
					} else {
						trans.sendSCD(
							m_source.getScdDir().getPath(),
							m_source.getRemoteDir(),
							new File(m_source.getScdDir().getPath(), "backup").getPath(),
							m_source.getRemoteinfo().isDeleteSCD()
						); // setting path...
					}
				} catch (IOException e) {
					Log2.error("[Bridge][SCDTransmit I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param sourceIdx
	 *            source's index number in seed file
	 * @param maxUrl
	 *            crawling maximum count
	 * @param urlDbPath
	 *            url db path
	 */
	private void dataCrawlByItemInfo(int sourceIdx, int maxUrl, String urlDbPath) {
		String popurl = "";
		RichURL richURL = new RichURL();
		while (true) {
			setLocale(curItem.getLocale());
			USER_CHARSET = curItem.getCharset();
			maxDepth = curItem.getMaxDepth();

			if (maxUrl == 0) {
				maxUrl = curItem.getMaxUrl();
			}

			if (maxDepth == 0) {
				maxDepth = 1000; // if max depth 0 then default value is 1000
			}

			while (urldb.getQ(richURL)) {
				try {
					popurl = richURL.getURL();
					if (!curItem.isSkipURL(popurl)) {
						saveURLData(popurl);
						if (writeCount >= maxUrl && maxUrl != 0) {
							System.out.print("[" + totalCount + "] ");
							break;
						}
					}
				} catch (IOException e) {
					Log2.error("[JOB Crawl Exception: ] [ " + "\n" + IOUtil.StackTraceToString(e) + "]");
				}
			}

			sourceIdx++; // [Source] count
			if (sourceIdx > sourceCount) { // source count compare
				break;
			}

			// setting... next seed info
			urldb.deinit();
			urldb.init(urlDbPath, true); // URL DB inintialize
			getSeedData(sourceIdx);

			notExtractURL = false;
			writeCount = 0;
		}
	}

	/**
	 * save Seedinfo method
	 * 
	 * @param start_cnt
	 *            Seed index number
	 */
	private void getSeedData(int start_cnt) {
		RichURL startURL = new RichURL();
		try {
			String source = seedinfo.getSource(start_cnt);
			if (source != null) {
				seedinfo.get(source, curItem);
				List url = curItem.getUrl();
				for (int i = 0; i < url.size(); i++) {
					// START URL
					String tmpURL = url.get(i).toString().replaceAll("\\\\", "/");
					startURL.setURL(tmpURL, source);
					urldb.putQ(startURL);
					urldb.insert(startURL);
				}

				urldb.Sync();
			}
		} catch (Exception e) {
			Log2.error("[JOB Get seed data exception]" + " [Get URL extract error from URL DB :" + "\n" + IOUtil.StackTraceToString(e) + "]");
		}
	}

	/**
	 * Crawldata save to SCD method
	 * HTML Content processing to filterHtmlData
	 * File Content processing to filterFileData.
	 * 
	 * @param seedurl
	 *            Crawl URL
	 * @throws IOException
	 *             SCD Write error
	 */
	private void saveURLData(String seedurl) throws IOException {
		String title = "";
		String[] st = StringUtil.split(seedurl, "$");
		if (st.length > 1) {
			int size = st.length;
			seedurl = st[0];
			title = "NONE";
			if (size == 2) {
				title = st[1];
			}
		} else {
			seedurl = st[0];
			title = "NONE";
		}

		seedurl = PlainText.convertFormattedTextToPlaintext(ParameterSort.sortUrlParameter(seedurl));
		curItem.addUrlElement(seedurl);

		LinkedHashMap<String, String> data;

		if (isBinaryExt(FileUtil.getFileExt(seedurl))) { // check url extention
			// attach file process
			String filterRootStr = "";
			Log2.debug("[Job] [Get Web Document File]", 3);

			String fileName = FileUtil.getFileName(seedurl);
			fileName = encodeFileNameAsUTF8(fileName);
			if (seedurl.lastIndexOf("/") > -1) {
				filterRootStr = wcse_home + FileUtil.getFileSeperator() + "Filter" + FileUtil.getFileSeperator()
						+ fileName;
			}

			File filterRoot = new File(filterRootStr);
			if (!filterRoot.exists()) {
				FileWriter fw = new FileWriter(filterRoot);
				fw.close();
			}
			if (seedurl.startsWith("http://") || seedurl.startsWith("https://")) {
				// file download
				seedurl = encodeURLAsUTF8(seedurl);
				GetAttachThread attach_thread = new GetAttachThread(seedurl, DOCUMENT_CHARSET, filterRootStr, curItem.getBasicAuthID(), curItem.getBasicAuthPW());
				attach_thread.start();

				try {
					attach_thread.join(1000 * 180); // 180sec wating
				} catch (InterruptedException e) {
				}

				if (!attach_thread.isFiltered()) {
					return;
				}

				Log2.debug("[Job] [Save URL Data: Get SCD Buffer Info, Title:" + fileName + ", SeedUrl:" + seedurl
						+ ", Source File:" + filterRootStr + " ]", 3);
				data = getSCDBuffer(fileName, seedurl, filterRootStr);
			} else {
				Log2.debug("Bad protocol. Only the HTTP and HTTPS protocols are supported.", 2);
				return;
			}
		} else {
			// Start Html Content processing ...
			Log2.debug("[Job] [Get SCD Contents And Extract URL Work Start ...]", 3);
			data = getSCDBuffer(title, seedurl, "");
			if (data == null) {
				return;
			}
		}

//		if (verb) {
//			System.out.println(filteredStr);
//		}

		totalCount++;
		writeCount++;

		// write to scd file
		fileManager.insert(data);
		data.clear();

		if (totalCount % 10 == 0) {
			if (totalCount % 100 == 0 || totalCount == 0) {
				System.out.print("[" + totalCount + "]");
				deleteThumbNail();
			} else {
				System.out.print(".");
			}
			if (isTest && totalCount == 10) {
				fileManager.close(isTest);
				System.exit(0);
			}
		}
	}

	/**
	 * scd buffer return method
	 * @param title
	 *            document subject variable
	 * @param seedurl
	 *            Crawl URL
	 * @param sourceFile
	 *            filtering file or url
	 * @return SCD Document
	 * @throws UnsupportedEncodingException
	 *             encoding error
	 */
	private LinkedHashMap<String, String> getSCDBuffer(String title, String seedurl, String sourceFile) throws UnsupportedEncodingException {
		String contentData = "";
		String type = "";
		String retStr = "";
		long fileSize = 0;
		if (!sourceFile.equals("")) {
			contentData = getFilterData(sourceFile);
			type = "file";
			File file = new File(sourceFile);
			if (file.exists()) {
				fileSize = file.length();
			}
		} else if (isAllowExt(seedurl) == false) {
			seedurl = encodeURLAsUTF8(seedurl);
			contentData = "";
			type = "file";
		} else {
			seedurl = encodeURLAsUTF8(seedurl);
			contentData = getURLContents(seedurl);
			if( contentData == null ) {
				contentData = "";
				type = "file";
			}
			else {
				type = "web";
			}
			fileSize = contentData.getBytes().length;
		}

		Log2.debug("[Job] [GetSCDBuffer: Source Type (" + type + ") ]", 3);

		InfoSet[] catalog = mapping.getCatalog();
		int tagCnt = catalog.length;

		String resultContStr = "";
		String tagValue[] = new String[tagCnt];
		if (type.equals("file")) { // case by file
			resultContStr = contentData;
		} else { // case by html document.
			// URL extract
			if (!curItem.isOnlyViewURL(seedurl)) {
				if (seedurl.indexOf("view") == -1) {
					String t_tail_src = curItem.getTailsrc();
					if (!t_tail_src.equals("")) {
						int ntailIdx = contentData.indexOf(t_tail_src);
						if (ntailIdx > -1) {
							contentData = contentData.substring(0, ntailIdx);
						}
					}
					if (maxDepthCnt <= maxDepth) {
						if (!notExtractURL) {
							setExtractUrl(curItem, urldb, seedurl, contentData.getBytes("utf-8"));

							// [islist]y �씤 寃쎌슦 紐⑸줉�럹�씠吏��뿉�꽌�뒗 留곹겕留� 異붿텧�븯怨� 蹂몃Ц �궡�슜�� 異붿텧�븯吏� �븡�뒗�떎.
							if (curItem.isList()) {
								notExtractURL = true;
								return null;
							}
						}
					} else {
						maxDepthCnt = 0;
					}
				}
			}

			Log2.debug("[Job] [HTML Parsing ...contents charset : " + DOCUMENT_CHARSET + " ]", 4);

			// String strContent = HtmlUtil.getHtmlParse(contentData.getBytes("utf-8"));
			String strContent = HtmlUtil.getHtmlParse(contentData.getBytes("utf-8"), m_source.isImgAltTag(),
					curItem.getDenyId(), curItem.getDenyClass(), false);

			// remove duplicate menu
			resultContStr = removeDuplicatedMenu(strContent);
		}

		Log2.debug("[Job][Mapping Seed] [Target : Title: " + title + ",  Extract URL: " + seedurl + "]", 3);

		boolean hasImageInfo = mapping.hasImageInfo();
		boolean hasThumbnailInfo = mapping.hasThumbnailInfo();
		String thumbnailData = "";
		String imageURL = "";
		int imageWidth = 0;
		int imageHeight = 0;

		if (hasImageInfo || hasThumbnailInfo) {
			try {
				ThumbNailInfo thumbNail = getThumbNailImageName(seedurl, contentData, hasThumbnailInfo);
				thumbnailData = thumbNail.getThumbNail();
				imageURL = thumbNail.getImageURL();
				imageWidth = thumbNail.getImageWidth();
				imageHeight = thumbNail.getImageHeight();
			} catch (JimiException e) {
				Log2.error("[JOB Get ThumbNailInfo  Exception] [" + "\n" + IOUtil.StackTraceToString(e) + "]");
			}
		}

		for (int colIdx = 0; colIdx < tagCnt; colIdx++) {
			tagValue[0] = EncryptUtil.MD5(seedurl) + m_source.getSrcPrefix() + curItem.getSource();
			int maxLen = catalog[colIdx].getMaxLen();
			if (catalog[colIdx].getType().equals("subject")) {
				title = getTitleByHtml(title, contentData, catalog[colIdx].isUseTitleTag(), type);
				if (isNoneTitle(title.trim()) || title.trim().equals("")) {
					if (maxLen == 0)
						maxLen = 30; // max len default value is 30
					if (resultContStr.length() < maxLen) {
						title = resultContStr.substring(0, resultContStr.length());
					} else {
						title = resultContStr.substring(0, maxLen) + "...";
					}
				}
				tagValue[colIdx] = title; // subject
			} else if (catalog[colIdx].getType().equals("content")) {
				if(catalog[colIdx].isHtmlParse()) {
					tagValue[colIdx] = resultContStr; // content
				} else {
					String strContent = HtmlUtil.getHtmlParse(contentData.getBytes("utf-8"), m_source.isImgAltTag(),
							curItem.getDenyId(), curItem.getDenyClass(), true);
					
					tagValue[colIdx] = strContent;
				}
			} else if (catalog[colIdx].getType().equals("url")) {
				tagValue[colIdx] = seedurl; // url
			} else if (catalog[colIdx].getType().equals("baseurl")) {
				tagValue[colIdx] = curItem.getBaseUrl(); // base_url
			} else if (catalog[colIdx].getType().equals("form")) {
				tagValue[colIdx] = type; // type
			} else if (catalog[colIdx].getType().equals("section")) {
				tagValue[colIdx] = curItem.getSection(); // section
			} else if (catalog[colIdx].getType().equals("section1")) {
				tagValue[colIdx] = curItem.getSection1(); // section
			} else if (catalog[colIdx].getType().equals("section2")) {
				tagValue[colIdx] = curItem.getSection2(); // section
			} else if (catalog[colIdx].getType().equals("section3")) {
				tagValue[colIdx] = curItem.getSection3(); // section
			} else if (catalog[colIdx].getType().equals("source")) {
				tagValue[colIdx] = curItem.getSource(); // source
			} else if (catalog[colIdx].getType().equals("date")) {
				tagValue[colIdx] = ""; // date
			} else if (catalog[colIdx].getType().equals("writer")) {
				tagValue[colIdx] = ""; // writer
			} else if (catalog[colIdx].getType().equals("filetype")) {
				tagValue[colIdx] = FileUtil.getFileExt(seedurl); // file type
			} else if (catalog[colIdx].getType().equals("filesize")) {
				tagValue[colIdx] = String.valueOf(fileSize); // file size
			} else if (catalog[colIdx].getType().equals("thumbnail")) {
				tagValue[colIdx] = thumbnailData; // thumbnail image name
			} else if (catalog[colIdx].getType().equals("imageurl")) {
				tagValue[colIdx] = imageURL; // thumbnail image name
			} else if (catalog[colIdx].getType().equals("imagewidth")) {
				tagValue[colIdx] = String.valueOf(imageWidth); // thumbnail image width
			} else if (catalog[colIdx].getType().equals("imageheight")) {
				tagValue[colIdx] = String.valueOf(imageHeight); // thumbnail image height
			} else if (catalog[colIdx].getType().equals("collectdivid")) {
				tagValue[colIdx] = HtmlDocument.getDivIdContents(curItem.getCollectDivIds(), seedurl,
						contentData.getBytes("utf-8"));
			} else if (catalog[colIdx].getType().equals("collectclass")) {
				tagValue[colIdx] = HtmlDocument.getClassContents(curItem.getCollectClasses(), seedurl,
						contentData.getBytes("utf-8"));
			} else if (catalog[colIdx].getType().equals("extend")) {
				tagValue[colIdx] = catalog[colIdx].getTypeValue(); // extended field
			} else if (catalog[colIdx].isSourceAlias() || catalog[colIdx].getType().equals("alias")) {
				tagValue[colIdx] = m_source.getSourceaias();
			}

			// Custom Class processing
			if (!catalog[colIdx].getClassName().equals("0x00")) {
				try {
					tagValue[colIdx] = CFactory.getInstance( catalog[colIdx].getClassName()).customData( tagValue[colIdx] );
				} catch (CustomException e) {
					Log2.error("[Job] [ " + "\n"
							+ IOUtil.StackTraceToString(e) + "\n]");
				} catch (BridgeException e) {
					Log2.error("[Job] [ " + "\n"
							+ IOUtil.StackTraceToString(e) + "\n]");
					e.printStackTrace();
				}
			}
		}

		// SCD data buffering ..
		return bufferingSCD(catalog, tagCnt, tagValue, contentData, type);
	}

	/**
	 * write scd buffer method
	 * @param catalog
	 *            InfoSet
	 * @param tagCnt
	 *            source document mapping tag count
	 * @param tagValue
	 *            meta tag value
	 * @param contentHTML
	 *            html document
	 * @param type
	 *            crawling data type
	 * @throws UnsupportedEncodingException
	 *             encoding error
	 */
	private LinkedHashMap<String, String> bufferingSCD(InfoSet[] catalog, int tagCnt, String[] tagValue, String contentHTML, String type)
			throws UnsupportedEncodingException {

		String metaStr = "";
		String userMetaStr = "";
		String tagName = "";
		Log2.debug("[Job][make SCD column] [each column data buffer append ...]", 3);

		LinkedHashMap<String, String> data = new LinkedHashMap<>();

		for (int colIdx = 0, tagIdx = 0; colIdx < tagCnt; colIdx++, tagIdx++) {
			tagName = "<" + catalog[colIdx].getTagName() + ">";// SCD TAG
			if (!catalog[colIdx].getMetaInfo().equals("") && !type.equals("file")) { // meta tag
				metaStr = catalog[colIdx].getMetaInfo();
				String metaValue = HtmlUtil.getUserMetaTagValue(contentHTML.getBytes("UTF-8"), metaStr);
				if (metaValue.equals("")) {
					metaValue = tagValue[tagIdx];
				}

				data.put(catalog[colIdx].getTagName(), metaValue);
			} else if (!catalog[colIdx].getUserTag().equals("") && !type.equals("file")) { // user tag
				userMetaStr = catalog[colIdx].getUserTag();
				data.put(catalog[colIdx].getTagName(), getUserMetaData(contentHTML, userMetaStr));
			} else if (catalog[colIdx].getType().equals("date") && !type.equals("file")) { // date type
				// �쐞 �뿉�꽌 HTML �뙆�씪�쓣 �닔�뻾�뻽湲� �븣臾몄뿉 諛섎났�쟻�씤 �옉�뾽�쓣 �닔�뻾 �븷 �븘�슂媛� �뾾�쓬
				// �뙆�떛�맂 string �뿉�꽌 �궇吏� �삎�떇�쓣 戮묒븘 �궦�떎
				String dateValue = getDate(contentHTML);
				if (dateValue.equals("") || dateValue.equals("19700101000000")) {
					dateValue = "19700101120100";
				}

				data.put(catalog[colIdx].getTagName(), dateValue);
			} else {
				if (tagValue[tagIdx] != null) {
					String str = getUserTagValue(contentHTML, tagName);
					if (!str.equals("")) {
						data.put(catalog[colIdx].getTagName(), str);
					} else {
						data.put(catalog[colIdx].getTagName(), tagValue[tagIdx]);
					}
				} else {
					data.put(catalog[colIdx].getTagName(), " ");
				}
			}
		}

		return data;
	}

	private String encodeFileNameAsUTF8(String filename) {
		StringBuilder sb = new StringBuilder();
	        StringBuilder sbNonAscii = new StringBuilder();
	        for( int i = 0; i < filename.length(); i++ ) {
	            char c = filename.charAt( i );
	            if( c > 127 || c < -126 ) {
	        		sbNonAscii.append( c );
	            } else {
	            	if( sbNonAscii.length() != 0 ) {
	                    try {
	                        sb.append( URLEncoder.encode( sbNonAscii.toString(), "UTF-8" ) );
	                        sbNonAscii.setLength( 0 );
	                    } catch ( UnsupportedEncodingException e ) {
	                        e.printStackTrace();
	                    }
	                }
	                if ( c == ' ' ) {
						sb.append("%20");
					} else if( c == ';' ) {
						sb.append("%3B");
					} else if( c == '#' ) {
						sb.append("%23");
					} else {
						sb.append(c);
					}
	            }
	        }

	        try {
	            sb.append( URLEncoder.encode( sbNonAscii.toString(), "UTF-8" ) );
	        } catch ( UnsupportedEncodingException e ) {
	            e.printStackTrace();
	        }

	        return sb.toString();
	}
	private String encodeURLAsUTF8( String url ) {
	    String header = "";
	    
	    if (url.startsWith( "https" )) {
	        header = "https://";
	    } else {
	        header = "http://";
	    }
	    
    	String[] tmpUrl = url.replace(header, "").split("/");
    	
    	try {
    		String port = "";
    		
    		int colon = tmpUrl[0].indexOf(":");
    		if( colon >= 0 ) {
    			port = tmpUrl[0].substring(colon);
    			tmpUrl[0] = tmpUrl[0].substring(0, colon);
    		}

			tmpUrl[0] = header + IDNA.toASCII(tmpUrl[0]) + port;
		} catch (IDNAException e1) {
			e1.printStackTrace();
		}

    	url = "";
    	for(int idx=0, urls=tmpUrl.length; idx<urls; idx++) {
    		if( idx > 0 ) {
				url += "/";
			}

    		url += tmpUrl[idx];
    	}

        StringBuilder sb = new StringBuilder();
        StringBuilder sbNonAscii = new StringBuilder();
        for( int i = 0; i < url.length(); i++ ) {
            char c = url.charAt( i );
            if( c > 127 || c < -126 ) {
        		sbNonAscii.append( c );
            } else {
                if( sbNonAscii.length() != 0 ) {
                    try {
                        sb.append( URLEncoder.encode( sbNonAscii.toString(), "UTF-8" ) );
                        sbNonAscii.setLength( 0 );
                    } catch ( UnsupportedEncodingException e ) {
                        e.printStackTrace();
                    }
                }
                if( c == ' ' ) {
					sb.append("%20");
				} else if( c == ';' ) {
					sb.append("%3B");
				} else if( c == '#' ) {
					sb.append("%23");
				} else {
					sb.append(c);
				}
            }
        }

        try {
            sb.append( URLEncoder.encode( sbNonAscii.toString(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }

        return sb.toString();
    }

	/**
	 * create thumbnail info method
	 * @param seedurl
	 *            crawl url
	 * @param contentData
	 *            html document contents
	 * @param hasThumbnailInfo
	 *            create thumbnail info y/n
	 * @return ThumbNailInfo Object
	 * @throws UnsupportedEncodingException
	 *             encoding error
	 * @throws JimiException
	 *             library error
	 */
	private ThumbNailInfo getThumbNailImageName(String seedurl, String contentData, boolean hasThumbnailInfo)
			throws UnsupportedEncodingException, JimiException {

		ThumbNailInfo thumbnail = new ThumbNailInfo();
		String imageRootPath = m_source.getThumbnailDir();
		if (imageRootPath.equals("")) {
			imageRootPath = FileUtil.lastSeparator(wcse_home) + "thumbnail";
		}
		FileUtil.makeDir(imageRootPath);

		int imageWidth = m_source.getThumbnailWidth();
		int imageHeight = m_source.getThumbnailHeight();
		boolean isInclude = m_source.isInclude();

		Vector v = HtmlDocument.getImageLink(seedurl, contentData.getBytes("utf-8"));
		int imageNumber = v.size();

		for (int n = 0; n < imageNumber; n++) {
			String imgName = v.get(n).toString();
			if (imgName.toLowerCase().indexOf(".jpg") > -1) {// only jpg file type
				String unid = EncryptUtil.MD5(seedurl);
				String jpgImage = FileUtil.lastSeparator(imageRootPath) + "wisenut_" + unid + getFileName(imgName);
				GetAttachThread attach_thread = new GetAttachThread(imgName, DOCUMENT_CHARSET, jpgImage, curItem.getBasicAuthID(), curItem.getBasicAuthPW());
				attach_thread.start();
				try {
					attach_thread.join(30000); // 30 sec wating
				} catch (InterruptedException e) {
				}

				File fJpgImage = new File(jpgImage);
				if (!fJpgImage.exists())
					continue;

				Image image = new ImageIcon(jpgImage).getImage();
				// Get the dimensions of the image; these will be non-negative
				int width = image.getWidth(null);
				int height = image.getHeight(null);
				int imgIdx = getFileName(imgName).indexOf(".");
				image.flush();
				if (height >= 150) { // if jpg height 150 pixel over : normal size
					thumbnail.setImageURL(imgName);
					thumbnail.setImageHeight(height);
					thumbnail.setImageWidth(width);
				}
				// if jpg height 150 pixel over : normal size
				if (height >= 150 && imgIdx > -1 && hasThumbnailInfo) {
					String thumbNailName = unid + getFileName(imgName).substring(0, imgIdx) + ".jpg";
					String thumbNailSubPath = FileUtil.lastSeparator(srcId)
							+ FileUtil.lastSeparator(String.valueOf(sourceCount));
					String thumbNailFullPath = FileUtil.lastSeparator(imageRootPath) + thumbNailSubPath;

					FileUtil.makeDir(thumbNailFullPath);
					double div = 0;
					if (!new File(thumbNailFullPath + thumbNailName).exists()) {
						ThumbNailThread thumbNailThread = new ThumbNailThread(jpgImage, imageWidth, imageHeight,
								thumbNailFullPath + thumbNailName);
						thumbNailThread.start();
						long start = System.currentTimeMillis();
						try {
							thumbNailThread.join(60000); // wating for 60seconds
						} catch (InterruptedException e) {
						}
						long end = System.currentTimeMillis();
						div = ((double) (end - start) / 1000);
					}
					if (div >= 60) { // if 60seconds over
						thumbnail.setThumbNail("");
					} else {
						if (isInclude) {
							thumbnail.setThumbNail(getThumbNailData(thumbNailFullPath, thumbNailName));
						} else {
							String strThumbnail = "/" + StringUtil.replace(thumbNailSubPath + thumbNailName, "\\", "/");
							thumbnail.setThumbNail(strThumbnail);
						}
					}
				}
				// Delete image
				fJpgImage.delete();
				break;
			}
		}
		// IMAGE AND Thumnail Process End
		return thumbnail;
	}

	private String getThumbNailData(String imagePath, String thumbnailName) {
		String result = "";
		try {
			File fpThumbnail = new File(FileUtil.lastSeparator(imagePath) + thumbnailName);
			byte[] imageBytes = loadFile(fpThumbnail);
			byte[] encoded = StringUtil.encodeBase64toByte(imageBytes);
			result = new String(encoded, "ASCII");
		} catch (IOException e) {
			Log2.error("[ThumbNailData] [BridgeException " + "\n" + IOUtil.StackTraceToString(e) + "]");
		}
		return result;
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] barr = new byte[1024];
		while (true) {
			int r = in.read(barr);
			if (r <= 0) {
				break;
			}
			out.write(barr, 0, r);
		}
	}

	private byte[] loadFile(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			copy(in, buffer);
			return buffer.toByteArray();
		} finally {
			in.close();
		}
	}

	private void deleteThumbNail() {
		String thumbnailDirectory = m_source.getThumbnailDir();
		if (!thumbnailDirectory.equals("")) {
			File files = new File(thumbnailDirectory);
			File[] images = files.listFiles(new CustomFileNameFilter("wisenut_"));
			int length = images.length;
			for (int i = 0; i < length; i++) {
				images[i].deleteOnExit();
			}
		}
	}

	/**
	 * @param fullFileName
	 *            file name
	 * @return String file name
	 */
	public String getFileName(String fullFileName) {
		String filename = "";
		int len = fullFileName.length();
		int Idx = fullFileName.lastIndexOf("/");

		if (Idx > -1 && (Idx + 1) != len)
			filename = fullFileName.substring(Idx + 1, len);

		if (filename.indexOf("?") != -1) {
			int idx = filename.lastIndexOf("=");
			if (idx != -1)
				filename = filename.substring(idx + 1, filename.length());
		}
		return filename;
	}

	/**
	 * 
	 * @param contentHTML
	 *            html document
	 * @param tagName
	 *            find tag name
	 * @return String tag value
	 */
	private String getUserTagValue(String contentHTML, String tagName) {
		String str = "";
		for (int i = 0; i < curItem.getUsertag().size(); i++) {
			String userTag = curItem.getUsertag().get(i).toString();
			if (tagName.equals("<" + getUserTag(userTag) + ">") && !(getUserTag(userTag).equals(""))) {
				str = getUserTagData(contentHTML, curItem.getUsertag().get(i).toString());
				// user tag process by customjs class
				if (!curItem.getCustomJS().equals("")) {
					try {
						str = CFactory.getInstance(curItem.getCustomJS()).customUserDefine(curItem, contentHTML,
								userTag);
					} catch (CustomException e) {
						Log2.error("[UserTagValue] [CustomException " + "\n" + IOUtil.StackTraceToString(e) + "]");
					} catch (BridgeException e) {
						Log2.error("[UserTagValue] [BridgeException " + "\n" + IOUtil.StackTraceToString(e) + "]");
					}
				}
				Log2.debug("[Job] [UserDefine Value: " + str + " ]", 3);
			}
		}
		return str;
	}

	/**
	 * Get HTML Document subject
	 * @param title
	 *            html document subject
	 * @param strHTML
	 *            html document contents
	 * @param useTitleTag
	 *            extract title by Title Tag
	 * @param docType
	 *            crawling document type
	 * @return String
	 * @throws UnsupportedEncodingException
	 *             encoding error
	 */
	private String getTitleByHtml(String title, String strHTML, boolean useTitleTag, String docType)
			throws UnsupportedEncodingException {
		if (!useTitleTag && !docType.equals("file") || (title.equals("NONE"))) {
			title = HtmlUtil.getTagValue(strHTML.getBytes("UTF-8"), "title");
		}
		return title;
	}

	/**
	 * 
	 * @param seedurl
	 *            crawling url
	 * @return boolean true/false
	 */
	private boolean checkSeedUrlRule(String seedurl) {
		int listIdx = (seedurl.toLowerCase()).indexOf("list.");
		int seedLen = seedurl.length();
		return !(listIdx > -1 && ((seedLen - 4) <= (listIdx + 4)))
				&& !(seedurl.indexOf("download") > -1 || (seedurl.indexOf("down.asp") > -1) || (seedurl
						.indexOf("down.php") > -1))
				&& !(((seedurl.toLowerCase()).indexOf("del") > -1) || ((seedurl.toLowerCase()).indexOf("write") > -1)
						|| ((seedurl.toLowerCase()).indexOf("modify") > -1) || ((seedurl.toLowerCase())
						.indexOf("reply") > -1));
	}

	/**
	 * 
	 * @param strContent
	 *            html document contents
	 * @return String
	 */
	private String removeDuplicatedMenu(String strContent) {
		List temp_tailmenu = curItem.getTailmenu();
		List temp_skipmenu = curItem.getSkipmenu();
		DuplicateMenu dtu = new DuplicateMenu();
		String retStr = dtu.deleteMenu(strContent, temp_skipmenu);
		retStr = dtu.deleteTailMenu(retStr, temp_tailmenu);

		return retStr;
	}

	/**
	 * Get HTML Document method
	 * @param addrURL
	 *            Seedurl
	 * @return Content HTML
	 */
	private String getURLContents(String addrURL) {
		String pageURLContents = "";
		if (addrURL == null || !(addrURL.startsWith("http://") || addrURL.startsWith("https://"))) {
			Log2.debug("[Job] [Not Supported Http Protocol, URL: " + addrURL + " ]", 2);
			return pageURLContents;
		} else if (addrURL.length() == 0) {
			Log2.debug("[Job] [This URL Contents Length is 0]", 3);
			return pageURLContents;
		} else {
			URL objURL = null;
			try {
				objURL = new URL(addrURL);
			} catch (MalformedURLException e) {
				Log2.error("[Job] [" + IOUtil.StackTraceToString(e) + " ]");
			}
			if (objURL == null) {
				return "";
			}
			if (objURL.getProtocol().equals("http") || objURL.getProtocol().equals("https")) {
				curItem.setBaseUrl(objURL.getHost());
				Log2.debug("[Job] [GetURLContents : Get HTML input Stream working ...]", 3);

				GetURLContentsThread url_thread = new GetURLContentsThread(USER_CHARSET, addrURL, curItem.getBasicAuthID(), curItem.getBasicAuthPW());
				
				url_thread.start();

				try {
					url_thread.join(60000); // wating for 60seconds.
				} catch (InterruptedException e) {
					pageURLContents = " ";
					return pageURLContents;
				}

				this.DOCUMENT_CHARSET = url_thread.getContentsCharSet();
				pageURLContents = url_thread.getPageURLContents();
			} else {
				pageURLContents = "";
			}
		}
		return pageURLContents;
	}

	/**
	 * 
	 * @param userStr
	 * @return String
	 */
	private String getUserTag(String userStr) {
		if (userStr.indexOf("=") > -1) {
			userStr = userStr.substring(0, userStr.indexOf("="));
		}
		return userStr;
	}

	/**
	 * Get user defined tag method
	 * @param content
	 *            html document
	 * @param userStr
	 *            user tag name
	 * @return String
	 */
	private String getUserTagData(String content, String userStr) {
		String temp = "";
		String strResult = "";
		if (userStr.indexOf("=") > -1) {
			temp = userStr.substring(userStr.indexOf("=") + 1, userStr.length());
		} else {
			return strResult;
		}

		int pos = temp.indexOf(":");
		if (pos > -1) {
			String StartStr = temp.substring(0, pos);
			String tagCnt = temp.substring(pos + 1, temp.length());
			try {
				strResult = HtmlUtil.getTagValue(content.getBytes(), StartStr, Integer.parseInt(tagCnt));
			} catch (Exception e) {
				Log2.error("[JOB Get User Tag Data  Exception] [" + "\n" + IOUtil.StackTraceToString(e) + "]");
			}
		} else {
			return strResult;
		}
		return strResult;
	}

	/**
	 * Get user defined matatag method
	 * @param Content
	 *            html document
	 * @param metaName
	 *            meta tag name
	 * @return String
	 */
	private String getUserMetaData(String Content, String metaName) {
		String retStr = "";
		int st_pos = Content.indexOf(metaName) + metaName.length();
		int ed_pos = Content.indexOf(">", st_pos);

		if (st_pos > 0 && ed_pos > 0) {
			//retStr = Content.substring(st_pos, ed_pos).replaceAll("\"", "");
			retStr = Content.substring(st_pos, ed_pos);
			retStr = retStr.trim();

			if (!retStr.equals("")) {
				retStr = retStr.replaceAll("content=", "");
				retStr = retStr.substring(retStr.indexOf("\"") + 1, retStr.lastIndexOf("\""));
				// retStr = retStr.replaceAll("=", "");
			}
		} else {
			retStr = " ";
		}
		return retStr;

	}

	/**
	 * Check for Html Document subject
	 * @param title
	 *            title string
	 * @return True/False
	 */
	private boolean isNoneTitle(String title) {
		title = title.toLowerCase();
		for (int i = 0; i < m_Arr_Title.length; i++) {
			if (m_Arr_Title[i].toLowerCase().equals(title)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * extract web content date format method
	 * support format YYYY-MM-DD , YYYY/MM/DD , YYYY.MM.DD
	 * @param Content
	 *            html document
	 * @return String
	 */
	private String getDate(String Content) {
		String pattern = "[0-9-/.]*[0-9]"; // format regular expression
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(Content);
		String token = "";
		while (m.find()) {
			token = m.group(); // 2005-05-18 or 2005/05/18 or 2005.05.18
			if (token.length() == 10 && (token.indexOf("-") > -1 || token.indexOf("/") > -1 || token.indexOf(".") > -1)) {
				token = token.replace('/', '-');
				token = token.replace('.', '-');
				String[] dateArray = StringUtil.split(token, "-");
				if (dateArray.length == 3) {
					if (isInteger(dateArray[0]) && dateArray[0].length() == 4 && isInteger(dateArray[1])
							&& dateArray[1].length() == 2) {
						token = token.substring(0, 10); // 2005-05-18
						token = DateUtil.parseDate(token, "yyyy-mm-dd");
						break;
					}
				} else {
					token = DateUtil.parseDate("1970-01-01", "yyyy-mm-dd");
				}
			} else {
				token = DateUtil.parseDate("1970-01-01", "yyyy-mm-dd");
			}
		}
		return token;
	}

	/**
	 * Get filtered data method
	 * 
	 * @param filePath
	 *            filtering file path
	 * @return Filtered Result String
	 */
	private String getFilterData(String filePath) {
		String[][] filterList = new String[1][1];
		filterList[0][0] = filePath;

		FilterSource filterSource = new FilterSource();
		filterSource.setDir("");
		filterSource.setRetrival("local");
		filterSource.setCondition("chk-ext-indexof");
		filterSource.setFilterType("sn3f");

		filter.setFilterOption(m_source.getFilterOption());
		String str = filter.getFilterData(filterList, filterSource, m_source.getScdCharSet());
		if (this.isFilterDel) {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
				Log2.debug("[JOB Get Filter Data] [Attach Original File Delete Success]", 3);
			}
		}
		return str;
	}

	/**
	 * Extract URL Mehtod
	 * @param item
	 *            SeedItem
	 * @param urldb
	 *            URL DB
	 * @param seedUrl
	 *            crawling url
	 * @param htmlContents
	 *            html document contents
	 */
	private void setExtractUrl(SeedItem item, URLDB urldb, String seedUrl, byte[] htmlContents) {
		Vector links = HtmlDocument.getLink(seedUrl, htmlContents, true);
		if (links == null)
			return;
		String save_title_url = "";
		int size = links.size();

		Log2.debug("[Job][Source Extract URL: " + seedUrl + ", Extract Link Number : " + size + "]", 3);
		if (size == 0)
			return;
		for (int n = 0; n < size; n++) {
			save_title_url = links.get(n).toString();
			save_title_url = save_title_url.replaceAll("\\\\",  "/");
			
			String[] urlPaths = save_title_url.split( "/" );
            int urlPathCnt = urlPaths.length;
            
            String lastLink = urlPaths[urlPathCnt - 1];
                
            if( lastLink.indexOf( "?" ) == 0 )
            {
                save_title_url = item.getHomeurl() + lastLink;
            }
			
			
			String[] tempArray = StringUtil.split(save_title_url, "$");
			int len = tempArray.length;
			try {
				if (len > 1) {
					String urlAddr = tempArray[0];
					//if(urlAddr.indexOf("/download.php/") > 0) Log2.debug("[info] Link Url : " + urlAddr);
//					if(urlAddr.indexOf("document_id=") > 0) Log2.debug("[info] Link Url : " + urlAddr);
					if (!curItem.getCustomJS().equals("")) {
						urlAddr = CFactory.getInstance(curItem.getCustomJS()).customData(curItem, urlAddr);
						if (!urlAddr.equals(""))
							save_title_url = urlAddr + "$" + tempArray[1];
					}
					
					RichURL url = new RichURL(urlAddr, item.getSource());
					RichURL titleURL = new RichURL(save_title_url, item.getSource());
					if (item.isWriteAllowURL(urlAddr) && !item.isSkipURL(urlAddr) && !urlAddr.equals(seedUrl)) {
						if (!urldb.isExist(url)) {
							Log2.debug("[ExtractUrl][URL DB Insert Data : " + save_title_url + " ]", 3);
							urldb.putQ(titleURL);
							urldb.insert(url);
						}
					}
					maxDepthCnt++;
				}
			} catch (Exception e) {
				Log2.error("[ExtractUrl] [Extract Link URL Exception : " + "\n" + IOUtil.StackTraceToString(e) + "]");
			}
		}
		urldb.Sync();
	}

	/**
	 * @param url
	 *            Link URL
	 * @return True/False
	 */
	private boolean isAllowExt(String url) {
		// exe -> allow cgi-bin
		if (url.indexOf("cgi-bin") > -1) {
			return true;
		}
		String ext = FileUtil.getFileExt(url);
		ext = ext.toLowerCase();
		for (int i = 0; i < m_Arr_Ext.length; i++) {
			if (m_Arr_Ext[i].equals(ext)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param strVal
	 *            string
	 * @return boolean
	 */
	private boolean isInteger(String strVal) {
		int tempVal = -1;
		try {
			tempVal = Integer.parseInt(strVal);
			if (tempVal < 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * setting html page local
	 * @param locale
	 *            contry location
	 */
	private void setLocale(String locale) {
		if (locale.equals("japan")) {
			DOCUMENT_CHARSET = "shift_jis";
		} else if (locale.equals("china")) {
			DOCUMENT_CHARSET = "gb2312";
		} else if (locale.equals("hongkong")) {
			DOCUMENT_CHARSET = "big5";
		} else if (locale.equals("latin")) {
			DOCUMENT_CHARSET = "iso-8859-1";
		} else {
			String encoding = System.getProperty("file.encoding");// check for korean locale
			if (encoding.toLowerCase().equals("ms949") || encoding.toLowerCase().indexOf("euc-kr") > -1) {
				DOCUMENT_CHARSET = "euc-kr"; // only korea locale
			} else {
				DOCUMENT_CHARSET = "utf-8";
			}
		}
	}

	private final static String[] m_Arr_Title = new String[] {
		"0",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
		"9",
		"frame",
		"left",
		"main",
		"new document",
		"next",
		"none",
		"prev",
		"right",
		"top",
		"untitled document"
	};

	private final static String[] m_Arr_Ext = new String[] {
		"3fr",
		"3g2",
		"3gp",
		"3gp2",
		"3gpp",
		"a00",
		"a01",
		"a02",
		"a03",
		"aac",
		"ac3",
		"amr",
		"amv",
		"ape",
		"apx",
		"arw",
		"asf",
		"asx",
		"avi",
		"bak",
		"bin",
		"bmp",
		"cb7",
		"cbr",
		"cbt",
		"cbz",
		"cr2",
		"crw",
		"css",
		"cue",
		"dat",
		"dds",
		"divx",
		"dng",
		"dpg",
		"dpl",
		"dts",
		"dtshd",
		"dvr-ms",
		"eac3",
		"erf",
		"evo",
		"exe",
		"f4v",
		"flac",
		"flv",
		"gif",
		"hdp",
		"hv3",
		"ico",
		"ifo",
		"iso",
		"j2c",
		"j2k",
		"jp2",
		"jpc",
		"jpe",
		"jpeg",
		"jpf",
		"jpg",
		"jpm",
		"jpx",
		"jxr",
		"k3g",
		"kdc",
		"m1a",
		"m1v",
		"m2a",
		"m2t",
		"m2ts",
		"m2v",
		"m3u",
		"m3u8",
		"m4a",
		"m4b",
		"m4p",
		"m4v",
		"mef",
		"mka",
		"mkv",
		"mod",
		"mos",
		"mov",
		"mp2",
		"mp2v",
		"mp3",
		"mp4",
		"mpa",
		"mpc",
		"mpe",
		"mpeg",
		"mpg",
		"mpl",
		"mpls",
		"mpv2",
		"mrw",
		"mts",
		"nef",
		"nrw",
		"nsr",
		"nsv",
		"ogg",
		"ogm",
		"ogv",
		"orf",
		"pbm",
		"pcx",
		"pef",
		"pgm",
		"pls",
		"png",
		"pnm",
		"ppm",
		"ps",
		"psd",
		"qt",
		"ra",
		"raf",
		"ram",
		"rm",
		"rmvb",
		"rpm",
		"rw2",
		"scr",
		"skm",
		"sr2",
		"srw",
		"swf",
		"tak",
		"tga",
		"tif",
		"tiff",
		"tp",
		"tpr",
		"trp",
		"ts",
		"vob",
		"wab",
		"wav",
		"wax",
		"wdp",
		"webbp",
		"webm",
		"wm",
		"wma",
		"wmp",
		"wmv",
		"wmx",
		"wtv",
		"wv",
		"wvx",
		"x3f"
	};

	private final static String[] m_Allow_Ext = new String[] {
		"001",
		"7z",
		"ace",
		"aes",
		"alz",
		"apk",
		"arj",
		"bh",
		"bin",
		"bz2",
		"cab",
		"chm",
		"doc",
		"docx",
		"dwg",
		"eml",
		"gul",
		"gz",
		"hwd",
		"hwn",
		"hwp",
		"hwx",
		"img",
		"ipa",
		"iso",
		"isz",
		"jar",
		"jtd",
		"lha",
		"lzh",
		"lzma",
		"lzx",
		"mdb",
		"mdi",
		"mht",
		"mp3",
		"msg",
		"pdf",
		"pma",
		"ppt",
		"pptx",
		"ps2",
		"rar",
		"rtf",
		"swf",
		"sxc",
		"sxi",
		"sxw",
		"tar",
		"tgz",
		"tgz2",
		"tlz",
		"txt",
		"txz",
		"udf",
		"war",
		"wim",
		"wpd",
		"xls",
		"xlsx",
		"xml",
		"xpi",
		"xz",
		"z",
		"zip",
		"zipx"
	};

	private boolean isBinaryExt(String fileExt) {
		boolean isRet = false;
		fileExt = fileExt.toLowerCase();
		for (int i = 0; i < m_Allow_Ext.length; i++) {
			if (m_Allow_Ext[i].equals(fileExt)) {
				isRet = true;
				break;
			}
		}

		if (!isRet) {
			Log2.debug("[Filter] [None Filtering Source File Ext(" + fileExt + ")]", 4);
		}

		return isRet;
	}
}