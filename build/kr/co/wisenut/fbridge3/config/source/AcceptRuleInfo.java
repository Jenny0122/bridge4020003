/*
 * @(#)AcceptRuleInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

import kr.co.wisenut.fbridge3.config.source.AcceptRule;

import java.util.Vector;

/**
 *
 * AcceptRuleInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class AcceptRuleInfo {
    private final Vector vectRule;

    public AcceptRuleInfo() {
        vectRule = new Vector();
    }

    public void addRule(int type, String rule) {
        vectRule.add(new AcceptRule(type, rule));
    }

    public void addRule(AcceptRule rule) {
        vectRule.add(rule);
    }

    public boolean isAllow(String input) {
        for (int i=0; i < vectRule.size(); i++) {
            AcceptRule rule = (AcceptRule)vectRule.get(i);
            //System.out.println("<< " + input);
            int ret = rule.match(input);
            if (ret == AcceptRule.MATCH_ALLOW) {
                return true;
            } else if (ret == AcceptRule.MATCH_DENY) {
                return false;
            } else if (ret == AcceptRule.MATCH_NONE) {
                if(i != (vectRule.size()-1)) continue;
                return false;
            }
        }
        return true;
    }
}
