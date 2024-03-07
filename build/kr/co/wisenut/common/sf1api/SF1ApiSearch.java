package kr.co.wisenut.common.sf1api;

import kr.co.wisenut.common.constants.SF1Constants;
import kr.co.wisenut.common.sf1api.impl.SF1ApiCommon451Impl;
import kr.co.wisenut.common.sf1api.impl.SF1ApiCommon500Impl;
import kr.co.wisenut.common.sf1api.impl.SF1ApiCommon530Impl;
import kr.co.wisenut.common.sf1api.impl.SF1ApiCommon600Impl;

public class SF1ApiSearch 
{
	private SF1ApiCommon common;
	
	public SF1ApiSearch(String sf1Version)
	{
		this.common = getInstance(sf1Version);
	}
	
	/**
	 * 臾몄옄 �씤肄붾뵫�쓣 �꽑�젙�븳�떎.
	 * @param charset 罹먮┃�꽣�뀑
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setCharSet(String charset)
	{
		return this.common.setCharSet(charset);
	}
	/**
	 * 吏덉쓽�뼱瑜� �엯�젰�븳�떎.
	 * @param query 吏덉쓽�뼱
	 * @param orOperator 寃��깋 寃곌낵媛� �뾾�뒗寃쎌슦 AND瑜� OR濡� �솗�옣�븳�떎(0: 誘명솗�옣, 1: �솗�옣)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setCommonQuery(String query, int orOperator)
	{
		return this.common.setCommonQuery(query, orOperator);
	}
	/**
	 * 寃��깋���긽 而щ젆�뀡�쓣 吏��젙�븳�떎
	 * @param collection 而щ젆�뀡紐�
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setCollection(String collection)
	{
		return this.common.setCollection(collection);
	}
	/**
	 * 寃��깋 而щ젆�뀡�쓽 �뼵�뼱遺꾩꽍湲�, ���냼臾몄옄, �궎�썙�뱶 諛� �룞�쓽�뼱 �쟻�슜�뿬遺�瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param la �뼵�뼱遺꾩꽍湲�(1: �쟻�슜, 0: �쟻�슜�븞�븿)
	 * @param ignoreCase ���냼臾몄옄 援щ퀎(1: 援щ텇�븞�븿, 0: 援щ텇�븿)
	 * @param useOriginal 寃��깋 �궎�썙�뱶瑜� �룷�븿�븯�뿬 寃��깋�븷 吏� �뿬遺�(1: �룷�븿, 0: �룷�븿 �븞�븿)
	 * @param useSynonym �룞�쓽�뼱 �솗�옣 �뿬遺�(1: �룞�쓽�뼱 �솗�옣, 0: �솗�옣 �븞�븿)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setQueryAnalyzer(String collection, int la, int ignoreCase, int useOriginal, int useSynonym)
	{
		return this.common.setQueryAnalyzer(collection, la, ignoreCase, useOriginal, useSynonym);
	}
	/**
	 * 寃��깋 寃곌낵 媛쒖닔 踰붿쐞瑜� 吏��젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param startIdx 寃��깋 寃곌낵 �떆�옉 �쐞移�
	 * @param count 寃��깋 寃곌낵 由ы꽩 媛��닔
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setPageInfo(String collection, int startIdx, int count)
	{
		return this.common.setPageInfo(collection, startIdx, count);
	}
	/**
	 * 寃��깋寃곌낵�뿉 ���븳 �젙�젹�쓣 �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �젙�젹 �븷 �븘�뱶紐�
	 * @param order �삤由꾩감�닚/�궡由ъ감�닚(0: �삤由꾩감�닚, 1: �궡由쇱감�닚)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setSortField(String collection, String field, int order)
	{
		return this.common.setSortField(collection, field, order);
	}
	/**
	 * 寃��깋 �븘�뱶瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �씤�뜳�뒪�븘�뱶
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setSearchField(String collection, String field)
	{
		return this.common.setSearchField(collection, field);
	}
	/**
	 * ExQuery瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param query ExQuery
	 * @param operator 寃��깋 �뿰�궛�옄(1: And, 0: Or) (v5.0 only)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setExQuery(String collection, String query, int operator)
	{
		return this.common.setExQuery(collection, query, operator);
	}
	/**
	 * 寃곌낵 �븘�뱶瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �떎�걧癒쇳듃�븘�뱶
	 * @param displaylenth 寃��깋 寃곌낵 �븘�뱶 湲몄씠(0: �쟾泥대컲�솚)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int setDocumentField(String collection, String field, int displaylenth)
	{
		return this.common.setDocumentField(collection, field, displaylenth);
	}
	/**
	 * 寃��깋湲곗뿉 �젒�냽�븳�떎.
	 * @param ip 寃��깋湲� IP
	 * @param port 寃��깋湲� Port
	 * @param timeOut 寃��깋湲� Timeout
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int getConnection(String ip, int port, int timeOut)
	{
		return this.common.getConnection(ip, port, timeOut);
	}
	/**
	 * 寃��깋 寃곌낵瑜� 諛쏆븘�삩�떎.
	 * @param mode 寃��깋 �썑, 寃��깋議곌굔 珥덇린�솕 �뿬遺� 諛� 寃��깋湲� �뿰寃� �쑀吏� �뿬遺�
	 * 		  0 : �룞�씪�븳 寃��깋 議곌굔�쑝濡� �옱 吏덉쓽 �닔�뻾�븷 寃쎌슦瑜� �쐞�빐 �뿰寃� �쑀吏�
	 * 		  1 : �궗�슜�븯吏� �븡�쓬
	 * 		  2 : 寃��깋 �썑 吏덉쓽 �젙蹂� 珥덇린�솕.
	 * 		  3 : 寃��깋湲� �냼耳볦쓣 �떕�뒗�떎.
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	public int getSearchResult(int mode)
	{
		return this.common.getSearchResult(mode);
	}
	/**
	 * 寃��깋 寃곌낵 珥� 媛쒖닔瑜� �굹���궦�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @return 珥� 寃곌낵媛쒖닔
	 */
	public int getTotalCount(String collection)
	{
		return this.common.getTotalCount(collection);
	}
	/**
	 * Page�꽕�젙�뿉 �쟻�슜�맂 寃��깋 寃곌낵 媛쒖닔瑜� �굹���궦�떎. 
	 * @param collection 而щ젆�뀡紐�
	 * @return 寃곌낵媛쒖닔
	 */
	public int getResultCount(String collection)
	{
		return this.common.getResultCount(collection);
	}
	/**
	 * 寃��깋 寃곌낵 �븘�뱶�쓽 媛믪쓣 媛��졇�삩�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �븘�뱶紐�
	 * @param index �젅肄붾뱶 �씤�뜳�뒪
	 * @return 寃곌낵 媛�
	 */
	public String getField(String collection, String field, int index)
	{
		return this.common.getField(collection, field, index);
	}
	
