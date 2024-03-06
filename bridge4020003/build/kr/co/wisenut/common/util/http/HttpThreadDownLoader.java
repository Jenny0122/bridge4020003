package kr.co.wisenut.common.util.http;

import kr.co.wisenut.common.logger.Log2;
import org.apache.http.*;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009. 8. 21
 * To change this template use File | Settings | File Templates.
 */
public class HttpThreadDownLoader extends Thread {
    private String url;
    private String distFileName;
    Thread threadToInterrupt = null;

    public HttpThreadDownLoader(String url, String distFileName) {
        this.url = url;
        this.distFileName = distFileName;
        threadToInterrupt = Thread.currentThread();
        setDaemon(true);
    }

    public void run() {
        saveHttpContent(this.url, this.distFileName);
//        threadToInterrupt.interrupt();
    }

    public int saveHttpContent(String url, String distFileName) {
        int ret = 0;
        URIInfo uriInfo = URLUtil.getUriInfo(url);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        BasicHttpProcessor httpproc = new BasicHttpProcessor();

        // Required protocol interceptors
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());

        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(uriInfo.getDomain(), uriInfo.getPort());

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
//        ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            String[] targets = {uriInfo.getPrarameter()};

            for (int i = 0; i < targets.length; i++) {
                if (!conn.isOpen()) {
                    Socket socket = new Socket(host.getHostName(), host.getPort());
                    conn.bind(socket, params);
                }
                BasicHttpRequest request = new BasicHttpRequest("GET", targets[i]);
                Log2.debug("[HttpDownLoader ] [Target URL=" + request.getRequestLine().getUri() + "]", 3);

                request.setParams(params);
                httpexecutor.preProcess(request, httpproc, context);
                HttpResponse response = httpexecutor.execute(request, conn, context);
                response.setParams(params);
                httpexecutor.postProcess(response, httpproc, context);

                Log2.debug("[HttpDownLoader ] [Response : " + response.getStatusLine() + "]", 3);
                HttpEntity httpEntity = response.getEntity();
//                String contentCharSet = EntityUtils.getContentCharSet(httpEntity);

                File distFile = new File(distFileName);
                FileOutputStream fos = new FileOutputStream(distFile);
                fos.write(EntityUtils.toByteArray(httpEntity));
                fos.flush();
                fos.close();

                Log2.debug("[HttpDownLoader ] [Save File Success!! : " + distFile + "]", 3);
                conn.close();
            }
        } catch (UnknownHostException e) {
            ret = -1;
            Log2.error("[HttpDownLoader ] [UnknownHostException!!! " + e.getMessage() + "]");
        } catch (FileNotFoundException e) {
            ret = -1;
            Log2.error("[HttpDownLoader ] [FileNotFoundException!!! " + e.getMessage() + "]");
        } catch (IOException e) {
            ret = -1;
            Log2.error("[HttpDownLoader ] [IOException!!! " + e.getMessage() + "]");
        } catch (HttpException e) {
            ret = -1;
            Log2.error("[HttpDownLoader ] [HttpException!!! " + e.getMessage() + "]");
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                ret = -1;
                Log2.error("[HttpDownLoader ] [IOException!!! " + e.getMessage() + "]");
            }
        }
        return ret;
    }
}