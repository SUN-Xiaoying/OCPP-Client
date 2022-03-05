package com.xiao.cp.impl;

import com.xiao.cp.model.ChargePoint;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@AllArgsConstructor
public class ChargePointImpl {
    private final ChargePoint chargePoint;

    @PostConstruct
    public void startClient() throws Exception{
        chargePoint.connect();

        // Test
        chargePoint.sendAuthorizeRequest("testId");
        chargePoint.sendStartTransactionRequest();
    }
}
