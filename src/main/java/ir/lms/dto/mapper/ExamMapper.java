package ir.lms.dto.mapper;

import ir.lms.model.ExamTemplate;
import ir.lms.dto.mapper.base.BaseMapper;
import ir.lms.dto.exam.ExamDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamMapper extends BaseMapper<ExamTemplate, ExamDTO> {
}
