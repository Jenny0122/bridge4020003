package kr.co.wisenut.common.sf1api.impl;

import QueryAPI500.*;
import kr.co.wisenut.common.sf1api.SF1ApiCommon;

public class SF1ApiCommon500Impl implements SF1ApiCommon 
{
	private Search search = null;
	
	public SF1ApiCommon500Impl()
	{
		this.search = new Search();
	}

	public int setCharSet(String charset) 
	{
		int ret = search.w3SetCodePage(charset);
		
		return ret;
	}

	public int setCommonQuery(String query, int orOperator) 
	{
		int ret = search.w3SetCommonQuery(query, orOperator);
		
		return ret;
	}

	public int setCollection(String collection) 
	{
		int ret = search.w3AddCollection(collection);
		
		return ret;
	}

	public int setQueryAnalyzer(String collection, int la, int ignoreCase,
			int useOriginal, int useSynonym) 
	{
		int ret =search.w3SetQueryAnalyzer(collection, la, ignoreCase, useOriginal, useSynonym);
		
		return ret;
	}

	public int setPageInfo(String collection, int startIdx, int count) 
	{
		int ret = search.w3SetPageInfo(collection, startIdx, count);
		
		return ret;
	}

	public int setSortField(String collection, String field, int order) 
	{
		int ret = search.w3AddSortField(collection, field,0);
		
		return ret;
	}

	public int setSearchField(String collection, String field) 
	{
		int ret = search.w3AddSearchField(collection, field);
		
		return ret;
	}

	public int setExQuery(String collection, String query, int operator) 
	{
		int ret = search.w3SetPrefixQuery(collection, query, operator);
		
		return ret;
	}

	public int setDocumentField(String collection, String field,
			int displaylenth) 
	{
		int ret = search.w3AddDocumentField(collection, field, displaylenth);
		
		return ret;
	}

	public int getConnection(String ip, int port, int timeOut) 
	{
		int ret = search.w3ConnectServer(ip, port, timeOut);
		
		return ret;
	}

	public int getSearchResult(int mode) 
	{
		int ret = search.w3ReceiveSearchQueryResult(mode);
		
		return ret;
	}

	public int getTotalCount(String collection) 
	{
		int totalcount = search.w3GetResultTotalCount(collection);
		
		return totalcount;
	}

	public int getResultCount(String collection) 
	{
		int count = search.w3GetResultCount(collection);
		
		return count;
	}

	public String getField(String collection, String field, int index) 
	{
		String result = search.w3GetField(collection, "DOCID", index);
		
		return result;
	}
	
	public String getErrorInfo()
	{
		return search.w3GetErrorInfo();
	}
}