	public String getErrorInfo()
	{
		return this.common.getErrorInfo();
	}
	
	/**
	 * 寃��깋 �뿏吏� 踰꾩쟾蹂� �씤�뒪�꽩�뒪瑜� �쟾�떖�븳�떎.
	 * @param sf1Version SF-1 踰꾩쟾
	 * @return SF1ApiCommon
	 */
	private SF1ApiCommon getInstance(String sf1Version)
	{
		if(sf1Version.equals(SF1Constants.API_VER450))
		{
			//TODO: 4.5.0 �깮�꽦
		}
		else if(sf1Version.equals(SF1Constants.API_VER451))
		{
			common = new SF1ApiCommon451Impl();
			
			return common;
		}
		else if(sf1Version.equals(SF1Constants.API_VER452))
		{
			//TODO: 4.5.2 �깮�꽦
		}
		else if(sf1Version.equals(SF1Constants.API_VER453))
		{
			//TODO: 4.5.3 �깮�꽦
		}
		else if(sf1Version.equals(SF1Constants.API_VER500))
		{
			common = new SF1ApiCommon500Impl();
			
			return common;
		}
		else if(sf1Version.equals(SF1Constants.API_VER530))
		{
			common = new SF1ApiCommon530Impl();
			
			return common;
		}
		else if(sf1Version.equals(SF1Constants.API_VER600))
		{
			common = new SF1ApiCommon600Impl();
			
			return common;
		}
		
		return null;
	}
	
}
