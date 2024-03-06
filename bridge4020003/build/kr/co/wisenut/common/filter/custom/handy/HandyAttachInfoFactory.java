/*
 * @(#)HandyAttachInfoFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter.custom.handy;

import kr.co.wisenut.common.logger.Log2;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * HandyAttachInfoFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class HandyAttachInfoFactory {
    public HandyAttachInfo getInstance(String path) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        DocumentBuilder builder;
        Document document = null;

        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(path));
        } catch (ParserConfigurationException e) {
            Log2.error(e);
        } catch (IOException e) {
            Log2.error(e);
        } catch (SAXException e) {
            Log2.error(e);
            //e.printStackTrace();
        }

        HandyAttachInfo handyInfo = new HandyAttachInfo();

        XPathFactory xFactory = XPathFactory.newInstance();
        XPath xpath = xFactory.newXPath();
        //xpath.setNamespaceContext(new PresonalNamespaceContext());
        String expression = "//docInfo/formInfo/formID/text() | //docInfo/formInfo/wordType/text()";
        XPathExpression expr;
        try {
            expr = xpath.compile(expression);
            Object result = expr.evaluate(document, XPathConstants.NODESET);

            NodeList nodes = (NodeList) result;
            if(nodes.getLength() == 0) {
                //System.out.println("Unable to load XPath data("+expression +")");
            }

            for(int i=0; i < nodes.getLength(); i++) {
                if("formID".equals(nodes.item(i).getParentNode().getNodeName())) {
                    handyInfo.setFormID(nodes.item(i).getNodeValue());
                } else if("wordType".equals(nodes.item(i).getParentNode().getNodeName())) {
                    handyInfo.setWordType(nodes.item(i).getNodeValue());
                }
//                System.out.println(i + nodes.item(i).getParentNode().getNodeName() + "> " + nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            Log2.error(e);
            e.printStackTrace();
        }

        // Get attach file infomation
        expression = "//docInfo/objectIDList/objectID[@type=\"泥⑤�\"]/ID/text() | //docInfo/objectIDList/objectID[@type=\"泥⑤�\"]/name/text()";
        try {
            expr = xpath.compile(expression);
            Object result = expr.evaluate(document, XPathConstants.NODESET);

            NodeList nodes = (NodeList) result;
            if(nodes.getLength() == 0) {
                Log2.debug("Unable to load XPath data(" + expression + ")");
            }

            int idx = 0;
            String[][] array = new String[nodes.getLength()/2][2];
            for(int i=0; i < nodes.getLength()/2; i++) {
                idx = i * 2;
                array[i][0] = nodes.item(idx).getNodeValue();
                array[i][1] = nodes.item(idx+1).getNodeValue();
//                System.out.println("getNodeValue()=" + nodes.item(idx).getNodeValue());
//                System.out.println("getNodeValue()=" + nodes.item(idx+1).getNodeValue());
            }
            handyInfo.setAttachArray(array);
        } catch (XPathExpressionException e) {
            Log2.error(e);
            //e.printStackTrace();
        }
        return handyInfo;
    }

    public static void main(String[] args) {
        if(new File(args[0]).exists()) {
            HandyAttachInfoFactory ha = new HandyAttachInfoFactory();
            HandyAttachInfo handy = ha.getInstance(args[0]);
            String[][] attachArray = handy.getAttachArray();
            for(int i=0 ; attachArray != null && i < attachArray.length; i++) {
                System.out.println(attachArray[i][0]);
            }
        } else {
            System.out.println(args[0] + " not found.");
        }

    }
}
