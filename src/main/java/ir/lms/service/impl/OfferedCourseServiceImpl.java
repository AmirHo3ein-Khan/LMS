package ir.lms.service.impl;

import ir.lms.model.OfferedCourse;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.service.OfferedCourseService;
import ir.lms.service.base.BaseService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferedCourseServiceImpl extends BaseServiceImpl<OfferedCourse, Long> implements OfferedCourseService {
    private final OfferedCourseRepository offeredCourseRepository;

    protected OfferedCourseServiceImpl(JpaRepository<OfferedCourse, Long> repository, OfferedCourseRepository offeredCourseRepository) {
        super(repository);
        this.offeredCourseRepository = offeredCourseRepository;
    }

    @Override
    protected void prePersist(OfferedCourse offeredCourse) {
        super.prePersist(offeredCourse);
    }

    @Override
    protected void postPersist(OfferedCourse offeredCourse) {
        super.postPersist(offeredCourse);
    }

    @Override
    protected void preUpdate(OfferedCourse offeredCourse) {
        super.preUpdate(offeredCourse);
    }

    @Override
    protected void postUpdate(OfferedCourse offeredCourse) {
        super.postUpdate(offeredCourse);
    }

    @Override
    protected void preDelete(Long aLong) {
        super.preDelete(aLong);
    }

    @Override
    protected void postDelete(Long aLong) {
        super.postDelete(aLong);
    }
}
