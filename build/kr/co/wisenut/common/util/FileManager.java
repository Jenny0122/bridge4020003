package kr.co.wisenut.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import kr.co.wisenut.common.logger.Log2;

public abstract class FileManager {
    protected static final int BUFFER_SIZE =  4096;
    protected final String filterDir = "Filter"; //Fixed directory name

    //Define SCD File type
    protected static final String[] TYPE_STRING = new String[] { "I", "U", "D", "R" };

    protected static final int TYPE_INSERT = 0;
    protected static final int TYPE_UPDATE = 1;
    protected static final int TYPE_DELETE = 2;
    protected static final int TYPE_REPLACE = 3;

    // file directory variable
    protected final File dir;

    // file name
    protected final File[] files;
    protected final String sourceIdx;

    // file WRITER
    protected final BufferedWriter[] fileWriter;

    // Add SCD document count
    protected final int[] fileCount;
    protected static int MaxFileCount = 0;
    protected static int MaxBufferSize = 2048 * 1000 * 1000;

    protected boolean isTEST = false;
    protected boolean isMKE = false;
    protected String charset = "";

    /**
     * Construct new instance with variables
     * @param scdDir SCD file directory
     *                Max count of documents are written to a file
     * @param charset scd encoding charset
     */
    public FileManager(String scdDir, String charset) {
        this(new File(scdDir), charset, "00");
    }

    public FileManager(String scdDir, String charset, boolean isMKE) {
        this(new File(scdDir), charset, "00");
        this.isMKE = isMKE;
    }

    public FileManager(File scdDir,String charset, boolean isMKE) {
        this(scdDir, charset, "00");
        this.isMKE = isMKE;
    }

    /**
     * Construct new instance with variables
     * @param dir SCD file directory
     *                Max count of documents are written to a file
     * @param charSet scd encoding charSet
     */
    public FileManager(File dir,String charSet, String sourceIdx) {
        this.dir = dir;

        // Variables initialize
        this.files = new File[4];
        this.sourceIdx = sourceIdx;
        this.fileWriter = new BufferedWriter[4];
        this.fileCount = new int[] { 0, 0, 0, 0 };

        if (charset.equals("") || charSet.equalsIgnoreCase("UTF-8")) {
            Log2.out("[FileManager] [File Encoding : UTF-8]");
            this.charset = "UTF-8";
        } else {
            Log2.out("[FileManager] [File Encoding :+" + charset + "]");
            this.charset = charSet;
        }

        // Create the directory, if not exists
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (System.getProperty("file.count") != null) {
            try {
                MaxFileCount = Integer.parseInt(System.getProperty("file.count"));
            } catch(Exception e) {

            }
        }
        
        if (System.getProperty("file.size") != null ) {
        	try {
        		MaxBufferSize = Integer.parseInt(System.getProperty("file.size")) * 1000 * 1000;
        	} catch(Exception e) {

            }
        }
    }

    public void insert(LinkedHashMap data) throws IOException {
        appendData(TYPE_INSERT, data);
    }

    public void update(LinkedHashMap data) throws IOException {
        appendData(TYPE_UPDATE, data);
    }

    public void delete(LinkedHashMap data) throws IOException {
        appendData(TYPE_DELETE, data);
    }

    public void replace(LinkedHashMap data) throws IOException {
        appendData(TYPE_REPLACE, data);
    }

    protected void errorFlushAll() throws IOException {
        for (int i = 0; i < fileWriter.length; i++) {
            errorFlush(i);
        }
    }

    protected void flushAll() throws IOException {
        for (int i = 0; i < fileWriter.length; i++) {
            flush(i);
        }
    }

    public void errorClose(boolean isTest) throws IOException {
        this.isTEST = isTest;
        errorFlushAll();
    }

    public void close(boolean isTest) throws IOException {
        this.isTEST = isTest;
        flushAll();
    }

    protected abstract void appendData(int type, LinkedHashMap<String, String> data) throws IOException;

    protected abstract File getTempFile(int type);

    protected void errorFlush(int type) throws IOException {
        if (fileWriter[type] == null) {
            return;
        }

        fileWriter[type].flush();
        fileWriter[type].close();

        // Set writer to null
        fileWriter[type] = null;
        fileCount[type] = 0;

        // Rename temporary file to real file
        File tmpFile = files[type];
        String tmpName = tmpFile.getName();
        if (tmpFile.exists() && tmpFile.length() == 0) {
            // Temporary SCD file is blank
            if (!tmpFile.delete()) {
                System.out.println( "[FileWriter] File delete error. " + tmpName);
            }
        }
    }

    protected abstract void flush(int type) throws IOException;

    public abstract boolean delete();

}
