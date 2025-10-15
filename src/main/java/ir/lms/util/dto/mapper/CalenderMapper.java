package ir.lms.util.dto.mapper;

import ir.lms.model.AcademicCalender;
import ir.lms.util.dto.AcademicCalenderDTO;
import ir.lms.util.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CalenderMapper extends BaseMapper<AcademicCalender , AcademicCalenderDTO> {
}
