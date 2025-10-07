package ir.lms.aop;

import ir.lms.mapper.base.BaseMapper;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class MapperRegistry {

    private final Map<Class<?>, BaseMapper<?, ?>> registry = new HashMap<>();

    public <E, D> void register(Class<E> entityClass, BaseMapper<E, D> mapper) {
        registry.put(entityClass, mapper);
    }

    @SuppressWarnings("unchecked")
    public <E, D> BaseMapper<E, D> getMapper(Class<E> entityClass) {
        return (BaseMapper<E, D>) registry.get(entityClass);
    }
}
