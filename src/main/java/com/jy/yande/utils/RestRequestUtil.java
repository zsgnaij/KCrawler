package com.jy.yande.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class RestRequestUtil {

    @Autowired
    RestTemplate restTemplate;


    /**
     * resttemplate 发起get请求
     *
     * @param url    请求路径
     * @param header 请求头
     * @return 请求结果
     */
    public String restGetRequest(String url, Map<String, String> header) {
        String resData = null;
        ResponseEntity<String> responseEntity = null;
        if (header != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            header.keySet().forEach(key -> {
                httpHeaders.set(key, header.get(key));
            });
            HttpEntity<JSONObject> entity = new HttpEntity<>(null, httpHeaders);
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } else {
            responseEntity = restTemplate.getForEntity(url, String.class);
        }
        HttpStatus httpStatus = responseEntity.getStatusCode();
        if (httpStatus.is2xxSuccessful()) {
            resData = responseEntity.getBody();
        } else {
            System.out.println("请求失败");
        }
        return resData;
    }

}
