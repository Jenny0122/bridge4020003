/*
 * @(#)WNCommon.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common;

/**
 *
 * WNCommon
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public final class WNCommon implements ApplicationInfo{

    public static void main(String[] args) {
        printStartMessage();
    }

    private static String getStartMessage(){
        String print = "\n"+SERVER_DESCRIPTION_NAME+" "+SERVER_DESCRIPTION_VERSION+", "+SERVER_DESCRIPTION_DATETIME+"\n";
        print +=  SERVER_DESCRIPTION_COPYRIGHT+" \n";
        return print;
    }

    private static void printStartMessage() {
        System.out.println(getStartMessage() );
    }
}
