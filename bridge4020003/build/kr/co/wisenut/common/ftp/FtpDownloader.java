package kr.co.wisenut.common.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpDownloader {

	private FtpDownloaderConfig config;
	private FTPClient ftp = new FTPClient();

	public FtpDownloader() throws ConfigException {
		this.config = new FtpDownloaderConfig();
	}

	public void connect() throws FtpDownloaderException {

		try {
			Log2.debug("[info] [FtpDownloader] [FTP server info=ip:" + config.ip + ",port:" + config.port + "]");
			Log2.debug("[info] [FtpDownloader] [FTP connecting...]");

			try {
				ftp.connect(config.ip, config.port);
			} catch (Exception e) {
				throw new FtpDownloaderException("FTP connect fail:" + e.getMessage());
			}

			Log2.debug("[info] [FtpDownloader] [FTP connect success]");

			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				disconnect();
				throw new FtpDownloaderException("Invalid reply code:" + getReplyString(ftp));
			}

			if (!ftp.login(config.userId, config.passwd)) {
				disconnect();
				throw new FtpDownloaderException("FTP login fail:" + getReplyString(ftp));
			}

			Log2.debug("[info] [FtpDownloader] [FTP login success]");

			if (config.binary) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				Log2.debug("[info] [FtpDownloader] [Set binary mode]");
			} else {
				ftp.setFileType(FTP.ASCII_FILE_TYPE);
				Log2.debug("[info] [FtpDownloader] [Set ascii mode]");
			}

			if (config.passive) {
				ftp.enterLocalPassiveMode();
				Log2.debug("[info] [FtpDownloader] [Set passive mode]");
			} else {
				ftp.enterLocalPassiveMode();
				Log2.debug("[info] [FtpDownloader] [Set active mode]");
			}
		} catch (FtpDownloaderException e) {
			throw e;
		} catch (Exception e) {
			throw new FtpDownloaderException(e.toString());
		}

	}

	public void download(String orignXmlPath, String downloadXmlPath) throws FtpDownloaderException {
		FileOutputStream fos = null;
		boolean isSuccess = false;
		try {

			if (!isConnected()) {
				connect();
			}

			fos = new FileOutputStream(new File(downloadXmlPath));
			isSuccess = ftp.retrieveFile(orignXmlPath, fos);

			if (!isSuccess) {
				String msg = getReplyString(ftp);
				throw new FtpDownloaderException(msg);
			}
		} catch (FtpDownloaderException e) {
			throw e;
		} catch (Exception e) {
			throw new FtpDownloaderException("download error : " + e.toString());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!isSuccess) {
				FileUtil.delete(new File(downloadXmlPath));
			}
		}
	}

	private boolean isConnected() {
		if (ftp.isConnected()) {
			return true;
		}
		return false;
	}

	public void disconnect() throws IOException {
		if (ftp.isConnected()) {
			ftp.disconnect();
		}

	}

	private String getReplyString(FTPClient ftp) {
		String str = ftp.getReplyString();
		str = str.replaceAll("\n", "").replaceAll("\r", "");
		return str;
	}

}
