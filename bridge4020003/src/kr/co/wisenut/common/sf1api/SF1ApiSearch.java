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
	 * 문자 인코딩을 선정한다.
	 * @param charset 캐릭터셋
	 * @return 성공 0, 실패 -1
	 */
	public int setCharSet(String charset)
	{
		return this.common.setCharSet(charset);
	}
	/**
	 * 질의어를 입력한다.
	 * @param query 질의어
	 * @param orOperator 검색 결과가 없는경우 AND를 OR로 확장한다(0: 미확장, 1: 확장)
	 * @return 성공 0, 실패 -1
	 */
	public int setCommonQuery(String query, int orOperator)
	{
		return this.common.setCommonQuery(query, orOperator);
	}
	/**
	 * 검색대상 컬렉션을 지정한다
	 * @param collection 컬렉션명
	 * @return 성공 0, 실패 -1
	 */
	public int setCollection(String collection)
	{
		return this.common.setCollection(collection);
	}
	/**
	 * 검색 컬렉션의 언어분석기, 대소문자, 키워드 및 동의어 적용여부를 설정한다.
	 * @param collection 컬렉션명
	 * @param la 언어분석기(1: 적용, 0: 적용안함)
	 * @param ignoreCase 대소문자 구별(1: 구분안함, 0: 구분함)
	 * @param useOriginal 검색 키워드를 포함하여 검색할 지 여부(1: 포함, 0: 포함 안함)
	 * @param useSynonym 동의어 확장 여부(1: 동의어 확장, 0: 확장 안함)
	 * @return 성공 0, 실패 -1
	 */
	public int setQueryAnalyzer(String collection, int la, int ignoreCase, int useOriginal, int useSynonym)
	{
		return this.common.setQueryAnalyzer(collection, la, ignoreCase, useOriginal, useSynonym);
	}
	/**
	 * 검색 결과 개수 범위를 지정한다.
	 * @param collection 컬렉션명
	 * @param startIdx 검색 결과 시작 위치
	 * @param count 검색 결과 리턴 갯수
	 * @return 성공 0, 실패 -1
	 */
	public int setPageInfo(String collection, int startIdx, int count)
	{
		return this.common.setPageInfo(collection, startIdx, count);
	}
	/**
	 * 검색결과에 대한 정렬을 설정한다.
	 * @param collection 컬렉션명
	 * @param field 정렬 할 필드명
	 * @param order 오름차순/내리차순(0: 오름차순, 1: 내림차순)
	 * @return 성공 0, 실패 -1
	 */
	public int setSortField(String collection, String field, int order)
	{
		return this.common.setSortField(collection, field, order);
	}
	/**
	 * 검색 필드를 설정한다.
	 * @param collection 컬렉션명
	 * @param field 인덱스필드
	 * @return 성공 0, 실패 -1
	 */
	public int setSearchField(String collection, String field)
	{
		return this.common.setSearchField(collection, field);
	}
	/**
	 * ExQuery를 설정한다.
	 * @param collection 컬렉션명
	 * @param query ExQuery
	 * @param operator 검색 연산자(1: And, 0: Or) (v5.0 only)
	 * @return 성공 0, 실패 -1
	 */
	public int setExQuery(String collection, String query, int operator)
	{
		return this.common.setExQuery(collection, query, operator);
	}
	/**
	 * 결과 필드를 설정한다.
	 * @param collection 컬렉션명
	 * @param field 다큐먼트필드
	 * @param displaylenth 검색 결과 필드 길이(0: 전체반환)
	 * @return 성공 0, 실패 -1
	 */
	public int setDocumentField(String collection, String field, int displaylenth)
	{
		return this.common.setDocumentField(collection, field, displaylenth);
	}
	/**
	 * 검색기에 접속한다.
	 * @param ip 검색기 IP
	 * @param port 검색기 Port
	 * @param timeOut 검색기 Timeout
	 * @return 성공 0, 실패 -1
	 */
	public int getConnection(String ip, int port, int timeOut)
	{
		return this.common.getConnection(ip, port, timeOut);
	}
	/**
	 * 검색 결과를 받아온다.
	 * @param mode 검색 후, 검색조건 초기화 여부 및 검색기 연결 유지 여부
	 * 		  0 : 동일한 검색 조건으로 재 질의 수행할 경우를 위해 연결 유지
	 * 		  1 : 사용하지 않음
	 * 		  2 : 검색 후 질의 정보 초기화.
	 * 		  3 : 검색기 소켓을 닫는다.
	 * @return 성공 0, 실패 -1
	 */
	public int getSearchResult(int mode)
	{
		return this.common.getSearchResult(mode);
	}
	/**
	 * 검색 결과 총 개수를 나타낸다.
	 * @param collection 컬렉션명
	 * @return 총 결과개수
	 */
	public int getTotalCount(String collection)
	{
		return this.common.getTotalCount(collection);
	}
	/**
	 * Page설정에 적용된 검색 결과 개수를 나타낸다. 
	 * @param collection 컬렉션명
	 * @return 결과개수
	 */
	public int getResultCount(String collection)
	{
		return this.common.getResultCount(collection);
	}
	/**
	 * 검색 결과 필드의 값을 가져온다.
	 * @param collection 컬렉션명
	 * @param field 필드명
	 * @param index 레코드 인덱스
	 * @return 결과 값
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
	 * 검색 엔진 버전별 인스턴스를 전달한다.
	 * @param sf1Version SF-1 버전
	 * @return SF1ApiCommon
	 */
	private SF1ApiCommon getInstance(String sf1Version)
	{
		if(sf1Version.equals(SF1Constants.API_VER450))
		{
			//TODO: 4.5.0 생성
		}
		else if(sf1Version.equals(SF1Constants.API_VER451))
		{
			common = new SF1ApiCommon451Impl();
			
			return common;
		}
		else if(sf1Version.equals(SF1Constants.API_VER452))
		{
			//TODO: 4.5.2 생성
		}
		else if(sf1Version.equals(SF1Constants.API_VER453))
		{
			//TODO: 4.5.3 생성
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
