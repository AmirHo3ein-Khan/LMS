package ir.lms.service.base;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImpl<T extends BaseEntity<ID>, ID extends Serializable>
        implements BaseService<T, ID> {

    private final String ENTITY_NOT_FOUND = "Entity not found";

    private final JpaRepository<T, ID> repository;

    protected BaseServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T persist(T t) {
        prePersist(t);
        T saved = repository.save(t);
        postPersist(saved);
        return saved;
    }

    @Override
    public void delete(ID id) {
        preDelete(id);
        repository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        repository.deleteById(id);
        postDelete(id);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }


    public abstract T update(ID id, T t);


    protected void prePersist(T t) {
    }

    protected void postPersist(T t) {
    }

    protected void preDelete(ID id) {
    }

    protected void postDelete(ID id) {
    }

}




