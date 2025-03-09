package com.chertiavdev.bookingapp.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcuts {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerLog(){
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceLog() {
    }
}
