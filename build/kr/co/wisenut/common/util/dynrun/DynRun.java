package kr.co.wisenut.common.util.dynrun;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2010. 3. 23
 * Time: �삤�썑 3:46:11
 *
 * �룞�쟻 �떎�뻾. �씪�씠釉뚮윭由ъ뿉 �젙�쓽�맂 class 瑜� �샇異�. 留ㅼ냼�뱶 �떎�뻾寃곌낵瑜� 由ы꽩�븳�떎.
 *
 */
public class DynRun {

     static public Object run(String classNameString, String methodNameString) throws Exception {
        return run(classNameString, methodNameString, null, null, null, null);
        //return run(classNameString, methodNameString, new Class[0], null, new Class[0], null);
    }

    /**
     * �젙�쓽�븳 �겢�옒�뒪�깮�꽦 留ㅼ냼�뱶�떎�뻾寃곌낵瑜� 由ы꽩�븳�떎. 媛곴컖 吏��젙�븳 �뙆�씪硫뷀꽣濡� �겢�옒�뒪瑜� �깮�꽦�븯怨�, 留ㅼ냼�뱶瑜� �떎�뻾�븳�떎.
     * @param classNameString   �겢�옒�뒪紐�
     * @param methodNameString 留ㅼ냼�뱶紐�
     * @param paramTypesClassConstructor  �뙆�씪硫뷀꽣�쓽 ���엯 �겢�옒�뒪瑜� �젙�쓽 - �겢�옒�뒪�깮�꽦�옄 �슜
     * @param paramsClassConstructor      �뙆�씪硫뷀꽣瑜� �젙�쓽              - �겢�옒�뒪�깮�꽦�옄 �슜
     * @param paramTypesMethod            �뙆�씪硫뷀꽣�쓽 ���엯 �겢�옒�뒪瑜� �젙�쓽 - 留ㅼ냼�뱶 �슜
     * @param paramsMethod                �뙆�씪硫뷀꽣瑜� �젙�쓽              - 留ㅼ냼�뱶 �슜
     * @return 留ㅼ냼�뱶 寃곌낵瑜� 由ы꽩
     * @throws Exception �삁�쇅泥섎━
     */
    static public Object run(String classNameString, String methodNameString
            , Class[]  paramTypesClassConstructor,  Object[] paramsClassConstructor
            , Class[]  paramTypesMethod , Object[] paramsMethod ) throws Exception {

        Class svcClass = Class.forName( classNameString );
        Object svcObject = svcClass.getConstructor( paramTypesClassConstructor ).newInstance( paramsClassConstructor );
        return svcClass.getMethod(methodNameString, paramTypesMethod).invoke(svcObject, paramsMethod);
    }

}
