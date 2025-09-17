package ir.lms.util.mapper.base;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
