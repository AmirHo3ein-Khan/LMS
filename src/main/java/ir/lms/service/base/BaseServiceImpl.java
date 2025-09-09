package ir.lms.service.base;

import ir.lms.model.base.BaseEntity;
import ir.lms.util.BaseMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<E extends BaseEntity<ID>, DTO, ID extends Serializable>
        implements BaseService<DTO, ID> {

    private final JpaRepository<E, ID> repository;
    private final BaseMapper<E, DTO> mapper;

    protected BaseServiceImpl(JpaRepository<E, ID> repository, BaseMapper<E, DTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public DTO create(DTO dto) {
        E entity = mapper.toEntity(dto);
        E saved = repository.save(entity);
        return mapper.toDto(saved);
    }


    @Override
    public DTO update(ID id, DTO dto) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
        E updatedEntity = updateEntity(entity, dto);
        return mapper.toDto(repository.save(updatedEntity));
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public DTO findById(ID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Override
    public List<DTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    protected abstract E updateEntity(E entity, DTO dto);
}




