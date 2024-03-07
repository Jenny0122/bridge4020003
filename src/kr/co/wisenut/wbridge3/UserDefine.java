package kr.co.wisenut.wbridge3;

import kr.co.wisenut.common.util.html.HTMLParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UserDefine {

    public static void main(String[] args) {
        HTMLParser parser = new HTMLParser();
        //ValidConnection vc = ValidConnection.getInstance();
        String temp = "" ;
        String strResult ="";
        String userStr = "";
        String content = "";
        String urlAddr = "";

        try {
            if(args.length > 0) {
                for(int i=0; i<args.length; i++){
                    if(!args[i].startsWith("-")) {
                        System.out.println("\t[ERROR] Unknow User Define Argument\n");
                        PrintUsage() ;
                        return ;
                    }
                    if(args[i].equalsIgnoreCase("-url")) {
                        if(i + 1 < args.length && !args[i + 1].startsWith("-")){
                            urlAddr = args[i + 1];
                            i++;
                        } else {
                            System.out.println("\n[ERROR] !! User Define < -url > not set ");
                            PrintUsage();
                            return ;
                        }
                    }else if (args[i].equalsIgnoreCase("-define")) {
                        if(i+1 < args.length && !args[i + 1].startsWith("-")) {
                            userStr = args[i + 1] ;
                            i++ ;
                        }else {
                            System.out.println("\n[ERROR] !! User Define < -define > not set ");
                            PrintUsage();
                            return ;
                        }
                    }
                }
            }else{
                PrintUsage();
                return ;
            }

            System.out.println("[User Defined Conf  ] "+ userStr);
            URL objURL = new URL(urlAddr);
            URLConnection ucon = objURL.openConnection();
            StringBuffer pageContents = new StringBuffer();
            String pageLine ="";
            BufferedReader r  =  new BufferedReader(new InputStreamReader(ucon.getInputStream()));
            while ((pageLine = r.readLine()) != null) {
                pageContents.append(pageLine);
                pageContents.append("\r\n");
            }
            content = pageContents.toString();

            if(userStr.indexOf("=") > -1){
                temp = userStr.substring(userStr.indexOf("=")+1, userStr.length() );
            }

            int pos = temp.indexOf(":");
            if(pos > -1){
                try {
                   System.out.println("[User Defined Value is ] " + strResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                System.out.println("User Defined Value Is Empty");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static void PrintUsage(){
        String usage = " Usage : java -classpath bridge381.jar  kr.co.wisenut.bridge3.UserDefine -url <URL Address> -define <ex : writer=td:29>\n";
        System.out.println(usage);
    }
}
