package com.tcsl.zodiac.server.controller;

import com.tcsl.zodiac.server.model.AppInfo;
import com.tcsl.zodiac.server.model.Instance;
import com.tcsl.zodiac.server.model.PageInfo;
import com.tcsl.zodiac.server.register.RegisterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: capricornus
 * @description:
 * @author: Yang Pan
 */

@RestController
@RequestMapping("/zodiac/apps")
@Slf4j
public class RegisterController {


    @Autowired
    private RegisterFactory register;

    @RequestMapping("/getAppName")
    public ResponseEntity<?>  getAppName() {
        log.info("获取服务列表");
        List<AppInfo> appInfos = register.getRegister().getAppInfos();
        List<String> appNames = appInfos.stream().map(appInfo -> appInfo.getAppName()).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(appNames);
    }

    @RequestMapping("/getAppInfos")
    public ResponseEntity<?>  getAppInfos() {
        log.info("获取服务列表");
        List<AppInfo> appInfos = register.getRegister().getAppInfos();
        return ResponseEntity.status(HttpStatus.OK).body(appInfos);
    }

    @RequestMapping(value = "/getInsatces", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInstances() {
        log.info("获取所有实例列表");
        List<Instance> instacnes = register.getRegister().getInstacnes();
        return ResponseEntity.status(HttpStatus.OK).body(instacnes);
    }

    @RequestMapping("/getInstances")
    public List<Instance> getInstances(String appName) {
        log.info("获取实例列表,appName:{}",appName);
        return register.getRegister().getInstacnes(appName);
    }

    @RequestMapping(value = "/getInsatce/{appName}/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInstanceInfo(@PathParam("appName") String appName , @PathParam("instanceId") String instanceId) {
        log.info("获取实例列表,appName:{},instanceId:{}",appName,instanceId);
        if (StringUtils.isBlank(appName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("确实服务名称");
        }
        if (StringUtils.isBlank(instanceId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失服务实例id");
        }
        Instance instance = register.getRegister().getInstacne(appName,instanceId);
        return ResponseEntity.status(HttpStatus.OK).body(instance);

    }

    @RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody Instance instance) {
        log.info("注册服务,instance:{}",instance);

        if (StringUtils.isBlank(instance.getInstanceId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失服务实例id");
        }
        if (StringUtils.isBlank(instance.getIp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失ip");
        }
        if (instance.getPort() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失端口");
        }

        register.getRegister().register(instance);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @RequestMapping(value = "/deregister/{appName}/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deregister(@PathParam("appName") String appName , @PathParam("instanceId") String instanceId) {
        log.info("注消服务,appName:{},instanceId:{}",appName,instanceId);

        if (StringUtils.isBlank(appName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失服务实例id");
        }
        if (StringUtils.isBlank(instanceId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失ip");
        }

        register.getRegister().deregister(appName,instanceId);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}

