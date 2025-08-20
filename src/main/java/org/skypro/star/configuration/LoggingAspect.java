package org.skypro.star.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* org.skypro.star.service..*(..))")
    public void logCaller(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();
        boolean isArgsEmpty = (args == null || args.length == 0);

        BiFunction<Object[], Boolean, String> logTail = (arguments, empty) -> {
            if (empty) {
                return "with empty args";
            } else {
                String argumentTypes = Arrays.stream(arguments).map(arg -> arg.getClass().getSimpleName()).collect(Collectors.joining(", "));
                return String.format("with input args = %s (from %s.class)", Arrays.toString(arguments), argumentTypes);
            }
        };

        logger.info(" - this calling (parent) method = {}", methodName);
        logger.info(" - calling {}.{}() {}", className, methodName, logTail.apply(args, isArgsEmpty));
    }

    @AfterThrowing(pointcut = "execution(*  org.skypro.star.service..*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();
        logger.error(" - exception in {}.{}(), exception = {}", className, methodName, ex.toString());
    }
}

