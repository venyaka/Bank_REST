package com.example.bankcards.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import static net.logstash.logback.argument.StructuredArguments.*;

/**
 * Аспект для логирования операций в приложении.
 * <p>
 * Обеспечивает централизованное логирование:
 * - Входящих запросов в контроллеры
 * - Времени выполнения методов сервисов
 * - Исключений в сервисном слое
 * <p>
 * Использует StructuredArguments для JSON-формата логов (ELK-совместимость).
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut для всех методов контроллеров.
     */
    @Pointcut("within(com.example.bankcards.controller..*)")
    public void controllerMethods() {}

    /**
     * Pointcut для всех методов сервисов.
     */
    @Pointcut("within(com.example.bankcards.service.impl..*)")
    public void serviceMethods() {}

    /**
     * Логирует входящие запросы в контроллеры.
     *
     * @param joinPoint точка соединения
     */
    @Before("controllerMethods()")
    public void logControllerMethodCall(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Вызов контроллера: {}.{}()",
                kv("controller", className),
                kv("method", methodName));
    }

    /**
     * Логирует успешное выполнение методов контроллеров.
     *
     * @param joinPoint точка соединения
     * @param result    результат выполнения метода
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logControllerMethodReturn(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Контроллер завершён успешно: {}.{}()",
                kv("controller", className),
                kv("method", methodName));
    }

    /**
     * Логирует время выполнения методов сервисов.
     *
     * @param joinPoint точка соединения
     * @return результат выполнения метода
     * @throws Throwable если метод выбросил исключение
     */
    @Around("serviceMethods()")
    public Object logServiceMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 1000) {
                log.warn("Медленное выполнение сервиса",
                        kv("service", className),
                        kv("method", methodName),
                        kv("executionTimeMs", executionTime),
                        kv("slow", true));
            } else {
                log.debug("Сервис выполнен",
                        kv("service", className),
                        kv("method", methodName),
                        kv("executionTimeMs", executionTime));
            }

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Сервис завершился с ошибкой",
                    kv("service", className),
                    kv("method", methodName),
                    kv("executionTimeMs", executionTime),
                    kv("error", true));
            throw ex;
        }
    }

    /**
     * Логирует исключения, возникшие в сервисном слое.
     *
     * @param joinPoint точка соединения
     * @param ex        выброшенное исключение
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Exception ex) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Исключение в сервисе",
                kv("service", className),
                kv("method", methodName),
                kv("exceptionType", ex.getClass().getSimpleName()),
                kv("exceptionMessage", ex.getMessage()));
    }
}

