package ir.lms.util.mapper;

import ir.lms.model.ExamTemplate;
import ir.lms.util.mapper.base.BaseMapper;
import ir.lms.util.dto.exam.ExamDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamMapper extends BaseMapper<ExamTemplate, ExamDTO> {
}
