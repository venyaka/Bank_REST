package com.example.bankcards.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования операций в приложении.
 * <p>
 * Обеспечивает централизованное логирование:
 * - Входящих запросов в контроллеры
 * - Времени выполнения методов сервисов
 * - Исключений в сервисном слое
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
        Object[] args = joinPoint.getArgs();

        log.info("==> {}.{}() с аргументами: {}", className, methodName, formatArgs(args));
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

        log.info("<== {}.{}() завершён успешно", className, methodName);
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
                log.warn("Медленное выполнение: {}.{}() заняло {} мс", className, methodName, executionTime);
            } else {
                log.debug("{}.{}() выполнен за {} мс", className, methodName, executionTime);
            }

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("{}.{}() завершился с ошибкой за {} мс", className, methodName, executionTime);
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

        log.error("Исключение в {}.{}(): {} - {}",
                className, methodName, ex.getClass().getSimpleName(), ex.getMessage());
    }

    /**
     * Форматирует аргументы метода для логирования.
     * Скрывает чувствительные данные (пароли, токены).
     *
     * @param args аргументы метода
     * @return отформатированная строка
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    String argString = arg.toString();
                    // Скрываем чувствительные данные
                    if (argString.toLowerCase().contains("password") ||
                        argString.toLowerCase().contains("token") ||
                        argString.toLowerCase().contains("secret")) {
                        return arg.getClass().getSimpleName() + "[HIDDEN]";
                    }
                    // Ограничиваем длину вывода
                    if (argString.length() > 100) {
                        return argString.substring(0, 100) + "...";
                    }
                    return argString;
                })
                .toList()
                .toString();
    }
}

