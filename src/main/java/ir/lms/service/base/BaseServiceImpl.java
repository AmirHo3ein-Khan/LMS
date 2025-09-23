package ir.lms.service.base;

import ir.lms.model.base.BaseEntity;
import jakarta.persistence.EntityNotFoundException;
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
        boolean isNew = t.getId() == null;
        if (isNew) {
            prePersist(t);
        } else {
            repository.findById(t.getId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            preUpdate(t);
        }
        T saved = repository.save(t);
        if (isNew) {
            postPersist(saved);
        } else {
            postUpdate(saved);
        }
        return saved;
    }

    @Override
    public void delete(ID id) {
        preDelete(id);
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


    protected void prePersist(T t){
    }

    protected void postPersist(T t){
    }

    protected void preUpdate(T t){
    }

    protected void postUpdate(T t){
    }

    protected void preDelete(ID id){
    }

    protected void postDelete(ID id){
    }

}




