package com.github.ipaas.ideploy.plugin.core;

import java.io.File;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;

public class RequestAcion {

	private static String HTTPS_PROTOCOL = "https://";
	private static String HTTP_PROTOCOL = "http://";

	/**
	 * Get请求
	 * 
	 * @param url
	 * @param params
	 * @param useHttps
	 *            是否使用https协议
	 * 
	 * @return
	 */
	public static String get(String url, List<NameValuePair> params, boolean useHttps) {
		String body = null;
		try {
			// Get请求
			HttpGet httpget = useHttps ? new HttpGet(HTTPS_PROTOCOL + url) : new HttpGet(HTTP_PROTOCOL + url);
			// 设置参数
			String str = EntityUtils.toString(new UrlEncodedFormEntity(params, "UTF-8"));
			httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));

			HttpClient hc = new DefaultHttpClient();

			// 发送请求
			HttpResponse httpresponse = hc.execute(httpget);
			// 获取返回数据
			HttpEntity entity = httpresponse.getEntity();
			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (Exception e) {
			ConsoleHandler.error("请求异常:" + e.getMessage());
		}
		return body;
	}

	/**
	 * // Post请求
	 * 
	 * @param url
	 * @param params
	 * @param useHttps
	 * @param useHttps
	 *            是否使用http协议
	 * @return
	 */
	public static String post(String url, List<NameValuePair> params, boolean useHttps) {
		String body = null;
		try {
			// Post请求
			HttpPost httppost = useHttps ? new HttpPost(HTTPS_PROTOCOL + url) : new HttpPost(HTTP_PROTOCOL + url);

			// 设置参数
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpClient hc = new DefaultHttpClient();
			if (useHttps) {
				hc = RequestAcion.wrapClient(hc);
			}

			// 发送请求
			HttpResponse httpresponse = hc.execute(httppost);
			// 获取返回数据
			HttpEntity entity = httpresponse.getEntity();

			body = EntityUtils.toString(entity);
			EntityUtils.consume(entity);

		} catch (Exception e) {
			e.printStackTrace();
			ConsoleHandler.error("请求异常:" + e.getMessage());
		}
		return body;
	}

	/**
	 * 使用https, 指定DefaultHttpClient忽略证书认证错误，继续进行https交互
	 * 
	 * @param base
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static HttpClient wrapClient(HttpClient base) {
		// try {
		// SSLContext ctx = SSLContext.getInstance("TLS");
		// X509TrustManager tm = new X509TrustManager() {
		//
		// public void checkServerTrusted(X509Certificate[] chain, String
		// authType) throws CertificateException {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public X509Certificate[] getAcceptedIssuers() {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public void checkClientTrusted(java.security.cert.X509Certificate[]
		// arg0, String arg1)
		// throws java.security.cert.CertificateException {
		// // TODO Auto-generated method stub
		//
		// }
		// };
		// ctx.init(null, new TrustManager[] { tm }, null);
		// SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		// ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		// ClientConnectionManager ccm = base.getConnectionManager();
		// SchemeRegistry sr = ccm.getSchemeRegistry();
		// // 设置要使用的端口，默认是443
		// sr.register(new Scheme("https", 443, ssf));
		//
		// return new DefaultHttpClient(ccm, base.getParams());
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// return null;
		// }

		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
			return new DefaultHttpClient(mgr, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param userInfo
	 * @param filePath
	 * @param appRowId
	 * @param useHttps
	 * @return
	 * @throws Exception
	 */
	public static String uploadFile(String url, UserInfo userInfo, String filePath, Integer appRowId, boolean useHttps)
			throws Exception {
		File uploadFile = new File(filePath);
		String resultContent = null;
		if (!uploadFile.exists() || uploadFile.isDirectory()) {
			ConsoleHandler.error("文件不存在!");
			return resultContent;
		}
		HttpClient httpclient = new DefaultHttpClient();
		if (useHttps) {
			httpclient = RequestAcion.wrapClient(httpclient);
		}
		// 请求处理页面
		HttpPost httppost = useHttps ? new HttpPost(HTTPS_PROTOCOL + url) : new HttpPost(HTTP_PROTOCOL + url);
		// 创建待处理的文件
		FileBody file = new FileBody(uploadFile);
		// 对请求的表单域进行填充
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("file", file);
		reqEntity.addPart("userName", new StringBody(userInfo.getEmail()));
		reqEntity.addPart("password", new StringBody(userInfo.getPassword()));
		reqEntity.addPart("appRowId", new StringBody(String.valueOf(appRowId)));
		// 设置请求
		httppost.setEntity(reqEntity);
		// 执行
		HttpResponse response = httpclient.execute(httppost);
		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			HttpEntity entity = response.getEntity();
			// 显示内容
			if (entity != null) {
				resultContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
			}

		} else {
			HttpEntity entity = response.getEntity();
			// 显示内容
			if (entity != null) {
				EntityUtils.consume(entity);
			}
		}
		return resultContent;
	}
}
