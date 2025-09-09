package ir.lms.service.base;

import java.util.List;

public interface BaseService <DTO , ID> {
    DTO create(DTO dto);
    DTO update(ID id , DTO dto);
    void delete(ID id);
    DTO findById(ID id);
    List<DTO> findAll();
}
