package org.jfantasy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * web 工具类
 *
 * @author 李茂峰
 * @version 1.0
 * @功能描述 web开发中经常使用的方法
 * @since 2013-9-10 上午9:16:01
 */
public class WebUtil {

    private static final Log logger = LogFactory.getLog(WebUtil.class);

    public static String getRequestUrl(HttpServletRequest request) {
        return getRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
    }

    public static String getRequestUrl(HttpServletRequest request, String contextPath) {
        return getRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(), contextPath);
    }

    public static String getRequestUrl(String scheme, String serverName, int serverPort, String contextPath) {
        scheme = scheme.toLowerCase();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if ("http".equals(scheme)) {
            if (serverPort != 80) {
                url.append(":").append(serverPort);
            }
        } else if ("https".equals(scheme)) {
            if (serverPort != 443) {
                url.append(":").append(serverPort);
            }
        }
        url.append(contextPath);
        return url.toString();
    }

    /**
     * 获取请求的端口号
     *
     * @param request 路径
     * @return {String}
     */
    public static String getPort(HttpServletRequest request) {
        return String.valueOf(request.getLocalPort());
    }

    @Deprecated
    public static boolean acceptEncoding(HttpServletRequest request) {
        String encoding = request.getHeader("Accept-Encoding");
        return (encoding != null) && (encoding.contains("gzip"));
    }

    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }

    private static Cookie[] getCookies(HttpServletRequest request) {
        return request.getCookies();
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = getCookies(request);
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(name))
                    return cookies[i];
            }
        }
        return null;
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getRealIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static boolean isSelfIp(String ip) {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ia = ips.nextElement();
                    if (ia instanceof Inet4Address && (ia.isSiteLocalAddress() || ia.isMCGlobal())) {
                        // 只获取IPV4的局域网和广域网地址，忽略本地回环和本地链路地址
                        // System.out.println("IP:"
                        // + ia.getHostAddress());
                        // System.out.println("--------------------------------------------");
                        if (ia.getHostAddress().equals(ip.trim())) {
                            return true;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public static Browser browser(HttpServletRequest request) {
        return Browser.getBrowser(request.getHeader("User-Agent"));
    }

    public static String getBrowserVersion(Browser browser, HttpServletRequest request) {
        return browser.getVersion(request.getHeader("User-Agent").toLowerCase());
    }

    public static String getOsVersion(HttpServletRequest request) {
        String useros = request.getHeader("User-Agent").toLowerCase();
        String osVersion = "unknown";
        if (useros.indexOf("nt 6.1") > 0)
            osVersion = "Windows 7";
        else if (useros.indexOf("nt 6.0") > 0)
            osVersion = "Windows Vista/Server 2008";
        else if (useros.indexOf("nt 5.2") > 0)
            osVersion = "Windows Server 2003";
        else if (useros.indexOf("nt 5.1") > 0)
            osVersion = "Windows XP";
        else if (useros.indexOf("nt 5") > 0)
            osVersion = "Windows 2000";
        else if (useros.indexOf("nt 4") > 0)
            osVersion = "Windows nt4";
        else if (useros.indexOf("me") > 0)
            osVersion = "Windows Me";
        else if (useros.indexOf("98") > 0)
            osVersion = "Windows 98";
        else if (useros.indexOf("95") > 0)
            osVersion = "Windows 95";
        else if (useros.indexOf("ipad") > 0)
            osVersion = "iPad";
        else if (useros.indexOf("macintosh") > 0)
            osVersion = "Mac";
        else if (useros.indexOf("unix") > 0)
            osVersion = "UNIX";
        else if (useros.indexOf("linux") > 0)
            osVersion = "Linux";
        else if (useros.indexOf("sunos") > 0) {
            osVersion = "SunOS";
        }else if(useros.indexOf("iPhone")>0){
            osVersion = "iPhone";
        }else if(useros.indexOf("Android")>0){
            osVersion = "Android";
        }
        return osVersion;
    }

    public static String getQueryString(Map<String, String[]> params) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                queryString.append(entry.getKey()).append("=").append(value).append("&");
            }
        }
        return queryString.toString().replaceAll("&$", "");
    }

    public static class UserAgent{

    }

    public static enum Browser {
        Opera("Opera", "version/\\d+\\W\\d+"),
        chrome("Chrome", "Chrome/\\d+\\W\\d+"),
        Firefox("Firefox", "Firefox/\\d+\\W\\d+"),
        safari("Safari", "version/\\d+\\W\\d+\\W\\d+"),
        _360se("360SE", "360SE/\\d+\\W\\d+"),
        green("GreenBrowser", "GreenBrowser/\\d+\\W\\d+"),
        qq("QQBrowser", "QQBrowser/\\d+\\W\\d+"),
        maxthon("Maxthon", "Maxthon \\d+\\W\\d+"),
        msie("MSIE", "msie\\s\\d+\\W\\d+"),
        mozilla("Mozilla", "firefox/\\d+\\W\\d+"),
        mqqbrowser("MQQBrowser","MQQBrowser/\\d+\\W\\d+"),
        ucbrowser("UCBrowser","UCBrowser/\\d+\\W\\d+"),
        baidubrowser("baidubrowser","baidubrowser/\\d+\\W\\d+"),
        unknown("unknown", "version/\\d+\\W\\d+");

        private String browser;
        private String version;

        private Browser(String browser, String version) {
            this.browser = browser;
            this.version = version;
        }

        public String getVersion(String agent) {
            if ("unknown".equals(this.version)) {
                return null;
            }
            return RegexpUtil.parseFirst(agent, this.version);
        }

        public static Browser getBrowser(String userAgent) {
            userAgent = StringUtil.defaultValue(userAgent, "").toLowerCase();
            for (Browser browser : Browser.values()) {
                if (RegexpUtil.isMatch(userAgent, browser.browser)) {
                    return browser;
                }
            }
            return unknown;
        }

        @Override
        public String toString() {
            return this.browser;
        }

    }

    public static String getValue(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return new String(bytes);
        }
    }

    public static String transformCoding(String str, String oldCharset, String charset) {
        try {
            return new String(str.getBytes(oldCharset), charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return str;
        }
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    public static String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null ? session.getId() : null);
    }

    public static String getMethod(HttpServletRequest request) {
        return request.getMethod();
    }

}