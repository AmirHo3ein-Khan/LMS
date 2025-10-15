package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.AcademicCalender;
import ir.lms.model.Term;
import ir.lms.repository.TermRepository;
import ir.lms.service.TermService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.SemesterUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TermServiceImpl extends BaseServiceImpl<Term, Long> implements TermService {

    private final static String YEAR_ILLEGAL = "Term year must be in future";
    private final static String ILLEGAL_AFTER_START = "Can't %s term after start date!";
    private final static String NOT_FOUND = "%s not found!";
    private final static String ALREADY_EXISTS = "Term already exists in major in %s semester!";


    private final TermRepository termRepository;

    protected TermServiceImpl(JpaRepository<Term, Long> repository, TermRepository termRepository) {
        super(repository);
        this.termRepository = termRepository;
    }

    @Override
    protected void prePersist(Term term) {
        LocalDate now = LocalDate.now();

        term.setYear(term.getAcademicCalender().getCourseRegistrationStart().getYear());

        if (term.getYear() < now.getYear()) {
            throw new IllegalArgumentException(YEAR_ILLEGAL);
        }

        term.setSemester(SemesterUtil.currentSemester());
        if (termRepository.existsBySemesterAndMajor(term.getSemester(), term.getMajor())
                && term.getYear() == now.getYear()) {
            throw new IllegalArgumentException(String.format(ALREADY_EXISTS, term.getSemester()));
        }
        term.setDeleted(false);
    }

    @Override
    public void delete(Long aLong) {
        Term term = termRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "term")));
        LocalDate now = LocalDate.now();
        if (!term.getAcademicCalender().getCourseRegistrationStart().isAfter(now)) {
            throw new AccessDeniedException(String.format(ILLEGAL_AFTER_START, "delete"));
        }
        term.setDeleted(true);
    }

    @Override
    public Term findById(Long aLong) {
        Term term = termRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "term")));
        if (term.isDeleted()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "term"));
        }
        return term;
    }

    @Override
    public List<Term> findAll() {
        List<Term> terms = termRepository.findAll();
        List<Term> result = new ArrayList<>();
        for (Term term : terms) {
            if (!term.isDeleted()) {
                result.add(term);
            }
        }
        return result;
    }

    @Override
    public Term update(Long aLong, Term term) {
        Term foundedTerm = termRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "term")));
        LocalDate now = LocalDate.now();
        if (!foundedTerm.getAcademicCalender().getCourseRegistrationStart().isAfter(now)) {
            throw new AccessDeniedException(String.format(ILLEGAL_AFTER_START, "update"));
        }
        if (term.isDeleted()){
            throw new EntityNotFoundException(String.format(NOT_FOUND, "term"));
        }
        foundedTerm.setYear(term.getYear());
        foundedTerm.setSemester(term.getSemester());
        return termRepository.save(foundedTerm);
    }

    @Override
    public AcademicCalender findTermCalenderByTermId(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "term")));
        return term.getAcademicCalender();
    }
}
