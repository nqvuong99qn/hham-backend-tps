package com.tpssoft.hham.aspect;

import com.tpssoft.hham.service.SearchConstraint;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * TODO: Receive array of constraints instead
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
    }

    @Pointcut("execution(* *(*constraint)) && args(constraint)")
    public void search(SearchConstraint constraint) {
    }

    @Before("restController() && search(constraint)")
    public void beforeControllerSearchMethod(JoinPoint jp, SearchConstraint constraint) {
        log.info("Entering " + jp.toLongString());
        log.info(constraint.toString());
    }

    @After("restController() && search(constraint)")
    public void afterControllerMethodInvoked(JoinPoint jp, SearchConstraint constraint) {
        log.info("Exiting" + jp.toLongString());
    }
}
