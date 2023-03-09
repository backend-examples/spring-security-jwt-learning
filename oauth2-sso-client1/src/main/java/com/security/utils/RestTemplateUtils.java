package com.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * http请求公共类
 */
public class RestTemplateUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtils.class);

    /**
     * 请求接口公共方法
     * @param Methods
     * @param url
     * @param UrlParams
     * @return
     */
    public static String getResponse(String url, HttpMethod Methods, Map<String, String> UrlParams, Map<String, String> HeaderParams) throws IOException {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = Methods;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 设置用户提交的header头数据
        if (!HeaderParams.isEmpty()) {
            HeaderParams.forEach((k, v) -> {
                headers.set(k, v);
            });
        }
        // 将请求头部和参数合成一个请求
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (Methods == HttpMethod.GET) {
            String _UrlParams = getUrlParamsByMap(UrlParams);
            url = url + "?" +_UrlParams;
        }else {
            if (!UrlParams.isEmpty()) {
                UrlParams.forEach((k,v) ->{
                    params.put(k, Collections.singletonList(v));
                });
            }
        }
//        logger.info("URL:{}", url);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        // 执行HTTP请求，将返回的结构使用spring ResponseEntity处理http响应
        ResponseEntity<byte[]> responseEntity = client.exchange(url, method, requestEntity, byte[].class);
        String contentEncoding = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
        // gzip编码
        if ("gzip".equals(contentEncoding)) {
            // gzip解压服务器的响应体
            byte[] data = unGZip(new ByteArrayInputStream(responseEntity.getBody()));
//            logger.info(new String(data, StandardCharsets.UTF_8));
            return new String(data);
        } else {
            // 其他编码暂时不做处理(如果需要处理其他编码请自行扩展)
            return new String(responseEntity.getBody());
        }
    }

    /**
     * Gzip解压缩
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] unGZip(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buf = new byte[4096];
            int len = -1;
            while ((len = gzipInputStream.read(buf, 0, buf.length)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.close();
        }
    }

    /**
     　　* 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     　　* @param params 需要排序并参与字符拼接的参数组
     　　* @return 拼接后字符串
     　　* @throws UnsupportedEncodingException
     　　*/
    public static String getUrlParamsByMap(Map<String, String> params) throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            value = URLEncoder.encode(value, "UTF-8");
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
}
