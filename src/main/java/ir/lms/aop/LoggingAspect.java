//package ir.lms.aop;
//
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//
//public class LoggingAspect {
//
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//
//    @Pointcut(
//            "execution(public * ir.lms.service..*(..)) || " +
//                    "execution(public * ir.lms.controller..*(..))"
//    )
//    public void appPointcut(){}
//
//    @Around("appPointcut()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        String signature = joinPoint.getSignature().toShortString();
//        log.info("▶ Entering  {}", signature);
//        long start = System.currentTimeMillis();
//
//        try {
//            Object result = joinPoint.proceed();
//            long timeTaken = System.currentTimeMillis() - start;
//            log.info("◀ Exiting   {} | time={}ms", signature, timeTaken);
//            return result;
//        } catch (Throwable t) {
//            log.error("‼ Exception in {}: {}", signature, t.getMessage());
//            throw t;
//        }
//    }
//}