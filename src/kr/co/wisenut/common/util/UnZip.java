package kr.co.wisenut.common.util;

import kr.co.wisenut.common.logger.Log2;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.*;
import java.text.Format;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 12. 7
 * Time: 오후 3:08:03
 * To change this template use File | Settings | File Templates.
 */
public class UnZip {
    private  ZipFile zf;
    private List zipList;
    private  File unPackDir;
    private int preFix = 0;

    public static final int EOF = -1;

    private final int BUFFER = 2048;

    public List unZip(File srcFilePath, String targetDir) throws IOException {
        return unZip(srcFilePath.getPath(), targetDir);
    }

    public List unZipOld(String srcFilePath, String targetDir) throws IOException {
        FileUtil.makeDir(targetDir);    // 압축해제 하기 위한 폴더를 생성한다
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(srcFilePath);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File targetFile;
        List zipList = new ArrayList(10);
        while((entry = zis.getNextEntry()) != null) {
            Log2.debug("Extracting: " +entry, 3);
            int count;
            byte data[] = new byte[BUFFER];
            // write the files to the disk
            targetFile = new File(targetDir, entry.getName());
            zipList.add(targetFile.getPath());
            FileOutputStream fos = new FileOutputStream(targetFile);
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER))
                    != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        zis.close();

        return zipList;
    }

    public List unZip(String zipFilePath, String unZipDir) throws IOException {
        zipList = new ArrayList(10);
        File zipFile = new File(zipFilePath);

//        Enumeration<ZipArchiveEntry> lenum;
        Enumeration lenum;

        try {
            zf = new ZipFile(zipFile, getEncode());
            String unpackPath = zipFile.getAbsolutePath();

            unPackDir = new File(unZipDir);
            FileUtil.makeDir(unZipDir);    // 압축해제 하기 위한 폴더를 생성한다
            lenum = (Enumeration) zf.getEntries();

            while (lenum.hasMoreElements()) {
                ZipArchiveEntry target = (ZipArchiveEntry) lenum.nextElement();
//                if(FileUtil.isFiltered(target.getName())) {
                    saveEntry( target );
//                }

            }
            zf.close();
        } catch (FileNotFoundException e) {
            Log2.debug("zip file not found (" + zipFile.getPath() + ")", 3);
        }
        return zipList;
    }

    /**
     * zip 파일을 해제하고 해제한 zip 파일 안에 다른 zip 파일이 존재하는경우 재귀적으로 해제한다.
     * @param zipFilePath
     * @param unZipDir
     * @return
     * @throws IOException
     */
    public void unZipAll(String zipFilePath, String unZipDir) throws IOException {
        List unZipListAll = new ArrayList(20);
        List unZipList = unZip(zipFilePath, unZipDir);
        for(int i=0; i < unZipList.size(); i++) {
            if(isZipFile((String) unZipList.get(i))) {
                File zipFile = new File((String) unZipList.get(i));
                unZipAll(zipFile.getPath(), new File(unZipList.get(i)+".d").getPath());
            }
        }
    }

    private List getAllFile(File dir) {
        List listFilesPath = new ArrayList();
        File[] listFiles = dir.listFiles();
        for(int i=0; i < listFiles.length; i++) {
            if(listFiles[i].isDirectory()) {
                getAllFile(listFiles[i]);
            }
            if(!listFiles[i].delete()) {
                Log2.debug("UnZip::getAllFile() delete fail : " + listFiles[i], 3);
                int count = 0;
                while (count < 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    listFiles[i].delete();
                    count++;
                }
            }
        }
        return listFilesPath;
    }

    private  void saveEntry(ZipArchiveEntry target) throws  IOException {
        try {
            if(target.isDirectory()) {
                FileUtil.makeDir(new File(unPackDir, target.getName()).getPath());
            } else {
                File file = new File(unPackDir, target.getName());
                preFix++;
                InputStream is = zf.getInputStream(target);
                BufferedInputStream bis = new BufferedInputStream(is);
                File dir = new File(file.getParent());
                FileUtil.makeDir(dir.getPath());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int c;
                while ((c = bis.read()) != EOF) {
                    bos.write((byte) c);
                }
                bos.flush();
                bos.close();
                fos.flush();
                fos.close();
                bis.close();
                is.close();

                zipList.add(file.getPath());
            }
        }  catch (IOException e) {
            Log2.error(e.toString());
        }
    }
    private String getEncode() {
        String encode = System.getProperty("sun.jnu.encoding") != null ? System.getProperty("sun.jnu.encoding") : System.getProperty("file.encoding");
        return encode;
    }

    public static boolean isZipFile(String path) {
        boolean isZip = false;
        if(path.endsWith(".zip") || path.endsWith(".ZIP")) {
            isZip = true;
        }
        return isZip;
    }

    public static void main(String[] args) throws Exception {
        Log2.setLogger("c:/Temp/log", "day", true, 3, false, "test");

        System.out.println("=================================");

        File file = new File("", "c:/Temp/softcamp/keyDAC.sc");
        System.out.println(file.getPath());
        List zipList = null;
//        try {
//            zipList = new UnZip().unZip(args[0], "c:/Temp/unzip");
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        UnZip unZip = new UnZip();
        unZip.unZipAll("c:/Temp/softcamp/kms6638083808517703817 - 복사본.zip", "c:/Temp/unZipAll");

//        File dir = new File("c:/Temp/unZipAll");
//        unZip.getAllFile(dir);

//        List list = unZip.getAllFile(dir);
//        for(int k=0; k < list.size(); k++) {
//            System.out.println(list.get(k));
//        }
    }

}
