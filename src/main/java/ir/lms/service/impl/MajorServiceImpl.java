package ir.lms.service.impl;

import ir.lms.exception.DuplicateException;
import ir.lms.model.Major;
import ir.lms.repository.MajorRepository;
import ir.lms.service.MajorService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MajorServiceImpl extends BaseServiceImpl<Major , Long> implements MajorService {

    private final static String EXIST_MAJOR = "This major already exists!";
    private final static String NOT_FOUND = "%s not found!";

    private final MajorRepository majorRepository;

    protected MajorServiceImpl(JpaRepository<Major, Long> repository, MajorRepository majorRepository) {
        super(repository);
        this.majorRepository = majorRepository;
    }

    @Override
    protected void prePersist(Major major) {
        if (majorRepository.existsByMajorName(major.getMajorName())){
            throw new DuplicateException(EXIST_MAJOR);
        }
        major.setMajorCode(UUID.randomUUID());
        major.setActive(true);
    }

    @Override
    public void delete(Long aLong) {
        Major major = majorRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Major")));
        major.setActive(false);
        majorRepository.save(major);
    }
}

