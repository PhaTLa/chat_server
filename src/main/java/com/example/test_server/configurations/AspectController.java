package com.example.test_server.configurations;

import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Aspect
@Log
@Configuration
public class AspectController {

    @Around("execution(* com.example.test_server.controllers.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Long stepInTime = System.currentTimeMillis();
        log.info(String.format("Go into %s at %d",joinPoint, stepInTime));

        if(joinPoint.getArgs().length>0){
            log.info(String.format("Params: %s",
                    Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", "))));
        }

        Object ret = joinPoint.proceed();

        Long stepOutTime = System.currentTimeMillis();
        Long takenTime = stepOutTime - stepInTime;
        log.info(String.format("Exit from %s at %d  -  taken: %d",joinPoint,stepOutTime,takenTime));

        return ret;
    }

    @AfterReturning(pointcut = "execution(* com.example.test_server.controllers.*.*(..))", returning = "returnObj")
    public void afterReturning(JoinPoint joinPoint,Object returnObj){
        if(returnObj!=null){
            log.info(String.format("return from %s : %s",joinPoint,returnObj));
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.test_server.controllers.*.*(..))", throwing = "throwable")
    public void afterThrowing(JoinPoint joinPoint, Exception throwable) {
        if (throwable != null){
            log.log(Level.SEVERE,"Throw from {0} : {1}",new Object[]{joinPoint,throwable});
        }
    }
}
