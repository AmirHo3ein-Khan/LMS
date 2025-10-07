package ir.lms.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.model.log.OperationLog;
import ir.lms.repository.OperationLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    private final Map<Object, String> oldDataHolder = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OperationLogRepository logRepository;
    private final MapperRegistry mapperRegistry;


    public LoggingAspect(OperationLogRepository logRepository, MapperRegistry mapperRegistry) {
        this.logRepository = logRepository;
        this.mapperRegistry = mapperRegistry;
    }


    @Before("execution(* ir.lms.service.base.BaseServiceImpl.update*(..)) || execution(* ir.lms.service.base.BaseServiceImpl.delete*(..))")
    public void captureOldData(JoinPoint joinPoint) {
        Object entity = joinPoint.getArgs()[0];
        try {
            Object oldEntity = joinPoint.getTarget().getClass()
                    .getMethod("findById", entity.getClass())
                    .invoke(joinPoint.getTarget(), entity);

            oldDataHolder.put(getEntityId(entity), toJson(oldEntity));
        } catch (Exception e) {
            oldDataHolder.put(getEntityId(entity), "Could not fetch old entity");
        }
    }

    @AfterReturning(pointcut = "execution(* ir.lms.service.base.BaseServiceImpl.*(..)) || " +
            "execution(* ir.lms.service.impl.AuthServiceImpl.login(..)) ||" +
            " execution(* ir.lms.service.impl.QuestionServiceImpl.createQuestion(..))", returning = "result")
    public void logOperation(JoinPoint joinPoint, Object result) {

        String methodName = joinPoint.getSignature().getName();
        String entityName = result != null ? result.getClass().getSimpleName() : "Unknown";

        OperationLog log = new OperationLog();

        log.setEntity(entityName);
        log.setTimestamp(LocalDateTime.now());
        log.setUsername(getCurrentUsername());

        Object entity = joinPoint.getArgs()[0];
        try {
            if (methodName.startsWith("persist") || methodName.startsWith("register") || methodName.startsWith("createQuestion")) {
                log.setOperation("CREATE");
                log.setNewData(toJson(result));
                log.setEntityId(result != null ? getEntityId(result) : null);
            } else if (methodName.startsWith("update")) {
                log.setOperation("UPDATE");
                log.setOldData(oldDataHolder.get(getEntityId(entity)));
                log.setNewData(toJson(result));
                log.setEntityId(result != null ? getEntityId(result) : entity.toString());
            } else if (methodName.startsWith("delete")) {
                log.setOperation("DELETE");
                log.setOldData(oldDataHolder.get(getEntityId(entity)));
                log.setEntityId(getEntityId(entity));
            } else if (methodName.startsWith("login")) {
                log.setOperation("LOGIN");
                log.setEntity(null);
            }
        } catch (Exception e) {
            if (result != null) log.setNewData(result.toString());
        } finally {
            oldDataHolder.remove(getEntityId(entity));
            saveLogAsync(log);
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Unknown";
    }

    private String getEntityId(Object entity) {
        try {
            Method entityID = entity.getClass().getMethod("getId");
            Object id = entityID.invoke(entity);
            return id != null ? id.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }


    private String toJson(Object entity) {
        if (entity == null) return null;

        try {
            BaseMapper mapper = mapperRegistry.getMapper(entity.getClass());
            Object dto = (mapper != null) ? mapper.toDto(entity) : entity;
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            return entity.toString();
        }
    }

    @Async
    public void saveLogAsync(OperationLog log) {
        logRepository.save(log);
    }

}
