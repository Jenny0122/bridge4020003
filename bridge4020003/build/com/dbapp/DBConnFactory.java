 /*
 * @(#)Config.java   3.8.1 2009/03/11
 *
 */
package com.dbapp;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;
/**
 *
 * DBConnFactory
 * KDB encrypt DBConnection
 * <p>Title: enRise Framework</p>
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DBConnFactory
{
    public DBConnFactory()
    {
        secu_pass[0] = 65;
        secu_pass[1] = 68;
        secu_pass[2] = 77;
        secu_pass[3] = 86;
        secu_pass[4] = 76;
        secu_pass[5] = 68;
        secu_pass[6] = 80;
        secu_pass[7] = 84;
        secu_pass[8] = 77;
        security = "PSSADMIN";
    }
    private native byte[] decrypt(String s);
    private Connection getDefaultConnection()
        throws Exception
    {
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        return DriverManager.getConnection(DB_URL, security, new String(secu_pass));
    }
    public Connection getConnection(String s)
        throws Exception
    {
        int i = -1;
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream("/kdb04/lib/url.ini"));
        }
        catch(Exception exception)
        {
            System.out.println("Can't find url.ini file, check the current directory.");
        }
        String s1;
        if((i = s.indexOf('@')) == -1)
        {
            s1 = s;
            DB_URL = properties.getProperty("Default");
        } else
        {
            s1 = s.substring(0, i);
            DB_URL = properties.getProperty(s.substring(i + 1));
        }
        String s2 = getPassword(s1);
        if(s2 == null || "".equals(s2))
        {
            throw new SQLException("User ID [" + s.substring(0, i).toUpperCase() + "] Not found. User ID\270\246 \310\256\300\316\307\317\274\274\277\344.\nORA-01403: no data found");
        } else
        {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            String s3 = new String(decrypt(s2));
            return DriverManager.getConnection(DB_URL, s3, new String(decrypt(s2)));
        }
    }
    public Connection getConnection(String s, String s1, String s2)
        throws Exception
    {
        String s3 = getPassword(s);
        if(s3 == null || "".equals(s3))
            throw new SQLException("User ID [" + s.toUpperCase() + "] Not found. User ID\270\246 \310\256\300\316\307\317\274\274\277\344.\nORA-01403: no data found");
        Class.forName(s1).newInstance();
        if(buff.containsKey(s))
            return DriverManager.getConnection(s2, s, (String)buff.get(s));
        else
            return DriverManager.getConnection(s2, s, new String(decrypt(s3)));
    }
    private String getPassword(String s)
    {
        Connection connection;
        PreparedStatement preparedstatement;
        Vector vector;
        connection = null;
        preparedstatement = null;
        vector = new Vector();
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = false;
        String s3;
        String s1 = "";
        ResultSet resultset = null;
        try {
            connection = getDefaultConnection();
            preparedstatement = connection.prepareStatement("SELECT PASS FROM PSSADMIN.psmpt WHERE USER_ID='" +
                s.toUpperCase() + "'");
            resultset = preparedstatement.executeQuery();
            int i = 0;
            int j = resultset.getMetaData().getColumnCount();
            while (resultset.next()) {
                String as[] = new String[j];
                for (int k = 0; k < j; k++)
                    as[k] = resultset.getString(k + 1);
                vector.addElement(as);
                i++;
            }
            for (int l = 0; l < vector.size(); l++) {
                String as1[] = (String[]) vector.get(l);
                for (int i1 = 0; i1 < as1.length; i1++)
                    s1 = as1[i1];
            }
            s3 = s1;
            return s3;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (preparedstatement != null)
                    preparedstatement.close();
                if (connection != null)
                    connection.close();
            } catch (Exception exception2) {
                exception2.printStackTrace();
            }
        }
    }
    private static String DB_URL;
    private static String security;
    private static final char DLM = 64;
    private static byte secu_pass[] = new byte[9];
    protected static Hashtable buff;
    static
    {
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("/kdb04/lib/env.ini"));
            System.load(properties.getProperty("libpath"));
            buff = new Hashtable();
        }
        catch(Exception exception)
        {
            System.err.println("error while loading library");
            System.err.println("\t:" + exception.toString());
            System.exit(1);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.out.println("Check the env.ini library path: " + unsatisfiedlinkerror);
        }
    }
}
