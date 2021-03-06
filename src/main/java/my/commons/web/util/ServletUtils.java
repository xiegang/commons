package my.commons.web.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import my.commons.Constants;
import my.commons.Result;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * servlet工具类<br/>
 * 主要功能：
 * <ul>
 * <li>获取真实IP地址</li>
 * <li>简化session部分操作</li>
 * <li>简化cookie部分操作</li>
 * <li>URL相关</li>
 * </ul>
 * </p>
 * @author xiegang
 * @since 2012-11-12
 *
 */
public class ServletUtils {
	public static String URL_ENCODING = Constants.ENCODING;
	
	/**
	 * 判断请求是否是ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWith = request.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}
	
	/** 判断是否为搜索引擎  */
	public static boolean isRobot(HttpServletRequest request) {
		String ua = request.getHeader("user-agent");
		if (StringUtils.isBlank(ua)) {
			return false;
		}
		return ua.indexOf("Baiduspider") != -1
				|| ua.indexOf("Googlebot") != -1 
				|| ua.indexOf("sogou") != -1
				|| ua.indexOf("sina") != -1
				|| ua.indexOf("iaskspider") != -1
				|| ua.indexOf("ia_archiver") != -1
				|| ua.indexOf("Sosospider") != -1
				|| ua.indexOf("YoudaoBot") != -1
				|| ua.indexOf("yahoo") != -1
				|| ua.indexOf("yodao") != -1
				|| ua.indexOf("MSNBot") != -1
				|| ua.indexOf("spider") != -1
				|| ua.indexOf("Twiceler") != -1
				|| ua.indexOf("Sosoimagespider") != -1
				|| ua.indexOf("naver.com/robots") != -1
				|| ua.indexOf("Nutch") != -1
				|| ua.indexOf("spider") != -1;
	}
	
