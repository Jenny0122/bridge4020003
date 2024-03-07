package kr.co.wisenut.common.sf1api;

public interface SF1ApiCommon 
{
	/**
	 * 臾몄옄 �씤肄붾뵫�쓣 �꽑�젙�븳�떎.
	 * @param charset 罹먮┃�꽣�뀑
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setCharSet(String charset);
	
	/**
	 * 吏덉쓽�뼱瑜� �엯�젰�븳�떎.
	 * @param query 吏덉쓽�뼱
	 * @param orOperator 寃��깋 寃곌낵媛� �뾾�뒗寃쎌슦 AND瑜� OR濡� �솗�옣�븳�떎(0: 誘명솗�옣, 1: �솗�옣)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setCommonQuery(String query, int orOperator);
	
	/**
	 * 寃��깋���긽 而щ젆�뀡�쓣 吏��젙�븳�떎
	 * @param collection 而щ젆�뀡紐�
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setCollection(String collection);
	
	/**
	 * 寃��깋 而щ젆�뀡�쓽 �뼵�뼱遺꾩꽍湲�, ���냼臾몄옄, �궎�썙�뱶 諛� �룞�쓽�뼱 �쟻�슜�뿬遺�瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param la �뼵�뼱遺꾩꽍湲�(1: �쟻�슜, 0: �쟻�슜�븞�븿)
	 * @param ignoreCase ���냼臾몄옄 援щ퀎(1: 援щ텇�븞�븿, 0: 援щ텇�븿)
	 * @param useOriginal 寃��깋 �궎�썙�뱶瑜� �룷�븿�븯�뿬 寃��깋�븷 吏� �뿬遺�(1: �룷�븿, 0: �룷�븿 �븞�븿)
	 * @param useSynonym �룞�쓽�뼱 �솗�옣 �뿬遺�(1: �룞�쓽�뼱 �솗�옣, 0: �솗�옣 �븞�븿)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setQueryAnalyzer(String collection, int la, int ignoreCase, int useOriginal, int useSynonym);
	
	/**
	 * 寃��깋 寃곌낵 媛쒖닔 踰붿쐞瑜� 吏��젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param startIdx 寃��깋 寃곌낵 �떆�옉 �쐞移�
	 * @param count 寃��깋 寃곌낵 由ы꽩 媛��닔
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setPageInfo(String collection, int startIdx, int count);
	
	/**
	 * 寃��깋寃곌낵�뿉 ���븳 �젙�젹�쓣 �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �젙�젹 �븷 �븘�뱶紐�
	 * @param order �삤由꾩감�닚/�궡由ъ감�닚(0: �삤由꾩감�닚, 1: �궡由쇱감�닚)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setSortField(String collection, String field, int order);
	
	/**
	 * 寃��깋 �븘�뱶瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �씤�뜳�뒪�븘�뱶
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setSearchField(String collection, String field);
	
	/**
	 * ExQuery瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param query ExQuery
	 * @param operator 寃��깋 �뿰�궛�옄(1: And, 0: Or) (v5.0 only)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setExQuery(String collection, String query, int operator);
	
	/**
	 * 寃곌낵 �븘�뱶瑜� �꽕�젙�븳�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �떎�걧癒쇳듃�븘�뱶
	 * @param displaylenth 寃��깋 寃곌낵 �븘�뱶 湲몄씠(0: �쟾泥대컲�솚)
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int setDocumentField(String collection, String field, int displaylenth);

	/**
	 * 寃��깋湲곗뿉 �젒�냽�븳�떎.
	 * @param ip 寃��깋湲� IP
	 * @param port 寃��깋湲� Port
	 * @param timeOut 寃��깋湲� Timeout
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int getConnection(String ip, int port, int timeOut);
	
	/**
	 * 寃��깋 寃곌낵瑜� 諛쏆븘�삩�떎.
	 * @param mode 寃��깋 �썑, 寃��깋議곌굔 珥덇린�솕 �뿬遺� 諛� 寃��깋湲� �뿰寃� �쑀吏� �뿬遺�
	 * 		  0 : �룞�씪�븳 寃��깋 議곌굔�쑝濡� �옱 吏덉쓽 �닔�뻾�븷 寃쎌슦瑜� �쐞�빐 �뿰寃� �쑀吏�
	 * 		  1 : �궗�슜�븯吏� �븡�쓬
	 * 		  2 : 寃��깋 �썑 吏덉쓽 �젙蹂� 珥덇린�솕.
	 * 		  3 : 寃��깋湲� �냼耳볦쓣 �떕�뒗�떎.
	 * @return �꽦怨� 0, �떎�뙣 -1
	 */
	int getSearchResult(int mode);
	
	/**
	 * 寃��깋 寃곌낵 珥� 媛쒖닔瑜� �굹���궦�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @return 珥� 寃곌낵媛쒖닔
	 */
	int getTotalCount(String collection);
	
	/**
	 * Page�꽕�젙�뿉 �쟻�슜�맂 寃��깋 寃곌낵 媛쒖닔瑜� �굹���궦�떎. 
	 * @param collection 而щ젆�뀡紐�
	 * @return 寃곌낵媛쒖닔
	 */
	int getResultCount(String collection);
	
	/**
	 * 寃��깋 寃곌낵 �븘�뱶�쓽 媛믪쓣 媛��졇�삩�떎.
	 * @param collection 而щ젆�뀡紐�
	 * @param field �븘�뱶紐�
	 * @param index �젅肄붾뱶 �씤�뜳�뒪
	 * @return 寃곌낵 媛�
	 */
	String getField(String collection, String field, int index);
	
	String getErrorInfo();
}
