/*
 * @(#)ThumbNailThread.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job;

import com.sun.jimi.core.JimiUtils;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import java.awt.*;
import kr.co.wisenut.common.logger.Log2;

/**
 *
 * ThumbNailThread  - Create thumbnail image class 
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class ThumbNailThread extends Thread {
    private String orgFileName;
    private  int width;
    private  int height;
    private  String convertFileName;

    public ThumbNailThread(String orgFileName, int width, int height, String convertFileName) {
        this.orgFileName = orgFileName;
        this.width = width;
        this.height = height;
        this.convertFileName = convertFileName;
    }

    public void run() {
        // convert image to thumbnail
        Image _image= JimiUtils.getThumbnail(orgFileName, width, height, Jimi.SYNCHRONOUS | Jimi.IN_MEMORY);
        try {
            Jimi.putImage(_image, convertFileName);
        } catch (JimiException e) {
            Log2.error("[image convert error][" + orgFileName + ", "+convertFileName+"]");
        }
        if(_image != null) _image.flush();
    }
}
