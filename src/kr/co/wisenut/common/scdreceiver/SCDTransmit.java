package kr.co.wisenut.common.scdreceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.io.ExtFileNameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2010. 6. 14
 * Time: 오후 3:37:34
 * To change this template use File | Settings | File Templates.
 */
public class SCDTransmit {
	private String host;
	private int port;
	private String rcvMode;
	FClient client = null;

	public SCDTransmit(String host, int port, String rcvMode) {
		this.host = host;
		this.port = port;
		this.rcvMode = rcvMode;
	}

	public boolean connect() {
		boolean isRet = true;
		try {
			client = new FClient(host, port);
		} catch (Exception e) {
			isRet = false;
			Log2.error("[SCDTransmit ] [Error: SCDReceiver connection refused. HOST(" + host + ") PORT(" + port + ") ]");
		}
		return isRet;
	}

	public void close() {
		try {
			client.close();
		} catch (Exception e) {
			Log2.error("[SCDTransmit ] [Error: Connection close Error. " + e.toString() + "]");
		}
	}

	public void sendFiles(File[] fileList, String remoteDir, String backupDir, boolean isDeleteSCD) throws IOException {
		boolean isError = false;
		int bytesRead = 0;
		int bufferSize = 1024 * 1024;
		int maxBufferSize = 1024 * 1024;
		int bytesAvailable = 0;
		byte[] buffer = null;
		FileInputStream fileInputStream = null;
		
		boolean memblock = false;
		
		//-Dreceiver.blocksize=4
		if ( System.getProperty("receiver.blocksize") != null )
		{
			memblock = true;
			bufferSize = Integer.parseInt( System.getProperty("receiver.blocksize") ) * 1024;
			maxBufferSize = Integer.parseInt( System.getProperty("receiver.blocksize") ) * 1024;
			
			Log2.debug( "[SCDTransmit] Memblock Mode" );
		}

		Log2.debug("[SCDTransmit ] [SCD file send count: " + fileList.length + "]");
		String remoteDirTmp = FileUtil.checkDirPath(remoteDir);
		for (int i = 0; i < fileList.length; i++) {
			client.command(remoteDirTmp + getName(fileList[i].getName()));
			fileInputStream = new FileInputStream(fileList[i]);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			
			Timer timer = new Timer();
            ProgressDot dot = new ProgressDot();
            timer.schedule(dot, new Date(), 1000);
            
            if( memblock )
            {
            	buffer = new byte[bufferSize];
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (!isError && bytesRead > 0) {
					bytesAvailable = fileInputStream.available();
					if (bytesAvailable == 0) {
						client.send(FSListener.CMD_WRITE_NONE_EOF);
					} else {
						client.send(FSListener.CMD_WRITE_NONE_EOF);
					}
					client.writeBuffer(buffer);
					// received result data
					String msg = client.receive();
					// if(client.receive().equals(FSListener.CMD_WRITE_EXP)) {
					if (msg.equals(FSListener.CMD_WRITE_EXP)) {
						isError = true;
					}

					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					if (maxBufferSize > bufferSize) {
						buffer = new byte[bufferSize];
					}
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				
				client.send(FSListener.CMD_WRITE_EOF);
            }
            else if (rcvMode.equals("line")) {
				// TODO: Line Receive
				InputStream input = (InputStream) fileInputStream;
				Scanner sc = new Scanner(input);

				while (true) {
					if (sc.hasNextLine()) {
						client.send(FSListener.CMD_WRITE_NONE_EOF);

						String str = sc.nextLine() + "\n";
						byte[] sendByte = str.getBytes();

						client.writeBuffer(sendByte);
						// received result data
						String msg = client.receive();
						// if(client.receive().equals(FSListener.CMD_WRITE_EXP)) {
						if (msg.equals(FSListener.CMD_WRITE_EXP)) {
							isError = true;
						}

					} else {
						client.send(FSListener.CMD_WRITE_EOF);

						break;
					}
				}
			} else {
				buffer = new byte[bufferSize];
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (!isError && bytesRead > 0) {
					bytesAvailable = fileInputStream.available();
					if (bytesAvailable == 0) {
						client.send(FSListener.CMD_WRITE_NONE_EOF);// 2020.05 bug fix
					} else {
						client.send(FSListener.CMD_WRITE_NONE_EOF);
					}
					client.writeBuffer(buffer);
					// received result data
					String msg = client.receive();
					// if(client.receive().equals(FSListener.CMD_WRITE_EXP)) {
					if (msg.equals(FSListener.CMD_WRITE_EXP)) {
						isError = true;
					}

					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					if (maxBufferSize > bufferSize) {
						buffer = new byte[bufferSize];
					}
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				client.send(FSListener.CMD_WRITE_EOF);//ksg 2020.05 bug fix

			}

			fileInputStream.close();

			if (isDeleteSCD) {
				fileList[i].delete();
			} else {
				// backup 할 디랙토리가 존재하는지 확인하여 없으면 생성한다
				FileUtil.makeDir(backupDir);

				// 전송이 완료된 SCD 파일을 backup 디랙토리로 이동한다
				fileList[i].renameTo(new File(backupDir, fileList[i].getName()));
			}

		}
	}

	private String getName(String name) {
		return name.substring(0, name.lastIndexOf("C.SCD")) + "F.SCD";
	}

	/**
	 * Send to SCD
	 * @param scdDir
	 *            file path
	 * @param remotePath
	 *            file path
	 * @param backupDir
	 *            file path
	 */
	public void sendSCD(String scdDir, String remotePath, String backupDir, boolean isDeleteSCD) throws IOException {
		Log2.debug("[SCDTransmit ] [Send SCD: connect server ready]", 3);
		if (connect()) {
			File[] fileList = new File(scdDir).listFiles(new ExtFileNameFilter("c.scd"));
			sendFiles(fileList, remotePath, backupDir, isDeleteSCD);
			Log2.debug("[SCDTransmit ] [Send SCD: Finished]", 3);
			close();
		}
	}
}