package com.tcsl.zodiac.comm.http;

import com.tcsl.zodiac.comm.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: HttpUtil
 * @description:
 * @author: yang pan
 */
public class HttpUtils {
    private static final Pattern CONTEXT_PATH_MATCH = Pattern.compile("(\\/)\\1+");



    /**
     * Init request from entity map.
     *
     * @param requestBase requestBase {@link HttpRequestBase}
     * @param body    body map
     * @param charset charset of entity
     * @throws Exception exception
     */
    public static void initRequestFromEntity(HttpRequestBase requestBase, Map<String, String> body, String charset) throws Exception {
        if (body == null || body.isEmpty()) {
            return;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>(body.size());
        for (Map.Entry<String, String> entry : body.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        if (requestBase instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) requestBase;
            HttpEntity entity = new UrlEncodedFormEntity(params, charset);
            request.setEntity(entity);
        }
    }

    /**
     * Build URL.
     *
     * @param isHttps    whether is https
     * @param serverAddr server ip/address
     * @param subPaths   api path
     * @return URL string
     */
    public static String buildUrl(boolean isHttps, String serverAddr, String... subPaths) {
        StringBuilder sb = new StringBuilder();
        if (isHttps) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(serverAddr);
        String pre = null;
        for (String subPath : subPaths) {
            if (StringUtils.isBlank(subPath)) {
                continue;
            }
            Matcher matcher = CONTEXT_PATH_MATCH.matcher(subPath);
            if (matcher.find()) {
                throw new IllegalArgumentException("Illegal url path expression : " + subPath);
            }
            if (pre == null || !pre.endsWith("/")) {
                if (subPath.startsWith("/")) {
                    sb.append(subPath);
                } else {
                    sb.append("/").append(subPath);
                }
            } else {
                if (subPath.startsWith("/")) {
                    sb.append(subPath.replaceFirst("\\/", ""));
                } else {
                    sb.append(subPath);
                }
            }
            pre = subPath;
        }
        return sb.toString();
    }

    /**
     * Translate parameter map.
     *
     * @param parameterMap parameter map
     * @return parameter map
     * @throws Exception exception
     */
    public static Map<String, String> translateParameterMap(Map<String, String[]> parameterMap) throws Exception {
        Map<String, String> map = new HashMap<String, String>(16);
        for (String key : parameterMap.keySet()) {
            map.put(key, parameterMap.get(key)[0]);
        }
        return map;
    }

    /**
     * Encoding parameters to url string.
     *
     * @param params   parameters
     * @param encoding encoding charset
     * @return url string
     * @throws UnsupportedEncodingException if encoding string is illegal
     */
    public static String encodingParams(Map<String, String> params, String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == params || params.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.isEmpty(entry.getValue())) {
                continue;
            }

            sb.append(entry.getKey()).append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }

        return sb.toString();
    }

    /**
     * Encoding KV list to url string.
     *
     * @param paramValues parameters
     * @param encoding    encoding charset
     * @return url string
     * @throws UnsupportedEncodingException if encoding string is illegal
     */
    public static String encodingParams(List<String> paramValues, String encoding) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext(); ) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), encoding));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static String decode(String str, String encode) throws UnsupportedEncodingException {
        return innerDecode(null, str, encode);
    }


    private static String innerDecode(String pre, String now, String encode) throws UnsupportedEncodingException {
        // Because the data may be encoded by the URL more than once,
        // it needs to be decoded recursively until it is fully successful
        if (StringUtils.equals(pre, now)) {
            return pre;
        }
        pre = now;
        now = URLDecoder.decode(now, encode);
        return innerDecode(pre, now, encode);
    }
}
