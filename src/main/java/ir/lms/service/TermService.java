package ir.lms.service;

import ir.lms.model.AcademicCalender;
import ir.lms.model.Term;
import ir.lms.service.base.BaseService;

public interface TermService extends BaseService<Term, Long> {
    AcademicCalender findTermCalenderByTermId(Long termId);
}
