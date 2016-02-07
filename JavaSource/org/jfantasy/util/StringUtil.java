package org.jfantasy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class StringUtil {

    private static final Log logger = LogFactory.getLog(StringUtil.class);

    /**
     * 剔除字符串中的空白字符
     *
     * @param s 字符串
     * @return {string}
     */
    public static String trim(String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    /**
     * 以{delim}格式截取字符串返回list,不返回为空的字符串
     *
     * @param s     源字符串
     * @param delim 分割字符
     * @return {List<String>}
     */
    public static List<String> split(String s, String delim) {
        List<String> list = new ArrayList<String>();
        s = trim(s);
        if (s == null) {
            return list;
        }
        String[] rs = s.split(delim);
        for (String str : rs)
            if (str.trim().length() > 0) {
                list.add(str);
            }
        return list;
    }

    /**
     * 判断字符串是否为空或者空字符串
     *
     * @param s 要判断的字符串
     * @return {boolean}
     */
    public static boolean isNull(Object s) {
        return isBlank(nullValue(s));
    }

    /**
     * 判断字符串是否为非空字符串 {@link #isNull(Object)} 方法的取反
     *
     * @param s 要判断的字符串
     * @return {boolean}
     */
    public static boolean isNotNull(Object s) {
        return !isNull(s);
    }

    /**
     * 判断字符串是否为空，如果为空返回空字符，如果不为空，trim该字符串后返回
     *
     * @param s 要判断的字符串
     * @return {String}
     */
    public static String nullValue(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * 判断对象是否为空，如果为空返回空字符，如果不为空，返回该字符串的toString字符串
     *
     * @param s 要判断的对象
     * @return {String}
     */
    public static String nullValue(Object s) {
        return s == null ? "" : s.toString();
    }

    public static String nullValue(long s) {
        return s < 0L ? "" : String.valueOf(s);
    }

    public static String nullValue(int s) {
        return s < 0 ? "" : "" + s;
    }

    /**
     * 判断字符串是否为空，如果为空返回 {defaultValue}，如果不为空，直接返回该字符串
     *
     * @param s            要转换的对象
     * @param defaultValue 默认字符串
     * @return {String}
     */
    public static String defaultValue(Object s, String defaultValue) {
        return s == null ? defaultValue : s.toString();
    }

    /**
     * 判断字符串是否为空，如果为空返回 {defaultValue}，如果不为空，直接返回该字符串
     * {@link #defaultValue(Object, String)} 的重载
     *
     * @param s            要转换的字符串
     * @param defaultValue 默认字符串
     * @return {String}
     */
    public static String defaultValue(String s, String defaultValue) {
        return isBlank(s) ? defaultValue : s;
    }

    /**
     * 将Long转为String格式
     * 如果{s}为NULL 或者 小于 0 返回空字符。否则返回{s}的toString字符串
     *
     * @param s Long
     * @return {String}
     */
    public static String longValue(Long s) {
        return (s == null) || (s.intValue() <= 0) ? "" : s.toString();
    }

    /**
     * 将Long转为String格式
     * 如果{s}为NULL 或者 小于 0 返回"0"。否则返回{s}的toString字符串
     *
     * @param s Long
     * @return {String}
     */
    public static String longValueZero(Long s) {
        return (s == null) || (s.intValue() <= 0) ? "0" : s.toString();
    }

    public static String noNull(String s) {
        return s == null ? "" : s;
    }

    public static boolean isBlank(Object s) {
        return (s == null) || (nullValue(s).trim().length() == 0);
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * 首字母大写
     *
     * @param s 字符串
     * @return {String}
     */
    public static String upperCaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 字符串
     * @return {String}
     */
    public static String lowerCaseFirst(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    /**
     * 编码字符串 {s} 格式{enc}
     * 调用{@link #'URLEncoder.encode(String,String)'}
     *
     * @param s   要编码的字符串
     * @param enc 格式
     * @return {String}
     */
    public static String encodeURI(String s, String enc) {
        try {
            return isBlank(s) ? s : URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return s;
        }
    }

    /**
     * 解码字符串 {s} 格式{enc}
     * 调用{@link #'URLEncoder.decode(String,String)'}
     *
     * @param s   要解码的字符串
     * @param enc 格式
     * @return {String}
     */
    public static String decodeURI(String s, String enc) {
        try {
            return isBlank(s) ? s : URLDecoder.decode(s, enc);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return s;
        }
    }

    public static String replace(String s, String sub, String with) {
        int c = 0;
        int i = s.indexOf(sub, c);
        if (i == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder(s.length() + with.length());
        do {
            buf.append(s.substring(c, i));
            buf.append(with);
            c = i + sub.length();
        } while ((i = s.indexOf(sub, c)) != -1);

        if (c < s.length()) {
            buf.append(s.substring(c, s.length()));
        }
        return buf.toString();
    }

    public synchronized static void append(StringBuilder buf, String s, int offset, int length) {
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            if (i >= s.length())
                break;
            buf.append(s.charAt(i));
        }
    }

    public static void append(StringBuilder buf, byte b, int base) {
        int bi = 0xFF & b;
        int c = 48 + bi / base % base;
        if (c > 57)
            c = 97 + (c - 48 - 10);
        buf.append((char) c);
        c = 48 + bi % base;
        if (c > 57)
            c = 97 + (c - 48 - 10);
        buf.append((char) c);
    }

    public static void append2digits(StringBuffer buf, int i) {
        if (i < 100) {
            buf.append((char) (i / 10 + 48));
            buf.append((char) (i % 10 + 48));
        }
    }

    public static void append2digits(StringBuilder buf, int i) {
        if (i < 100) {
            buf.append((char) (i / 10 + 48));
            buf.append((char) (i % 10 + 48));
        }
    }

    public static String join(String[] array){
        return join(array,"");
    }

    public static String join(String[] array,String str){
        StringBuilder buf = new StringBuilder();
        for(String s : array){
            buf.append(s).append(str);
        }
        return buf.toString().replaceAll(str+"$","");
    }

    public static String[] add(String[] array, String... strs) {
        Set<String> list = new HashSet<String>(Arrays.asList(array));
        Collections.addAll(list, strs);
        return list.toArray(new String[list.size()]);
    }

    public static String nonNull(String s) {
        if (s == null)
            return "";
        return s;
    }

    public static boolean equals(String s, char[] buf, int offset, int length) {
        if (s.length() != length)
            return false;
        for (int i = 0; i < length; i++)
            if (buf[(offset + i)] != s.charAt(i))
                return false;
        return true;
    }

    public static String toUTF8String(byte[] b, int offset, int length) {
        try {
            return new String(b, offset, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static String toString(byte[] b, int offset, int length, String charset) {
        try {
            return new String(b, offset, length, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static String printable(String name) {
        if (name == null)
            return null;
        StringBuilder buf = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isISOControl(c))
                buf.append(c);
        }
        return buf.toString();
    }

    // public static String printable(byte[] b) {
    // StringBuilder buf = new StringBuilder();
    // for (int i = 0; i < b.length; i++) {
    // char c = (char) b[i];
    // if ((Character.isWhitespace(c)) || ((c > ' ') && (c < ''))) {
    // buf.append(c);
    // } else {
    // buf.append("0x");
    // TypeUtil.toHex(b[i], buf);
    // }
    // }
    // return buf.toString();
    // }

    public static byte[] getBytes(String s) {
        try {
            return s.getBytes("ISO-8859-1");
        } catch (Exception e) {
            logger.warn(e);
        }
        return s.getBytes();
    }

    public static byte[] getBytes(String s, String charset) {
        try {
            return s.getBytes(charset);
        } catch (Exception e) {
            logger.warn(e);
        }
        return s.getBytes();
    }

    public static String sidBytesToString(byte[] sidBytes) {
        StringBuilder sidString = new StringBuilder();

        sidString.append("S-");

        sidString.append(Byte.toString(sidBytes[0])).append('-');

        StringBuilder tmpBuilder = new StringBuilder();

        for (int i = 2; i <= 7; i++) {
            tmpBuilder.append(Integer.toHexString(sidBytes[i] & 0xFF));
        }

        sidString.append(Long.parseLong(tmpBuilder.toString(), 16));

        int subAuthorityCount = sidBytes[1];

        for (int i = 0; i < subAuthorityCount; i++) {
            int offset = i * 4;
            tmpBuilder.setLength(0);

            tmpBuilder.append(String.format("%02X%02X%02X%02X", new Object[]{sidBytes[(11 + offset)] & 0xFF, sidBytes[(10 + offset)] & 0xFF, sidBytes[(9 + offset)] & 0xFF, sidBytes[(8 + offset)] & 0xFF}));

            sidString.append('-').append(Long.parseLong(tmpBuilder.toString(), 16));
        }

        return sidString.toString();
    }

    public static byte[] sidStringToBytes(String sidString) {
        String[] sidTokens = sidString.split("-");

        int subAuthorityCount = sidTokens.length - 3;

        int byteCount = 0;
        byte[] sidBytes = new byte[8 + 4 * subAuthorityCount];

        sidBytes[(byteCount++)] = (byte) Integer.parseInt(sidTokens[1]);

        sidBytes[(byteCount++)] = (byte) subAuthorityCount;

        String hexStr = Long.toHexString(Long.parseLong(sidTokens[2]));

        while (hexStr.length() < 12) {
            hexStr = "0" + hexStr;
        }

        for (int i = 0; i < hexStr.length(); i += 2) {
            sidBytes[(byteCount++)] = (byte) Integer.parseInt(hexStr.substring(i, i + 2), 16);
        }

        for (int i = 3; i < sidTokens.length; i++) {
            hexStr = Long.toHexString(Long.parseLong(sidTokens[i]));

            while (hexStr.length() < 8) {
                hexStr = "0" + hexStr;
            }

            for (int j = hexStr.length(); j > 0; j -= 2) {
                sidBytes[(byteCount++)] = (byte) Integer.parseInt(hexStr.substring(j - 2, j), 16);
            }
        }

        return sidBytes;
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    private final static String[] emptyArray = new String[0];

}
