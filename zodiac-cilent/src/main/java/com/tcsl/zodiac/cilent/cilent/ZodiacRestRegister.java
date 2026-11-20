package com.tcsl.zodiac.cilent.cilent;

import com.tcsl.zodiac.cilent.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: RestCaprinornCilentRegister
 * @description: resthttp 注册
 * @author: yang pan
 */
public class ZodiacRestRegister implements ServiceRegister {
    protected final Logger logger = LoggerFactory.getLogger(ZodiacRestRegister.class);

    private RestTemplate restTemplate;
    private String serviceUrl;
    private InstanceInfo instanceInfo;


    public ZodiacRestRegister(RestTemplate restTemplate, InstanceInfo instanceInfo) {
        this.restTemplate = restTemplate;
        this.instanceInfo = instanceInfo;
        this.serviceUrl = instanceInfo.getServiceUrl();
    }

    @Override
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    @Override
    public void register() {
        InstanceInfo instanceInfo = getInstanceInfo();
        String urlPath = serviceUrl + "/register";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<Void> httpResponse = restTemplate.exchange(urlPath, HttpMethod.POST, new HttpEntity<>(instanceInfo, headers),
                Void.class);

        if (HttpStatus.OK.value() != httpResponse.getStatusCode().value()) {
            logger.error("服务注册失败,httpResponse:{}", httpResponse);
            throw new RuntimeException("服务注册失败 httpResponse" + httpResponse);
        }
    }

    @Override
    public void deregister() {
        InstanceInfo instanceInfo = getInstanceInfo();
        String appName = instanceInfo.getAppName();
        String instanceId = instanceInfo.getInstanceId();
        String urlPath = serviceUrl + "/deregister" + appName + '/' + instanceId;

        ResponseEntity<Void> httpResponse = restTemplate.exchange(urlPath, HttpMethod.DELETE, null, Void.class);

        if (HttpStatus.OK.value() != httpResponse.getStatusCode().value()) {
            logger.error("服务下线失败,httpResponse:{}", httpResponse);
            throw new RuntimeException("服务注册失败 httpResponse" + httpResponse);
        }
    }
    @Override
    public InstanceInfo getInstance(String appName, String instanceId) {
        String url = serviceUrl + "getInstance" + appName + "/" + instanceId;

        ResponseEntity<InstanceInfo> response = restTemplate.exchange(url, HttpMethod.GET, null,
                InstanceInfo.class);

        return response.getStatusCode().value() == HttpStatus.OK.value() && response.hasBody()
                ? response.getBody() : null;

    }
    @Override
    public List<InstanceInfo> getInstances(String appName) {
        String url = serviceUrl + "getInstances?appName="+appName;

        ParameterizedTypeReference<List<InstanceInfo>> responseType = new ParameterizedTypeReference<List<InstanceInfo>>() {
        };
        ResponseEntity<List<InstanceInfo>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                responseType);

        return response.getStatusCode().value() == HttpStatus.OK.value() && response.hasBody()
                ? response.getBody() : null;
    }

    @Override
    public boolean renew() {
        InstanceInfo instanceInfo = getInstanceInfo();
        try {
            ResponseEntity<Void> httpResponse = sendHeartBeat(instanceInfo);
            if ( HttpStatus.NOT_FOUND == httpResponse.getStatusCode() ) {
                logger.info("ZodiacRestRegister:{} - Re-registering apps/{}", instanceInfo.getAppPathIdentifier(), instanceInfo.getAppName());
                if (HttpStatus.OK == httpResponse.getStatusCode()  ) {
                    return true;
                }

            } else {
                return HttpStatus.OK  == httpResponse.getStatusCode() ;
            }
        } catch (Throwable throwable) {
            logger.error("ZodiacRestRegister:{} - 无法发送心跳!", instanceInfo.getAppPathIdentifier(), throwable);
            return false;
        }
        return false;
    }

    public ResponseEntity<Void> sendHeartBeat(InstanceInfo instanceInfo) {
        String urlPath = serviceUrl + "/beatHeart";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<Void> httpResponse = restTemplate.exchange(urlPath, HttpMethod.POST, new HttpEntity<>(instanceInfo, headers),
                Void.class);

        if (HttpStatus.OK.value() != httpResponse.getStatusCode().value()) {
            logger.error("心跳执行失败,httpResponse:{}", httpResponse);
            throw new RuntimeException("服务注册失败 httpResponse" + httpResponse);
        }
        return httpResponse;
    }

    private static Map<String, String> headersOf(ResponseEntity<?> response) {
        HttpHeaders httpHeaders = response.getHeaders();
        if (httpHeaders == null || httpHeaders.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return headers;
    }

    @Override
    public List<String> getServices() {
        String url = serviceUrl + "getAppName";

        ParameterizedTypeReference<List<String>> responseType = new ParameterizedTypeReference<List<String>>() {
        };
        ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                responseType);

        return response.getStatusCode().value() == HttpStatus.OK.value() && response.hasBody()
                ? response.getBody() : null;
    }
}
