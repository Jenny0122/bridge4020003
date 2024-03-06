package kr.co.wisenut.common.sf1api;

public interface SF1ApiCommon 
{
	/**
	 * 문자 인코딩을 선정한다.
	 * @param charset 캐릭터셋
	 * @return 성공 0, 실패 -1
	 */
	int setCharSet(String charset);
	
	/**
	 * 질의어를 입력한다.
	 * @param query 질의어
	 * @param orOperator 검색 결과가 없는경우 AND를 OR로 확장한다(0: 미확장, 1: 확장)
	 * @return 성공 0, 실패 -1
	 */
	int setCommonQuery(String query, int orOperator);
	
	/**
	 * 검색대상 컬렉션을 지정한다
	 * @param collection 컬렉션명
	 * @return 성공 0, 실패 -1
	 */
	int setCollection(String collection);
	
	/**
	 * 검색 컬렉션의 언어분석기, 대소문자, 키워드 및 동의어 적용여부를 설정한다.
	 * @param collection 컬렉션명
	 * @param la 언어분석기(1: 적용, 0: 적용안함)
	 * @param ignoreCase 대소문자 구별(1: 구분안함, 0: 구분함)
	 * @param useOriginal 검색 키워드를 포함하여 검색할 지 여부(1: 포함, 0: 포함 안함)
	 * @param useSynonym 동의어 확장 여부(1: 동의어 확장, 0: 확장 안함)
	 * @return 성공 0, 실패 -1
	 */
	int setQueryAnalyzer(String collection, int la, int ignoreCase, int useOriginal, int useSynonym);
	
	/**
	 * 검색 결과 개수 범위를 지정한다.
	 * @param collection 컬렉션명
	 * @param startIdx 검색 결과 시작 위치
	 * @param count 검색 결과 리턴 갯수
	 * @return 성공 0, 실패 -1
	 */
	int setPageInfo(String collection, int startIdx, int count);
	
	/**
	 * 검색결과에 대한 정렬을 설정한다.
	 * @param collection 컬렉션명
	 * @param field 정렬 할 필드명
	 * @param order 오름차순/내리차순(0: 오름차순, 1: 내림차순)
	 * @return 성공 0, 실패 -1
	 */
	int setSortField(String collection, String field, int order);
	
	/**
	 * 검색 필드를 설정한다.
	 * @param collection 컬렉션명
	 * @param field 인덱스필드
	 * @return 성공 0, 실패 -1
	 */
	int setSearchField(String collection, String field);
	
	/**
	 * ExQuery를 설정한다.
	 * @param collection 컬렉션명
	 * @param query ExQuery
	 * @param operator 검색 연산자(1: And, 0: Or) (v5.0 only)
	 * @return 성공 0, 실패 -1
	 */
	int setExQuery(String collection, String query, int operator);
	
	/**
	 * 결과 필드를 설정한다.
	 * @param collection 컬렉션명
	 * @param field 다큐먼트필드
	 * @param displaylenth 검색 결과 필드 길이(0: 전체반환)
	 * @return 성공 0, 실패 -1
	 */
	int setDocumentField(String collection, String field, int displaylenth);

	/**
	 * 검색기에 접속한다.
	 * @param ip 검색기 IP
	 * @param port 검색기 Port
	 * @param timeOut 검색기 Timeout
	 * @return 성공 0, 실패 -1
	 */
	int getConnection(String ip, int port, int timeOut);
	
	/**
	 * 검색 결과를 받아온다.
	 * @param mode 검색 후, 검색조건 초기화 여부 및 검색기 연결 유지 여부
	 * 		  0 : 동일한 검색 조건으로 재 질의 수행할 경우를 위해 연결 유지
	 * 		  1 : 사용하지 않음
	 * 		  2 : 검색 후 질의 정보 초기화.
	 * 		  3 : 검색기 소켓을 닫는다.
	 * @return 성공 0, 실패 -1
	 */
	int getSearchResult(int mode);
	
	/**
	 * 검색 결과 총 개수를 나타낸다.
	 * @param collection 컬렉션명
	 * @return 총 결과개수
	 */
	int getTotalCount(String collection);
	
	/**
	 * Page설정에 적용된 검색 결과 개수를 나타낸다. 
	 * @param collection 컬렉션명
	 * @return 결과개수
	 */
	int getResultCount(String collection);
	
	/**
	 * 검색 결과 필드의 값을 가져온다.
	 * @param collection 컬렉션명
	 * @param field 필드명
	 * @param index 레코드 인덱스
	 * @return 결과 값
	 */
	String getField(String collection, String field, int index);
	
	String getErrorInfo();
}
