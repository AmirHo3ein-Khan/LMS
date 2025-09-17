package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Term;
import ir.lms.repository.TermRepository;
import ir.lms.service.TermService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.SemesterUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TermServiceImpl extends BaseServiceImpl<Term, Long> implements TermService {
    private final static String NOT_BE_NULL = "Term start and end times must not be null";
    private final static String FUTURE_ILLEGAL = "Term %s time must be in the future";
    private final static String TIME_ILLEGAL = "Term start time must be before end time";
    private final static String ILLEGAL_AFTER_START = "Can't %s term after start date!";
    private final static String NOT_FOUND = "%s not found!";
    private final TermRepository termRepository;

    protected TermServiceImpl(JpaRepository<Term, Long> repository, TermRepository termRepository) {
        super(repository);
        this.termRepository = termRepository;
    }

    @Override
    protected void prePersist(Term term) {
        LocalDate now = LocalDate.now();
        if (term.getStartDate() == null || term.getEndDate() == null) {
            throw new IllegalArgumentException(NOT_BE_NULL);
        }

        if (!term.getStartDate().isAfter(now)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "start"));
        }

        if (!term.getEndDate().isAfter(now)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "end"));
        }

        if (!term.getStartDate().isBefore(term.getEndDate())) {
            throw new IllegalArgumentException(TIME_ILLEGAL);
        }
        term.setSemester(SemesterUtil.currentSemester());
    }

    @Override
    protected void preUpdate(Term term) {
        LocalDate now = LocalDate.now();
        if (!term.getStartDate().isAfter(now)) {
            throw new AccessDeniedException(String.format(ILLEGAL_AFTER_START, "update"));
        }
    }

    @Override
    protected void preDelete(Long aLong) {
        Term term = termRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "term")));
        LocalDate now = LocalDate.now();
        if (!term.getStartDate().isAfter(now)) {
            throw new AccessDeniedException(String.format(ILLEGAL_AFTER_START, "delete"));
        }
    }
}
