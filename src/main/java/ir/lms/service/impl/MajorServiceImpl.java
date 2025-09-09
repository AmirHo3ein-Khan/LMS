package ir.lms.service.impl;

import ir.lms.model.Major;
import ir.lms.repository.MajorRepository;
import ir.lms.service.MajorService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.BaseMapper;
import ir.lms.util.dto.major.MajorDTO;
import ir.lms.util.dto.mapper.MajorMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MajorServiceImpl extends BaseServiceImpl<Major, MajorDTO, Long> implements MajorService {
    protected MajorServiceImpl(MajorRepository repository, MajorMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected Major updateEntity(Major entity, MajorDTO majorDTO) {
        entity.setMajorName(majorDTO.getMajorName());
        entity.setUnitNumberGraduated(majorDTO.getUnitNumberGraduated());
        return entity;
    }
}
