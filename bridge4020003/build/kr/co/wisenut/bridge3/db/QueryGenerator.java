/*
 * @(#)QueryGenerator.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.bridge3.db;

import kr.co.wisenut.bridge3.config.source.TableSchema;
import kr.co.wisenut.common.util.StringUtil;

/**
 *
 * QueryGenerator
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class QueryGenerator {

    /**
     * query string return method
     * @param query input query string
     * @param findStr find string
     * @param replaceStr replace string
     * @return query string
     */
    public String replaceQuery(String query, String findStr, String replaceStr) {
        return StringUtil.replace(query, findStr, replaceStr);
    }


    /**
     * Using FilterInfo or subquery method
     * @param query  sql query string
     * @param condition sql query where condition
     * @return  Query string
     */
    public String addWhereCondition(String query, String[][] condition) {
        int idx = query.indexOf("%s");
        String value = "";
        String type = "";
        if(idx > -1) {
            for(int i=0; condition != null && i<condition.length  ; i++) {
                value = condition[i][0];
                type  = condition[i][1];
                query = StringUtil.replace(query, "%s"+(i+1), value);
            }
        }
        return query;
    }
}