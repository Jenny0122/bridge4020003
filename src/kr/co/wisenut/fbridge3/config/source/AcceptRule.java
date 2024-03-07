/*
 * @(#)AcceptRule.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.*;

/**
 *
 * AcceptRule
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class AcceptRule {
    public static final int MATCH_NONE = 0;
    public static final int MATCH_ALLOW = 1;
    public static final int MATCH_DENY = 2;

    private final int type;
    private final String rule;

    private PatternMatcher matcher;
    private GlobCompiler compiler;
    private Pattern pattern;

    public AcceptRule(int type, String rule) {
        this.type = type;
        this.rule = rule;

        matcher = new Perl5Matcher();
        compiler = new GlobCompiler();

        try {
            pattern = compiler.compile(rule, GlobCompiler.CASE_INSENSITIVE_MASK);
        }
        catch (MalformedPatternException me) {}
    }

    public int match(String str) {
        PatternMatcherInput input = new PatternMatcherInput(str);
        if (matcher.matches(input, pattern)) {
            return type;
        } else {
            return MATCH_NONE;
        }
    }
}