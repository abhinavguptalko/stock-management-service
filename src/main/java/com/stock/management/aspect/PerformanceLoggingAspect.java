package com.stock.management.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    @Around("execution(* com.stock.management..*(..))")
    public Object logExecutionTimeWithThreshold(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start;

            if (executionTime > 1000) { // Log a warning if execution takes more than 1 second
            	LOGGER.warn("Performance Alert: {} took {} ms", joinPoint.getSignature(), executionTime);
            } else {
            	LOGGER.info("Performance: {} executed in {} ms", joinPoint.getSignature(), executionTime);
            }
        }
    }

}
