package com.tcsl.zodiac.server.controller;

import com.tcsl.zodiac.server.model.Instance;
import com.tcsl.zodiac.server.register.InstanceRegister;
import com.tcsl.zodiac.server.register.RegisterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

/**
 * @className: BeatHeartController
 * @description:
 * @author: yang pan
 */
@RestController
@RequestMapping("/zodiac/apps")
@Slf4j
public class BeatHeartController {
    @Autowired
    private RegisterFactory registerFactory;

    @RequestMapping(value = "/beatHeart/{appName}/{instanceId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> beatHeart(@PathParam("appName") String appName , @PathParam("instanceId") String instanceId,
    @RequestParam("status")String  status , @RequestParam("lastDirtyTimestamp")String  lastDirtyTimestamp, @RequestParam("overriddenstatus")String  overriddenstatus) {

        InstanceRegister register = registerFactory.getRegister();
        register.getInstacne(appName,instanceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @RequestMapping(value = "/beatHeart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> beatHeart(@RequestBody Instance instance) {
//        log.info("服务心跳,instance:{}",instance);

        if (StringUtils.isBlank(instance.getInstanceId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失服务实例id");
        }
        if (StringUtils.isBlank(instance.getIp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失ip");
        }
        if (instance.getPort() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("缺失端口");
        }
        InstanceRegister register = registerFactory.getRegister();
        instance.setLastUpdatedTimestamp(System.currentTimeMillis());
        Instance instacne = register.getInstacne(instance.getAppName(), instance.getInstanceId());
        if (instacne == null){
            log.info("服务已经下线,从新注册服务,instance:{}",instance);
            register.register(instance);
        }
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

}
