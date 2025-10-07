package ir.lms.service.impl;

import ir.lms.model.*;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.OptionRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.service.AnswerService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.AnswerCacheService;
import ir.lms.util.AnswerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AnswerServiceImpl extends BaseServiceImpl<Answer, Long> implements AnswerService {

    private final AnswerFactory answerFactory;
    private final OptionRepository optionRepository;

    protected AnswerServiceImpl(JpaRepository<Answer, Long> repository,
                                AnswerFactory answerFactory, OptionRepository optionRepository) {
        super(repository);
        this.answerFactory = answerFactory;
        this.optionRepository = optionRepository;
    }


    @Override
    public void saveAnswer(String type,Answer answer, Option option, String answerText) {
        Answer answerForSave = answerFactory.createAnswer(type , answer, option, answerText);
        this.persist(answerForSave);
    }

    @Override
    public Option findOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("option not found"));
    }

    @Override
    public Answer update(Long aLong, Answer answer) {
        return null;
    }
}
