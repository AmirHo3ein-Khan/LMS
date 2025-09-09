package ir.lms.service.impl;

import ir.lms.model.ExamTemplate;
import ir.lms.repository.ExamRepository;
import ir.lms.service.ExamService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.dto.exam.ExamDTO;
import ir.lms.util.dto.mapper.ExamMapper;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl extends BaseServiceImpl<ExamTemplate, ExamDTO , Long> implements ExamService {

    protected ExamServiceImpl(ExamRepository repository, ExamMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected ExamTemplate updateEntity(ExamTemplate entity, ExamDTO examDTO) {
        entity.setTitle(examDTO.getTitle());
        entity.setDescription(examDTO.getDescription());
        entity.setExamTime(examDTO.getExamTime());
        entity.setExamStartTime(examDTO.getExamStartTime());
        entity.setExamEndTime(examDTO.getExamEndTime());
        return entity;
    }
}
