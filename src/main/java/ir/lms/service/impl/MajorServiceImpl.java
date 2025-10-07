package ir.lms.service.impl;

import ir.lms.exception.DuplicateException;
import ir.lms.model.Major;
import ir.lms.repository.MajorRepository;
import ir.lms.service.MajorService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        major.setDeleted(false);
    }

    @Override
    public void delete(Long aLong) {
        Major major = majorRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Major")));
        major.setDeleted(true);
        majorRepository.save(major);
    }

    @Override
    public List<Major> findAll() {
        List<Major> majors = majorRepository.findAll();
        List<Major> result = new ArrayList<>();
        for (Major major : majors) {
            if (!major.isDeleted()) {
                result.add(major);
            }
        }
        return result;
    }

    @Override
    public Major update(Long aLong, Major major) {
        Major foundedMajor = majorRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        foundedMajor.setMajorName(major.getMajorName());
        foundedMajor.setMajorCode(UUID.randomUUID());
        return majorRepository.save(foundedMajor);
    }

    @Override
    public Major findById(Long aLong) {
        Major major = majorRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Major")));
        if (major.isDeleted()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "Major"));
        }
        return major;
    }
}

