package ir.lms.util;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
