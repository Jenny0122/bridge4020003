package kr.co.wisenut.common.scdreceiver;

import java.util.TimerTask;

public class ProgressDot extends TimerTask{	
	int second = 0;
	
	public void run() {
		if((++second) % 10 == 0) {
			System.out.print("elapsed time...[" + second + "sec]\r");
		}
	}
}