	/**
	 * 获取IP地址
	 * @param request request current HTTP request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * Determine the session id of the given request, if any.
	 * @param request current HTTP request
	 * @return the session id, or <code>null</code> if none
	 */
	public static String getSessionId(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session != null ? session.getId() : null);
	}
	
	/**
	 * Check the given request for a session attribute of the given name.
	 * Returns null if there is no session or if the session has no such attribute.
	 * Does not create a new session if none has existed before!
	 * @param request current HTTP request
	 * @param name the name of the session attribute
	 * @return the value of the session attribute, or <code>null</code> if not found
	 */
	public static Object getSessionAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		return (session != null ? session.getAttribute(name) : null);
	}
	
	/**
	 * Set the session attribute with the given name to the given value.
	 * Removes the session attribute if value is null, if a session existed at all.
	 * Does not create a new session if not necessary!
	 * @param request current HTTP request
	 * @param name the name of the session attribute
	 * @param value the value of the session attribute
	 */
	public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
		if (value != null) {
			request.getSession().setAttribute(name, value);
		}
		else {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(name);
			}
		}
	}
	
	/**
	 * Retrieve the first cookie with the given name. Note that multiple
	 * cookies can have the same name but different paths or domains.
	 * @param request current servlet request
	 * @param name cookie name
	 * @return the first cookie with the given name, or <code>null</code> if none is found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据cookie名称返回cookie的值
	 *
	 * @param request http请求
	 * @param name	cookie名称
	 * @return	返回cookie值,值不存在则返回null
	 */
	public static String getCookieValue(HttpServletRequest request, String name){
		Cookie cookie = getCookie(request,name);
		if(cookie==null) {
			return null;
		}
		return cookie.getValue();
	}
	
	/**
	 * 是否是有效的Redirect地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isValidRedirectUrl(String url) {
		return url != null && url.startsWith("/") || isAbsoluteUrl(url);
	}

	/**
	 * 是否是绝对地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isAbsoluteUrl(String url) {
		final Pattern ABSOLUTE_URL = Pattern.compile("\\A[a-z.+-]+://.*", Pattern.CASE_INSENSITIVE);
		return ABSOLUTE_URL.matcher(url).matches();
	}
	
	public static String encodeURI(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, URL_ENCODING)
				.replaceAll("\\+", "%20")
				.replaceAll("\\%21", "!")
				.replaceAll("\\%27", "'")
				.replaceAll("\\%28", "(")
				.replaceAll("\\%29", ")")
				.replaceAll("\\%7E", "~");
	}
	
	public static String decodeURI(String s) throws UnsupportedEncodingException {
		s = s.replaceAll("%20", "\\+")
				.replaceAll("!", "\\%21")
				.replaceAll("'", "\\%27")
				.replaceAll("(", "\\%28")
				.replaceAll(")", "\\%29")
				.replaceAll("~", "\\%7E");
		return URLDecoder.decode(s, URL_ENCODING);
	}
	
	/**
	 * @see #getRequestURL(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURLComponent(HttpServletRequest req, String[] excludeParams) throws UnsupportedEncodingException {
		return getRequestURLComponent(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestURI(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURIComponent(HttpServletRequest req, String[] excludeParams) throws UnsupportedEncodingException {
		return getRequestURIComponent(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestPATH(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestPATHComponent(HttpServletRequest req, String[] excludeParams) throws UnsupportedEncodingException {
		return getRequestPATHComponent(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestURL(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURLComponent(HttpServletRequest req) throws UnsupportedEncodingException {
		return getRequestURLComponent(req, true, null);
	}
	
	/**
	 * @see #getRequestURI(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURIComponent(HttpServletRequest req) throws UnsupportedEncodingException {
		return getRequestURIComponent(req, true, null);
	}
	
	/**
	 * @see #getRequestPATH(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestPATHComponent(HttpServletRequest req) throws UnsupportedEncodingException {
		return getRequestPATHComponent(req, true, null);
	}
	
	/**
	 * @see #getRequestURL(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURL(HttpServletRequest req, String[] excludeParams) throws UnsupportedEncodingException {
		return getRequestURL(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestURI(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURI(HttpServletRequest req, String[] excludeParams) {
		return getRequestURI(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestPATH(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestPATH(HttpServletRequest req, String[] excludeParams) {
		return getRequestPATH(req, true, excludeParams);
	}
	
	/**
	 * @see #getRequestURL(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURL(HttpServletRequest req) {
		return getRequestURL(req, true, null);
	}
	
	/**
	 * @see #getRequestURI(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURI(HttpServletRequest req) {
		return getRequestURI(req, true, null);
	}
	
	/**
	 * @see #getRequestPATH(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestPATH(HttpServletRequest req) {
		return getRequestPATH(req, true, null);
	}
	
	/**
	 * @see #getRequestURL(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURLComponent(HttpServletRequest req, boolean hasQuery, String[] excludeParams) throws UnsupportedEncodingException {
		return URLEncoder.encode(getRequestURL(req, hasQuery, excludeParams), URL_ENCODING);
	}
	
	/**
	 * @see #getRequestURI(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestURIComponent(HttpServletRequest req, boolean hasQuery, String[] excludeParams) throws UnsupportedEncodingException {
		return URLEncoder.encode(getRequestURI(req, hasQuery, excludeParams), URL_ENCODING);
	}
	
	/**
	 * @see #getRequestPATH(HttpServletRequest, boolean, String[])
	 */
	public static String getRequestPATHComponent(HttpServletRequest req, boolean hasQuery, String[] excludeParams) throws UnsupportedEncodingException {
		return URLEncoder.encode(getRequestPATH(req, hasQuery, excludeParams), URL_ENCODING);
	}
	
	/**
	 * 获取请求的完整请求路径[支持forword的请求]<br/>
	 * 比如：http://127.0.0.1:8080/app/user/index.jsp?v=1
	 * 
	 */
	public static String getRequestURL(HttpServletRequest req, boolean hasQuery, String[] excludeParams) {
		// request URI
		String requestURI = (String) req.getAttribute("javax.servlet.forward.request_uri");
		if(null == requestURI) {
			requestURI = req.getRequestURI();
		}
		// query String
		String queryString = null;
		if (hasQuery) {
			queryString = (String) req.getAttribute("javax.servlet.forward.query_string");
			if(null == queryString) {
				queryString = req.getQueryString();
			}
		}
		return buildRequestURL(req.getScheme(), req.getServerName(), req.getServerPort(), requestURI, queryString, excludeParams);
	}
	
	/**
	 * 获取请求的路径[支持forword的请求]<br/>
	 * 比如：/app/user/index.jsp?v=1
	 */
	public static String getRequestURI(HttpServletRequest req, boolean hasQuery, String[] excludeParams) {
		// request URI
		String requestURI = (String) req.getAttribute("javax.servlet.forward.request_uri");
		if(null == requestURI) {
			requestURI = req.getRequestURI();
		}
		// query String
		String queryString = null;
		if (hasQuery) {
			queryString = (String) req.getAttribute("javax.servlet.forward.query_string");
			if(null == queryString) {
				queryString = req.getQueryString();
			}
		}
		return buildRequestURI(requestURI, queryString, excludeParams);
	}
	
	/**
	 * 获取请求的路径，应用名称之后的部分[支持forword的请求]<br/>
	 * 比如：/user/index.jsp?v=1
	 */
	public static String getRequestPATH(HttpServletRequest req, boolean hasQuery, String[] excludeParams) {
		// request URI
		String requestURI = (String) req.getAttribute("javax.servlet.forward.request_uri");
		if(null == requestURI) {
			requestURI = req.getRequestURI();
		}
		// query String
		String queryString = null;
		if (hasQuery) {
			queryString = (String) req.getAttribute("javax.servlet.forward.query_string");
			if(null == queryString) {
				queryString = req.getQueryString();
			}
		}
		return buildRequestPath(requestURI, req.getContextPath(), queryString, excludeParams);
	}
	
	private static String buildRequestURL(String scheme, String serverName, int serverPort, String requestURI, String queryString, String[] excludeParams) {
		scheme = scheme.toLowerCase();
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		// Only add port if not default
		if ("http".equals(scheme)) {
			if (serverPort != 80) {
				url.append(":").append(serverPort);
			}
		} else if ("https".equals(scheme)) {
			if (serverPort != 443) {
				url.append(":").append(serverPort);
			}
		}
		url.append(requestURI);

		queryString = queryStringExcludeParams(queryString, excludeParams);
		if (queryString != null && queryString.length() > 0) {
			url.append("?").append(queryStringExcludeParams(queryString, excludeParams));
		}
		return url.toString();
	}

	private static String buildRequestURI(String requestURI, String queryString, String[] excludeParams) {
		StringBuilder url = new StringBuilder(requestURI);
		queryString = queryStringExcludeParams(queryString, excludeParams);
		if (queryString != null && queryString.length() > 0) {
			url.append("?").append(queryStringExcludeParams(queryString, excludeParams));
		}
		return url.toString();
	}
	
	private static String buildRequestPath(String requestURI, String contextPath, String queryString, String[] excludeParams) {
		StringBuilder url = new StringBuilder(requestURI.substring(contextPath.length()));
		queryString = queryStringExcludeParams(queryString, excludeParams);
		if (queryString != null && queryString.length() > 0) {
			url.append("?").append(queryStringExcludeParams(queryString, excludeParams));
		}
		return url.toString();
	}
	
	private static String queryStringExcludeParams(String queryString, String[] excludeParams) {
		if (null != queryString && queryString.length() > 0 && null != excludeParams && excludeParams.length > 0) {
			StringBuffer sb = new StringBuffer();
			String[] params = queryString.split("&");
			for (String param : params) {
				String[] paramPart = param.split("=");
				int index;
				for (index = excludeParams.length - 1; index >= 0; index--) {
					if (paramPart[0].equals(excludeParams[index])) {
						break;
					}
				}
				if (index < 0) {
					if(paramPart.length>1){
						sb.append(paramPart[0]).append('=').append(paramPart[1]).append('&');
					}else{
						sb.append(paramPart[0]).append('=').append('&');
					}
				}
			}
			// update queryString
			queryString = (sb.length() > 0 && sb.charAt(sb.length() - 1) == '&') ? sb.substring(0, sb.length() - 1) : sb.toString();
		}
		return queryString;
	}
	
	/**
	 * 转发到指定页面<br/>
	 * /user/index.jsp
	 * 
	 * @param request
	 * @param response
	 * @param pageUrl 转发的页面
	 * @throws IOException
	 * @throws ServletException 
	 */
	public static void forward(HttpServletRequest request, HttpServletResponse response, String pageUrl) throws IOException, ServletException {
		if (!response.isCommitted()) {
			if (StringUtils.isNotBlank(pageUrl)) {
				// forward to page.
				request.getRequestDispatcher(pageUrl).forward(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			}
		}
	}

	/**
	 * 重定向到指定页面<br/>
	 * /user/index.jsp
	 * 
	 * @param request
	 * @param response
	 * @param pageUrl 重定向的页面
	 * @throws IOException 
	 */
	public static void redirect(HttpServletRequest request, HttpServletResponse response, String pageUrl) throws IOException {
		if (!response.isCommitted()) {
			if (StringUtils.isNotBlank(pageUrl)) {
				// redirect to page.
				response.sendRedirect(request.getContextPath() + pageUrl);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
			}
		}
	}
	
	/** forbidden request */
	public static void forbidden(HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
	
	/**
	 * 将result中的属性添加到请求
	 * 
	 * @param request
	 * @param result
	 * 
	 * @see Result
	 */
	public static void addResult2Request(HttpServletRequest request, Result result) {
		for (Entry<String, Object> entry : result.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * 将result中的属性合并到请求
	 * 
	 * @param request
	 * @param result
	 * 
	 * @see Result
	 */
	public static void mergeResult2Request(HttpServletRequest request, Result result) {
		for (Entry<String, Object> entry : result.entrySet()) {
			if (request.getAttribute(entry.getKey()) == null) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}
}
